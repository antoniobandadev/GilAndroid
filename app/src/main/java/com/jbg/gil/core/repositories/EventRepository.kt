package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.remote.apis.EventApi
import com.jbg.gil.core.data.remote.apis.UserApi
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import retrofit2.Response
import javax.inject.Inject

class EventRepository @Inject constructor (private val eventApi: EventApi) {

    suspend fun newEvent(event: EventDto): Response<EventDto> = eventApi.newEvent(event)


}