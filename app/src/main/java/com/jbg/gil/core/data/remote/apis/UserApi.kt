package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.PassUserDto
import com.jbg.gil.core.data.remote.dtos.SolPassDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi {

    @POST("users/newUser") //Complement de la URL del API
    suspend fun registerUser(@Body request: UserDto): Response<UserDto>

    @POST("users/exists")
    suspend fun existsUser(@Body request: UserDto) : Response<BasicResponse>

    @POST("users/login")
    suspend fun loginUser(@Body request: UserDto) : Response<UserDto>

    @POST("users/forgotPass")
    suspend fun forgotPass(@Body userEmail: SolPassDto): Response<PassUserDto>

    @PUT("users/updatePass")
    suspend fun updatePass(@Body user: UserDto): Response<BasicResponse>

}