package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.EventDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

interface EventApi {

    @Multipart  //Para que lo pueda enviar por partes
    @Streaming  //No usa toda la ram
    @POST("events/newEvent")
    suspend fun newEvent(
        @Part eventImage: MultipartBody.Part?,
        @Part("eventName") eventName: RequestBody,
        @Part("eventDesc") eventDesc: RequestBody,
        @Part("eventType") eventType: RequestBody,
        @Part("eventDateStart") eventDateStart: RequestBody,
        @Part("eventDateEnd") eventDateEnd: RequestBody,
        @Part("eventStreet") eventStreet: RequestBody,
        @Part("eventCity") eventCity: RequestBody,
        @Part("eventStatus") eventStatus: RequestBody?,
        @Part("userId") userId: RequestBody
    ) : Response<BasicResponse>

}