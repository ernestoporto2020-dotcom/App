package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WorkoutLogEntity): Long

    @Query("SELECT COUNT(*) FROM workout_logs")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT SUM(totalVolumeKg) FROM workout_logs")
    fun getTotalVolumeLifted(): Flow<Double?>

    @Query("DELETE FROM workout_logs")
    suspend fun clearLogs()
}

@Dao
interface ExerciseLoadDao {
    @Query("SELECT * FROM exercise_load_history WHERE exerciseName = :name ORDER BY date ASC")
    fun getLoadProgressionForExercise(name: String): Flow<List<ExerciseLoadHistoryEntity>>

    @Query("SELECT * FROM exercise_load_history ORDER BY date DESC LIMIT 100")
    fun getRecentLoadHistory(): Flow<List<ExerciseLoadHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoads(loads: List<ExerciseLoadHistoryEntity>)

    @Query("DELETE FROM exercise_load_history")
    suspend fun clearHistory()
}

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_in_history ORDER BY date DESC")
    fun getAllCheckIns(): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Query("DELETE FROM check_in_history")
    suspend fun clearCheckIns()
}
