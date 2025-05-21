package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.remote.apis.UserApi
import com.jbg.gil.core.data.remote.dtos.UserDto
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class UserRepository @Inject constructor (private val userApi: UserApi) {

    suspend fun postRegUser(regUser: UserDto): Response<UserDto> = userApi.registerUser(regUser)

    suspend fun postLogUser(logUser: UserDto): Response<UserDto> = userApi.loginUser(logUser)

}