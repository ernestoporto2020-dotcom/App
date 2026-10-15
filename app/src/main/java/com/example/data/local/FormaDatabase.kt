package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutLogEntity::class,
        ExerciseLoadHistoryEntity::class,
        CheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FormaDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun exerciseLoadDao(): ExerciseLoadDao
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: FormaDatabase? = null

        fun getInstance(context: Context): FormaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FormaDatabase::class.java,
                    "formaai_local_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
