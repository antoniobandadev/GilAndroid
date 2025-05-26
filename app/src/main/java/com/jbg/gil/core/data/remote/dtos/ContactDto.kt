package com.jbg.gil.core.data.remote.dtos

data class ContactDto(
    var contactId : String,
    var userId : String,
    var contactEmail : String,
    var contactName : String,
    var contactStatus : String,
    var contactType : String
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