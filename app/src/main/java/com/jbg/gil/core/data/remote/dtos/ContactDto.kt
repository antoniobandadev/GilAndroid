package com.jbg.gil.core.data.remote.dtos

data class ContactDto(
    var contactId : String? = null,
    var userId : String? = null,
    var contactEmail : String? = null,
    var contactName : String? = null,
    var contactStatus : String? = null,
    var contactType : String? = null
)

data class BasicResponse(
    val response: String
)

data class AddFriendDto(
    var userId: String,
    var friendEmail: String
)

data class RespFriendDto(
    var userId: String,
    var friendId: String,
    var friendStatus: String
)