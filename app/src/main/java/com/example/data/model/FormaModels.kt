package com.example.data.model

import java.util.UUID

enum class PrimaryGoal(val label: String, val description: String) {
    HYPERTROPHY("Hipertrofia", "Ganho de massa muscular com estímulo hipertrófico e volume ótimo"),
    WEIGHT_LOSS("Emagrecimento & Definição", "Queima de gordura preservando massa magra e densidade"),
    STRENGTH("Força Máxima", "Desenvolvimento neural e ganhos de força nos levantamentos fundamentais"),
    CONDITIONING("Condicionamento & Saúde", "Capacidade cardiorrespiratória, mobilidade e disposição diária")
}

enum class ExperienceLevel(val label: String, val description: String) {
    BEGINNER("Iniciante", "Menos de 6 meses de treino consistente"),
    INTERMEDIATE("Intermediário", "6 meses a 2 anos treinando regularmente"),
    ADVANCED("Avançado", "Mais de 2 anos de experiência e boa técnica")
}

enum class WorkoutLocation(val label: String) {
    COMMERCIAL_GYM("Academia Completa"),
    BUILDING_GYM("Academia de Prédio/Condomínio"),
    HOME_DUMBBELLS("Treino em Casa (Halteres/Elásticos)"),
    BODYWEIGHT("Calistenia / Peso Corporal")
}

data class AnamneseData(
    val primaryGoal: PrimaryGoal = PrimaryGoal.HYPERTROPHY,
    val secondaryGoals: List<String> = emptyList(),
    val experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val daysPerWeek: Int = 4,
    val sessionDurationMinutes: Int = 60,
    val location: WorkoutLocation = WorkoutLocation.COMMERCIAL_GYM,
    val exercisesToAvoid: String = "",
    val physicalLimitations: String = "",
    val hasSharpPainOrInjury: Boolean = false,
    val mainHurdles: String = ""
)

data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int = 90,
    val suggestedWeightKg: Double = 0.0,
    val executionTips: String = "",
    val alternatives: List<String> = emptyList(),
    val reasoning: String = ""
)

data class WorkoutSessionDay(
    val id: String = UUID.randomUUID().toString(),
    val dayLetter: String, // "A", "B", "C", etc.
    val title: String, // "Treino A - Peito, Ombros e Tríceps"
    val muscleGroups: List<String>,
    val exercises: List<Exercise>
)

data class WorkoutPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val objective: String,
    val weeklySplit: String,
    val days: List<WorkoutSessionDay>,
    val progressionGuidelines: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ExerciseSetRecord(
    val setNumber: Int,
    var weightKg: Double = 0.0,
    var repsDone: Int = 0,
    var isCompleted: Boolean = false
)

data class ActiveExerciseState(
    val exercise: Exercise,
    val sets: MutableList<ExerciseSetRecord>
)

data class CompletedWorkoutSummary(
    val id: Long = 0,
    val planName: String,
    val sessionTitle: String,
    val dayLetter: String,
    val timestamp: Long,
    val durationMinutes: Int,
    val totalVolumeKg: Double,
    val completedExercisesCount: Int,
    val notes: String
)

data class CheckInSubmission(
    val consistencyDaysDone: Int,
    val plannedDays: Int,
    val fatigueLevel: Int, // 1 to 10
    val sorenessRating: Int, // 1 to 10
    val perceivedEffortRpe: Int, // 1 to 10
    val userNotes: String
)

data class CheckInAiFeedback(
    val diagnosis: String,
    val suggestedAdjustments: String,
    val requiresPlanUpdate: Boolean = false
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
