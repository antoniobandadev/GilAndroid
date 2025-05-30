package com.jbg.gil.core.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.utils.Constants

@Dao
interface EventDao {

    @Query("SELECT * FROM ${Constants.DATABASE_EVENTS_TABLE} WHERE eventStatus = 'A' ORDER BY eventDateStart DESC")
    suspend fun getAllEventsDB() : List<EventEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_EVENTS_TABLE} WHERE eventSync = 0 ")
    suspend fun getSyncEvents() : List<EventEntity>

    @Query("UPDATE ${Constants.DATABASE_EVENTS_TABLE} SET eventSync = 1, eventImg= :eventImg WHERE eventId = :eventId")
    suspend fun updateSyncEvent(eventId: String, eventImg: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM ${Constants.DATABASE_EVENTS_TABLE}")
    suspend fun deleteAllEventsDB()

}