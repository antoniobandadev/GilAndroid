package com.jbg.gil.core.data.remote.apis

import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.PassUserDto
import com.jbg.gil.core.data.remote.dtos.SolPassDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Streaming

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

    @Multipart  //Para que lo pueda enviar por partes
    @Streaming  //No usa toda la ram
    @POST("users/updateProfile")
    suspend fun updateProfile(
        @Part userProfile: MultipartBody.Part?,
        @Part("userId") userId: RequestBody
    ) : Response<UserDto>

}