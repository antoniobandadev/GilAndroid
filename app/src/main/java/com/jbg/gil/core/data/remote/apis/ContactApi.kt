package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.ContactDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ContactApi {

    @GET("contacts/getContacts/{userId}")
    suspend fun getContacts(
        @Path("userId")
        userId: String
    ) : List<ContactDto>



}