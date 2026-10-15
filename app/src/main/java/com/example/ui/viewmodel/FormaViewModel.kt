package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CheckInEntity
import com.example.data.local.ExerciseLoadHistoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.ActiveExerciseState
import com.example.data.model.AnamneseData
import com.example.data.model.ChatMessage
import com.example.data.model.CheckInSubmission
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetRecord
import com.example.data.model.ExperienceLevel
import com.example.data.model.PrimaryGoal
import com.example.data.model.WorkoutLocation
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionDay
import com.example.data.repository.FormaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FormaScreen {
    LANDING,
    AUTH,
    ANAMNESE,
    DASHBOARD,
    WORKOUT_SHEET,
    ACTIVE_WORKOUT,
    HISTORY,
    COACH_CHAT,
    CHECK_IN,
    PROFILE
}

class FormaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FormaRepository(application)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workoutLogs: StateFlow<List<WorkoutLogEntity>> = repository.allWorkoutLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSessions: StateFlow<Int> = repository.totalSessionsCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalVolume: StateFlow<Double?> = repository.totalVolumeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val checkIns: StateFlow<List<CheckInEntity>> = repository.allCheckInsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLoads: StateFlow<List<ExerciseLoadHistoryEntity>> = repository.recentLoadsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active screen navigation
    private val _currentScreen = MutableStateFlow(FormaScreen.LANDING)
    val currentScreen: StateFlow<FormaScreen> = _currentScreen.asStateFlow()

    // Anamnese Form State
    private val _anamneseStep = MutableStateFlow(1)
    val anamneseStep: StateFlow<Int> = _anamneseStep.asStateFlow()

    private val _anamneseData = MutableStateFlow(AnamneseData())
    val anamneseData: StateFlow<AnamneseData> = _anamneseData.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    private val _safetyBlockedMessage = MutableStateFlow<String?>(null)
    val safetyBlockedMessage: StateFlow<String?> = _safetyBlockedMessage.asStateFlow()

    // Active Workout State
    private val _activeSessionDay = MutableStateFlow<WorkoutSessionDay?>(null)
    val activeSessionDay: StateFlow<WorkoutSessionDay?> = _activeSessionDay.asStateFlow()

    private val _activeExercises = MutableStateFlow<List<ActiveExerciseState>>(emptyList())
    val activeExercises: StateFlow<List<ActiveExerciseState>> = _activeExercises.asStateFlow()

    private val _workoutNotes = MutableStateFlow("")
    val workoutNotes: StateFlow<String> = _workoutNotes.asStateFlow()

    private val _workoutStartTime = MutableStateFlow(0L)
    val workoutStartTime: StateFlow<Long> = _workoutStartTime.asStateFlow()

    // Rest Timer
    private val _restSecondsRemaining = MutableStateFlow(0)
    val restSecondsRemaining: StateFlow<Int> = _restSecondsRemaining.asStateFlow()

    private val _restTimerTotal = MutableStateFlow(90)
    val restTimerTotal: StateFlow<Int> = _restTimerTotal.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Coach Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isFromUser = false,
                text = "Olá! Eu sou o seu Coach FormaAI. Posso tirar dúvidas sobre a sua ficha, sugerir exercícios alternativos se um aparelho estiver ocupado, conferir suas cargas anteriores ou analisar sua evolução. Como posso te ajudar hoje?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isCoachTyping = MutableStateFlow(false)
    val isCoachTyping: StateFlow<Boolean> = _isCoachTyping.asStateFlow()

    // CheckIn submission state
    private val _isCheckInSubmitting = MutableStateFlow(false)
    val isCheckInSubmitting: StateFlow<Boolean> = _isCheckInSubmitting.asStateFlow()

    private val _lastCheckInFeedback = MutableStateFlow<Pair<String, String>?>(null)
    val lastCheckInFeedback: StateFlow<Pair<String, String>?> = _lastCheckInFeedback.asStateFlow()

    init {
        // Observe profile to route to DASHBOARD if already onboarded
        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile != null && profile.hasCompletedOnboarding) {
                    if (_currentScreen.value == FormaScreen.LANDING || _currentScreen.value == FormaScreen.AUTH || _currentScreen.value == FormaScreen.ANAMNESE) {
                        _currentScreen.value = FormaScreen.DASHBOARD
                    }
                }
            }
        }
    }

    fun navigateTo(screen: FormaScreen) {
        _currentScreen.value = screen
    }

    fun setUserName(name: String) { _userName.value = name }
    fun setUserEmail(email: String) { _userEmail.value = email }

    fun updateAnamnese(update: AnamneseData.() -> AnamneseData) {
        _anamneseData.value = _anamneseData.value.update()
    }

    fun nextAnamneseStep() {
        val current = _anamneseData.value

        // Clinical Safety check on Step 3 or 4
        if (_anamneseStep.value == 4) {
            if (current.hasSharpPainOrInjury) {
                _safetyBlockedMessage.value = "Identificamos que você relatou dor aguda ou lesão recente ativa. Por segurança biomecânica e saúde clínica, o FormaAI suspende a geração de treinos automáticos de alta intensidade. Recomendamos fortemente a consulta a um médico ortopedista ou fisioterapeuta antes de iniciar."
                return
            } else {
                _safetyBlockedMessage.value = null
            }
        }

        if (_anamneseStep.value < 5) {
            _anamneseStep.value += 1
        } else {
            submitAnamnese()
        }
    }

    fun prevAnamneseStep() {
        if (_anamneseStep.value > 1) {
            _anamneseStep.value -= 1
        } else {
            _currentScreen.value = FormaScreen.LANDING
        }
    }

    fun dismissSafetyWarning() {
        _safetyBlockedMessage.value = null
    }

    private fun submitAnamnese() {
        viewModelScope.launch {
            _isGeneratingPlan.value = true
            try {
                repository.saveAnamneseAndGeneratePlan(
                    name = _userName.value.ifBlank { "Atleta" },
                    email = _userEmail.value.ifBlank { "atleta@formaai.app" },
                    anamnese = _anamneseData.value
                )
                _currentScreen.value = FormaScreen.DASHBOARD
            } catch (e: Exception) {
                // Handled gracefully in repo
                _currentScreen.value = FormaScreen.DASHBOARD
            } finally {
                _isGeneratingPlan.value = false
            }
        }
    }

    fun getActivePlan(): WorkoutPlan? {
        val json = userProfile.value?.activePlanJson
        return repository.parseWorkoutPlan(json)
    }

    fun getAnamnese(): AnamneseData? {
        val json = userProfile.value?.anamneseJson
        return repository.parseAnamnese(json)
    }

    // Active Workout Flow
    fun startWorkoutSession(day: WorkoutSessionDay) {
        _activeSessionDay.value = day
        _workoutStartTime.value = System.currentTimeMillis()
        _workoutNotes.value = ""

        val list = day.exercises.map { exercise ->
            val setRecords = (1..exercise.sets).map { setIndex ->
                ExerciseSetRecord(
                    setNumber = setIndex,
                    weightKg = exercise.suggestedWeightKg,
                    repsDone = 10,
                    isCompleted = false
                )
            }.toMutableList()

            ActiveExerciseState(exercise = exercise, sets = setRecords)
        }
        _activeExercises.value = list
        _currentScreen.value = FormaScreen.ACTIVE_WORKOUT
    }

    fun updateSetRecord(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int, isCompleted: Boolean) {
        val currentList = _activeExercises.value.toMutableList()
        if (exerciseIndex in currentList.indices) {
            val ex = currentList[exerciseIndex]
            if (setIndex in ex.sets.indices) {
                val record = ex.sets[setIndex]
                record.weightKg = weight
                record.repsDone = reps
                val wasCompleted = record.isCompleted
                record.isCompleted = isCompleted

                // If set was just marked as completed, trigger rest timer!
                if (!wasCompleted && isCompleted) {
                    startRestTimer(ex.exercise.restSeconds)
                }
            }
            _activeExercises.value = currentList
        }
    }

    fun startRestTimer(seconds: Int) {
        timerJob?.cancel()
        _restTimerTotal.value = seconds
        _restSecondsRemaining.value = seconds
        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            while (_restSecondsRemaining.value > 0) {
                delay(1000)
                _restSecondsRemaining.value -= 1
            }
            _isTimerRunning.value = false
        }
    }

    fun addRestTime(seconds: Int) {
        _restSecondsRemaining.value += seconds
        _restTimerTotal.value = maxOf(_restTimerTotal.value, _restSecondsRemaining.value)
    }

    fun skipRestTimer() {
        timerJob?.cancel()
        _restSecondsRemaining.value = 0
        _isTimerRunning.value = false
    }

    fun setWorkoutNotes(notes: String) {
        _workoutNotes.value = notes
    }

    fun finishWorkoutSession() {
        val session = _activeSessionDay.value ?: return
        val durationMinutes = maxOf(1, ((System.currentTimeMillis() - _workoutStartTime.value) / 60000).toInt())

        var totalVolumeKg = 0.0
        val loadsToRecord = mutableListOf<ExerciseLoadHistoryEntity>()
        val exerciseSummaries = mutableListOf<String>()

        _activeExercises.value.forEach { exState ->
            val completedSets = exState.sets.filter { it.isCompleted }
            if (completedSets.isNotEmpty()) {
                val maxWeight = completedSets.maxOf { it.weightKg }
                val maxReps = completedSets.firstOrNull { it.weightKg == maxWeight }?.repsDone ?: 10
                val volume = completedSets.sumOf { it.weightKg * it.repsDone }
                totalVolumeKg += volume

                // Brzycki 1RM formula: Weight * (36 / (37 - Reps))
                val estOneRm = if (maxReps in 1..36) maxWeight * (36.0 / (37.0 - maxReps)) else maxWeight

                loadsToRecord.add(
                    ExerciseLoadHistoryEntity(
                        exerciseName = exState.exercise.name,
                        weightKg = maxWeight,
                        reps = maxReps,
                        estimatedOneRm = estOneRm
                    )
                )

                exerciseSummaries.add("${exState.exercise.name}: ${completedSets.size} séries (máx ${maxWeight}kg)")
            }
        }

        viewModelScope.launch {
            repository.saveCompletedWorkout(
                sessionTitle = session.title,
                dayLetter = session.dayLetter,
                durationMinutes = durationMinutes,
                totalVolumeKg = totalVolumeKg,
                exercisesSummaryJson = exerciseSummaries.joinToString("; "),
                notes = _workoutNotes.value,
                loads = loadsToRecord
            )
            skipRestTimer()
            _activeSessionDay.value = null
            _currentScreen.value = FormaScreen.HISTORY
        }
    }

    // Coach Chat Flow
    fun sendChatMessage(message: String) {
        if (message.isBlank()) return

        val userMsg = ChatMessage(isFromUser = true, text = message)
        _chatMessages.value = _chatMessages.value + userMsg
        _isCoachTyping.value = true

        viewModelScope.launch {
            val responseText = repository.askCoach(message, _chatMessages.value)
            _chatMessages.value = _chatMessages.value + ChatMessage(isFromUser = false, text = responseText)
            _isCoachTyping.value = false
        }
    }

    // Check-in Flow
    fun submitWeeklyCheckIn(submission: CheckInSubmission) {
        viewModelScope.launch {
            _isCheckInSubmitting.value = true
            try {
                val feedback = repository.submitCheckIn(submission)
                _lastCheckInFeedback.value = feedback
            } finally {
                _isCheckInSubmitting.value = false
            }
        }
    }

    fun resetCheckInFeedback() {
        _lastCheckInFeedback.value = null
    }

    // Profile & Account Management
    fun resetAccountData() {
        viewModelScope.launch {
            repository.clearAllData()
            _currentScreen.value = FormaScreen.LANDING
        }
    }
}
