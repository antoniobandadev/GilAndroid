package com.jbg.gil.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterDto (

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("password")
    var password: String? = null

)