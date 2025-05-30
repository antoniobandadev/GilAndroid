package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.daos.EventDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.data.remote.apis.EventApi
import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.datastore.UserPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class EventRepository @Inject constructor (private val eventApi: EventApi,
                                           private val eventDao: EventDao,
                                           private var userPreferences: UserPreferences
) {

    //suspend fun newEvent(event: EventDto): Response<EventDto> = eventApi.newEvent(event)
    suspend fun uploadEvent(
        eventImage: MultipartBody.Part?,
        eventName: RequestBody,
        eventDesc: RequestBody,
        eventType: RequestBody,
        eventDateStart: RequestBody,
        eventDateEnd: RequestBody,
        eventStreet: RequestBody,
        eventCity: RequestBody,
        eventStatus: RequestBody?,
        userId: RequestBody

    ): Response<EventDto> = eventApi.newEvent(
        eventImage,
        eventName,
        eventDesc,
        eventType,
        eventDateStart,
        eventDateEnd,
        eventStreet,
        eventCity,
        eventStatus,
        userId
    )

    suspend fun getAllEventsApi(userId: String)= eventApi.getAllEventsApi(userId)

    //DAO
    suspend fun insertEventsDB(events : List<EventEntity>) =  eventDao.insertEvents(events)
    suspend fun insertEventDB(event : EventEntity) =  eventDao.insertEvent(event)
    suspend fun getEventSyncDB() =  eventDao.getSyncEvents()
    suspend fun updateEventSyncDB(eventId : String, eventImg: String) =  eventDao.updateSyncEvent(eventId, eventImg)
    suspend fun getAllEventsDB() = eventDao.getAllEventsDB()
    suspend fun deleteAllEventsDB() = eventDao.deleteAllEventsDB()

}