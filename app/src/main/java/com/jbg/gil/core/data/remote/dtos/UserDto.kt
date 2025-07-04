package com.jbg.gil.core.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class UserDto (

    @SerializedName("userId")
    var userId: String? = null,

    @SerializedName("userName")
    var name: String? = null,

    @SerializedName("userEmail")
    var email: String? = null,

    @SerializedName("userPassword")
    var password: String? = null,

    @SerializedName("userProfile")
    var userProfile: String? = null,

    @SerializedName("userDeviceId")
    var deviceId : String? = null,

    @SerializedName("userCreatedAt")
    var createdAt : String? = null

)

data class SolPassDto(
    var userEmail: String,
    var userLanguage: String
)

data class PassUserDto(
    var userPassId : String,
    var userId: String,
    var userPassCode: String
)
