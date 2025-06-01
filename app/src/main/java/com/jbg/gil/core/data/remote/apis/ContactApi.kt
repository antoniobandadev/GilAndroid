package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.AddFriendDto
import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.RespFriendDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ContactApi {

    @GET("contacts/contacts")
    suspend fun getContacts(
        @Query("userId")
        userId: String
    ) : Response<List<ContactDto>>

    @GET("contacts/friends")
    suspend fun getFriendsToContacts(
        @Query("userId")
        userId: String
    ) : Response<List<ContactDto>>

    @POST("contacts/newContacts")
    suspend fun newContact(@Body contact: ContactDto) : Response<BasicResponse>

    @PUT("contacts/updateContact")
    suspend fun updateContact(
        @Body contact: ContactDto
    ) : Response<BasicResponse>

    @PUT("contacts/deleteContact")
    suspend fun deleteContact(
        @Body contact: ContactDto
    ) : Response<BasicResponse>

    @GET("friends/friends")
    suspend fun getFriends(
        @Query("userId")
        userId: String,
        @Query("friendStatus")
        friendStatus: String
    ) : Response<List<ContactDto>>

    @POST("friends/newFriend")
    suspend fun addFriend(@Body friend: AddFriendDto) : Response<BasicResponse>

    @PUT("friends/solFriend")
    suspend fun responseSol(@Body friend: RespFriendDto) : Response<BasicResponse>

}