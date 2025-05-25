package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("user") //Complement de la URL del API
    suspend fun registerUser(@Body request: UserDto): Response<UserDto>


    @POST("user/exists")
    suspend fun existsUser(@Body request: UserDto) : Response<BasicResponse>

    @POST("login")
    suspend fun loginUser(@Body request: UserDto) : Response<UserDto>

}