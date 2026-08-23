package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.TripEntity

@Database(entities = [TripEntity::class], version = 1, exportSchema = false)
@TypeConverters(YatraTypeConverters::class)
abstract class YatraDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: YatraDatabase? = null

        fun getDatabase(context: Context): YatraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YatraDatabase::class.java,
                    "yatra_mobility_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
