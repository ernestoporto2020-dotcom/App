package com.example.data.repository

import android.content.Context
import com.example.data.local.CheckInEntity
import com.example.data.local.ExerciseLoadHistoryEntity
import com.example.data.local.FormaDatabase
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.AnamneseData
import com.example.data.model.ChatMessage
import com.example.data.model.CheckInSubmission
import com.example.data.model.ExperienceLevel
import com.example.data.model.PrimaryGoal
import com.example.data.model.WorkoutLocation
import com.example.data.model.WorkoutPlan
import com.example.data.network.GeminiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class FormaRepository(context: Context) {
    private val db = FormaDatabase.getInstance(context)
    private val userProfileDao = db.userProfileDao()
    private val workoutLogDao = db.workoutLogDao()
    private val exerciseLoadDao = db.exerciseLoadDao()
    private val checkInDao = db.checkInDao()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val anamneseAdapter = moshi.adapter(AnamneseData::class.java)
    private val planAdapter = moshi.adapter(WorkoutPlan::class.java)

    val userProfileFlow: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()
    val allWorkoutLogsFlow: Flow<List<WorkoutLogEntity>> = workoutLogDao.getAllWorkoutLogs()
    val totalSessionsCountFlow: Flow<Int> = workoutLogDao.getTotalSessionsCount()
    val totalVolumeFlow: Flow<Double?> = workoutLogDao.getTotalVolumeLifted()
    val allCheckInsFlow: Flow<List<CheckInEntity>> = checkInDao.getAllCheckIns()
    val recentLoadsFlow: Flow<List<ExerciseLoadHistoryEntity>> = exerciseLoadDao.getRecentLoadHistory()

    fun getExerciseHistoryFlow(exerciseName: String): Flow<List<ExerciseLoadHistoryEntity>> {
        return exerciseLoadDao.getLoadProgressionForExercise(exerciseName)
    }

    suspend fun saveAnamneseAndGeneratePlan(
        name: String,
        email: String,
        anamnese: AnamneseData
    ): WorkoutPlan {
        val plan = GeminiClient.generateWorkoutPlan(anamnese)
        val anamneseJson = anamneseAdapter.toJson(anamnese)
        val planJson = planAdapter.toJson(plan)

        val profile = UserProfileEntity(
            id = 1,
            name = name.ifBlank { "Atleta FormaAI" },
            email = email.ifBlank { "usuario@formaai.app" },
            anamneseJson = anamneseJson,
            activePlanJson = planJson,
            hasCompletedOnboarding = true
        )
        userProfileDao.saveUserProfile(profile)
        return plan
    }

    suspend fun updateActivePlan(plan: WorkoutPlan) {
        val currentProfile = userProfileDao.getUserProfileOnce()
        if (currentProfile != null) {
            val planJson = planAdapter.toJson(plan)
            userProfileDao.saveUserProfile(currentProfile.copy(activePlanJson = planJson))
        }
    }

    fun parseAnamnese(json: String?): AnamneseData? {
        if (json.isNullOrBlank()) return null
        return try {
            anamneseAdapter.fromJson(json)
        } catch (_: Exception) {
            null
        }
    }

    fun parseWorkoutPlan(json: String?): WorkoutPlan? {
        if (json.isNullOrBlank()) return null
        return try {
            planAdapter.fromJson(json)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveCompletedWorkout(
        sessionTitle: String,
        dayLetter: String,
        durationMinutes: Int,
        totalVolumeKg: Double,
        exercisesSummaryJson: String,
        notes: String,
        loads: List<ExerciseLoadHistoryEntity>
    ) {
        val log = WorkoutLogEntity(
            sessionTitle = sessionTitle,
            dayLetter = dayLetter,
            durationMinutes = durationMinutes,
            totalVolumeKg = totalVolumeKg,
            exercisesSummaryJson = exercisesSummaryJson,
            notes = notes
        )
        workoutLogDao.insertLog(log)

        if (loads.isNotEmpty()) {
            exerciseLoadDao.insertLoads(loads)
        }
    }

    suspend fun submitCheckIn(submission: CheckInSubmission): Pair<String, String> {
        val profile = userProfileDao.getUserProfileOnce()
        val anamnese = parseAnamnese(profile?.anamneseJson)
        val plan = parseWorkoutPlan(profile?.activePlanJson)

        val (diagnosis, adjustment) = GeminiClient.analyzeWeeklyCheckIn(submission, anamnese, plan)

        val checkInEntity = CheckInEntity(
            daysDone = submission.consistencyDaysDone,
            plannedDays = submission.plannedDays,
            fatigueLevel = submission.fatigueLevel,
            sorenessRating = submission.sorenessRating,
            effortRpe = submission.perceivedEffortRpe,
            userNotes = submission.userNotes,
            aiDiagnosis = "$diagnosis\n\nAjuste Sugerido: $adjustment"
        )
        checkInDao.insertCheckIn(checkInEntity)

        return Pair(diagnosis, adjustment)
    }

    suspend fun askCoach(userMessage: String, history: List<ChatMessage>): String {
        val profile = userProfileDao.getUserProfileOnce()
        val anamnese = parseAnamnese(profile?.anamneseJson)
        val plan = parseWorkoutPlan(profile?.activePlanJson)

        val recentLoads = exerciseLoadDao.getRecentLoadHistory().firstOrNull() ?: emptyList()
        val loadsSummary = if (recentLoads.isNotEmpty()) {
            recentLoads.take(5).joinToString(", ") { "${it.exerciseName}: ${it.weightKg}kg (${it.reps} reps)" }
        } else {
            "Nenhuma carga registrada ainda"
        }

        return GeminiClient.askAiCoach(userMessage, anamnese, plan, loadsSummary, history)
    }

    suspend fun clearAllData() {
        userProfileDao.clearProfile()
        workoutLogDao.clearLogs()
        exerciseLoadDao.clearHistory()
        checkInDao.clearCheckIns()
    }
}
