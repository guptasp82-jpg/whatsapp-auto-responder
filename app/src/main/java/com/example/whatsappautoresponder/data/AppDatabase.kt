package com.example.whatsappautoresponder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CallRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callRecordDao(): CallRecordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auto_responder_db"
                ).build()
                instance = newInst
                newInst
            }
        }
    }
}
