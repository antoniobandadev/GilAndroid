package com.jbg.gil.core.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.utils.Constants

@Dao
interface EventDao {

    @Query("SELECT * FROM ${Constants.DATABASE_EVENTS_TABLE} WHERE eventSync = 0 ")
    suspend fun getSyncEvents() : List<EventEntity>

    @Query("UPDATE ${Constants.DATABASE_EVENTS_TABLE} SET eventSync = 1 WHERE eventId = :eventId")
    suspend fun updateSyncEvent(eventId: String)

    @Insert
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert
    suspend fun insertEvent(event: EventEntity)

}