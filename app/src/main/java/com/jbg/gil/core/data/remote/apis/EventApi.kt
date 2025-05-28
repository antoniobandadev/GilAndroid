package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.EventDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EventApi {

    @POST("events/newEvent")
    suspend fun newEvent(@Body event: EventDto) : Response<EventDto>

}