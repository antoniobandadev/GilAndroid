package com.jbg.gil.core.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.getUserVals
import com.jbg.gil.core.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactApi: ContactApi,
    private val contactDao: ContactDao,
    @ApplicationContext private val context: Context,
) {

    private val dataStore = context.dataStore

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
                dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey("contactTable")] = true
                }
                Log.d(Constants.GIL_TAG, "API Response: $contacts")

            } else if (contactsFromApi.code() == 401) {
                Log.d(Constants.GIL_TAG, "Error 401")
            } else if (contactsFromApi.code() == 404) {
                dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey("contactTable")] = true
                }
                Log.d(Constants.GIL_TAG, "Error 404")
            } else if (contactsFromApi.code() == 500) {
                Log.d(Constants.GIL_TAG, "Error 500")
            }
        } catch (e: Exception) {
            Log.d("Error:", e.toString())
        }
    }

    suspend fun getContacts(userId: String): List<ContactEntity> {
        val userPreferences = getUserVals(context)
        val local = contactDao.getAllContacts()
        if (userPreferences.contactTable) {
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