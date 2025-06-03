package com.jbg.gil.core.repositories

import com.jbg.gil.core.data.remote.apis.UserApi
import com.jbg.gil.core.data.remote.dtos.BasicResponse
import com.jbg.gil.core.data.remote.dtos.PassUserDto
import com.jbg.gil.core.data.remote.dtos.SolPassDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor (private val userApi: UserApi) {

    suspend fun postRegUser(regUser: UserDto): Response<UserDto> = userApi.registerUser(regUser)

    suspend fun postLogUser(logUser: UserDto): Response<UserDto> = userApi.loginUser(logUser)

    suspend fun postExistUser(existUser: UserDto): Response<BasicResponse> = userApi.existsUser(existUser)

    suspend fun postForgotPass(forgotEmail: SolPassDto): Response<PassUserDto>  = userApi.forgotPass(forgotEmail)

    suspend fun putUpdatePass(user : UserDto): Response<BasicResponse> = userApi.updatePass(user)

    suspend fun updateProfile(
        userProfile: MultipartBody.Part?,
        userId: RequestBody

    ): Response<UserDto> = userApi.updateProfile(
        userProfile,
        userId
    )

}