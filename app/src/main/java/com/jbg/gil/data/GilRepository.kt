package com.jbg.gil.data

import android.provider.ContactsContract.CommonDataKinds.Email
import com.jbg.gil.data.remote.GilApi
import com.jbg.gil.data.remote.model.RegisterDto
import retrofit2.Response
import retrofit2.Retrofit

class GilRepository(private val retrofit: Retrofit) {

    private val gilaApi = retrofit.create(GilApi::class.java)

    suspend fun postRegUser(regUser: RegisterDto): Response<RegisterDto> = gilaApi.registerUser(regUser)

}