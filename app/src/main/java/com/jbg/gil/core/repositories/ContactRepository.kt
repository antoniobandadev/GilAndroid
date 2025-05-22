package com.jbg.gil.core.repositories

import android.util.Log
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.utils.Constants
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactApi: ContactApi,
    private val contactDao: ContactDao,
    private var userPreferences: UserPreferences
) {

    suspend fun getContactsFromDb(): List<ContactEntity> {
        return contactDao.getAllContacts()
    }

    private suspend fun loadContactsFromApi(userId: String) {
        try {
            val contactsFromApi = contactApi.getContacts(userId)
            if (contactsFromApi.isSuccessful) {
                val contactList = contactsFromApi.body() ?: emptyList()
                val contacts = contactList.map { contact ->
                    contact.toEntity()
                }
                //dao.clearAll()
                contactDao.insertContact(contacts)
                userPreferences.saveContactTable(true)
                Log.d(Constants.GIL_TAG, "API Response: $contacts")

            } else if (contactsFromApi.code() == 401) {
                Log.d(Constants.GIL_TAG, "Error 401")
            } else if (contactsFromApi.code() == 404) {
                userPreferences.saveContactTable(true)
                Log.d(Constants.GIL_TAG, "Error 404")
            } else if (contactsFromApi.code() == 500) {
                Log.d(Constants.GIL_TAG, "Error 500")
            }
        } catch (e: Exception) {
            Log.d("Error:", e.toString())
        }
    }

    suspend fun getContacts(userId: String): List<ContactEntity> {
        val local = contactDao.getAllContacts()
        if (userPreferences.getContactTable()) {
            Log.d(Constants.GIL_TAG, "Local")
            return local
        } else {
            loadContactsFromApi(userId)
            return contactDao.getAllContacts()
        }
    }

    private fun ContactDto.toEntity(): ContactEntity {
        return ContactEntity(
            contactId = this.contactId,
            userId = this.userId,
            contactEmail = this.contactEmail,
            contactName = this.contactName,
            contactStatus = this.contactStatus
        )
    }
}