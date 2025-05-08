package com.jbg.gil.core.network.data

import com.jbg.gil.core.network.remote.GilApi
import com.jbg.gil.core.model.UserDto
import retrofit2.Response
import retrofit2.Retrofit

class GilRepository(private val retrofit: Retrofit) {

    private val gilaApi = retrofit.create(GilApi::class.java)

    suspend fun postRegUser(regUser: UserDto): Response<UserDto> = gilaApi.registerUser(regUser)

}