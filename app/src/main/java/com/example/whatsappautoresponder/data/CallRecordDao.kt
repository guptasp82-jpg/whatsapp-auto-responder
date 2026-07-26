package com.example.whatsappautoresponder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallRecordDao {
    @Insert
    suspend fun insertCall(record: CallRecord)

    @Query("SELECT COUNT(*) FROM call_records WHERE phoneNumber = :number AND timestamp >= :sevenDaysAgoThreshold")
    suspend fun getCallsInLast7Days(number: String, sevenDaysAgoThreshold: Long): Int
}
