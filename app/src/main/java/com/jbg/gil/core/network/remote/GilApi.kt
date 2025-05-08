package com.jbg.gil.core.network.remote

import com.jbg.gil.core.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GilApi {

    @POST("user") //Complement de la URL del API
    suspend fun registerUser(@Body request: UserDto): Response<UserDto>

}