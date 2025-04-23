package com.jbg.gil.data.remote

import com.jbg.gil.data.remote.model.RegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GilApi {

    @POST("registerUser") //Complement de la URL del API
    suspend fun registerUser(@Body request: RegisterDto): Response<RegisterDto>

}