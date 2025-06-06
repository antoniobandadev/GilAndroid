package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.EventGuestDto
import com.jbg.gil.core.data.remote.dtos.GuestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GuestApi {

    @POST("guests/newGuest")
    suspend fun newGuest(
        @Body guest: GuestDto
    ): Response<GuestDto>

    @GET("guests/myGuestCF")
    suspend fun myGuestCF(
        @Query("eventId")
        eventId:String
    ): Response<List<ContactDto>>

    @GET("guests/myGuestInvite")
    suspend fun myGuestInvite(
        @Query("eventId")
        eventId:String,
        @Query("userId")
        userId:String
    ): Response<List<EventGuestDto>>

    @FormUrlEncoded
    @POST("guests/checkMyGuest")
    suspend fun checkGuest(
        @Field("guestId")
        guestId:String
    ): Response<GuestDto>
}