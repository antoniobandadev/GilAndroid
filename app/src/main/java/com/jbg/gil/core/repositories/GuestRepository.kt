package com.jbg.gil.core.repositories

import android.util.Log
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.apis.GuestApi
import com.jbg.gil.core.data.remote.dtos.GuestDto
import com.jbg.gil.core.utils.Constants
import javax.inject.Inject

class GuestRepository  @Inject constructor (private val guestApi: GuestApi){

    suspend fun insertGuest(guest: GuestDto) = guestApi.newGuest(guest)

    suspend fun getAllGuests(eventId: String) : List<ContactEntity> {
        val friendsFromApi = guestApi.myGuestCF(eventId)
        if (friendsFromApi.isSuccessful) {
            val contactList = friendsFromApi.body() ?: emptyList()
            val contacts = contactList
                .filter { friend -> friend.contactStatus == "A"}
                .map { contact ->
                    contact.toEntity()
                }

            Log.d(Constants.GIL_TAG, "API Response Guests: $contacts")
            return contacts
        }else{
            return emptyList()
        }
    }

}