package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.local.db.daos.EventDao
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.data.remote.apis.EventApi
import com.jbg.gil.core.data.remote.dtos.EventDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class EventRepository @Inject constructor (private val eventApi: EventApi,
                                           private val eventDao: EventDao
) {

    //suspend fun newEvent(event: EventDto): Response<EventDto> = eventApi.newEvent(event)
    suspend fun uploadEvent(
        eventImage: MultipartBody.Part?,
        eventId: RequestBody,
        eventName: RequestBody,
        eventDesc: RequestBody,
        eventType: RequestBody,
        eventDateStart: RequestBody,
        eventDateEnd: RequestBody,
        eventStreet: RequestBody,
        eventCity: RequestBody,
        eventStatus: RequestBody?,
        userId: RequestBody,
        userIdScan : RequestBody

    ): Response<EventDto> = eventApi.newEvent(
        eventImage,
        eventId,
        eventName,
        eventDesc,
        eventType,
        eventDateStart,
        eventDateEnd,
        eventStreet,
        eventCity,
        eventStatus,
        userId,
        userIdScan
    )

    suspend fun updateEvent(
        eventImage: MultipartBody.Part?,
        eventId: RequestBody,
        eventName: RequestBody,
        eventDesc: RequestBody,
        eventType: RequestBody,
        eventDateStart: RequestBody,
        eventDateEnd: RequestBody,
        eventStreet: RequestBody,
        eventCity: RequestBody,
        eventStatus: RequestBody?,
        userId: RequestBody,
        userIdScan: RequestBody,
        changeImage: RequestBody

    ): Response<EventDto> = eventApi.updateEvent(
        eventImage,
        eventId,
        eventName,
        eventDesc,
        eventType,
        eventDateStart,
        eventDateEnd,
        eventStreet,
        eventCity,
        eventStatus,
        userId,
        userIdScan,
        changeImage
    )

    suspend fun getAllEventsApi(userId: String)= eventApi.getAllEventsApi(userId)
    suspend fun deleteEventApi(event : EventDto) = eventApi.cancelEvent(event)

    suspend fun getAllEventsInviteApi(userId: String)= eventApi.getAllEventsInviteApi(userId)
    suspend fun getAllEventsScanApi(userId: String) = eventApi.getAllEventsScan(userId)

    //DAO
    suspend fun insertEventsDB(events : List<EventEntity>) =  eventDao.insertEvents(events)
    suspend fun insertEventDB(event : EventEntity) =  eventDao.insertEvent(event)
    suspend fun getEventSyncDB() =  eventDao.getSyncEvents()
    suspend fun getAllEventsDB() = eventDao.getAllEventsDB()
    suspend fun getEvent(eventId: String) = eventDao.getEventDB(eventId)
    suspend fun updateEventSyncDB(eventId : String, eventImg: String) =  eventDao.updateSyncEvent(eventId, eventImg)
    suspend fun updateEvent(event: EventEntity) = eventDao.updateEvent(event)
    suspend fun deleteAllEventsDB() = eventDao.deleteAllEventsDB()
    suspend fun getEventsDelete() = eventDao.getSyncEventsDelete()
    suspend fun updateEventDelete(eventId: String) = eventDao.deleteEventNoConnection(eventId)
    suspend fun deleteEventDB(eventId: String) = eventDao.deleteEvent(eventId)

}