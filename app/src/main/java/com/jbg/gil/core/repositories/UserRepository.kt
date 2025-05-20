package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.remote.apis.UserApi
import com.jbg.gil.core.data.remote.dtos.UserDto
import retrofit2.Response
import retrofit2.Retrofit

class UserRepository(private val retrofit: Retrofit) {

    private val gilaApi = retrofit.create(UserApi::class.java)

    suspend fun postRegUser(regUser: UserDto): Response<UserDto> = gilaApi.registerUser(regUser)

    suspend fun postLogUser(logUser: UserDto): Response<UserDto> = gilaApi.loginUser(logUser)

}