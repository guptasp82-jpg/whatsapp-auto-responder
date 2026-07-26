package com.example.whatsappautoresponder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_records")
data class CallRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val callType: String
)
