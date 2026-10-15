package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val email: String,
    val anamneseJson: String,
    val activePlanJson: String?,
    val hasCompletedOnboarding: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionTitle: String,
    val dayLetter: String,
    val durationMinutes: Int,
    val totalVolumeKg: Double,
    val exercisesSummaryJson: String,
    val notes: String
)

@Entity(tableName = "exercise_load_history")
data class ExerciseLoadHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseName: String,
    val date: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val reps: Int,
    val estimatedOneRm: Double
)

@Entity(tableName = "check_in_history")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val daysDone: Int,
    val plannedDays: Int,
    val fatigueLevel: Int,
    val sorenessRating: Int,
    val effortRpe: Int,
    val userNotes: String,
    val aiDiagnosis: String
)
