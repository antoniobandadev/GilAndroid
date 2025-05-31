package com.jbg.gil.core.repositories

import android.util.Log
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.dtos.ContactDto
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactApi: ContactApi,
    private val contactDao: ContactDao,
    private var userPreferences: UserPreferences
) {

    //___________________________Contacts----------------------------------------------------

    suspend fun getContactsFromDb(): List<ContactEntity> = contactDao.getAllContacts()

    private suspend fun loadContactsFromApi(userId: String) {
        try {
            val contactsFromApi = contactApi.getContacts(userId)
            if (contactsFromApi.isSuccessful) {
                val contactList = contactsFromApi.body() ?: emptyList()
                val contacts = contactList.map { contact ->
                    contact.toEntity()
                }

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
        if (userPreferences.getContactTable() && local.isNotEmpty()) {
            Log.d(Constants.GIL_TAG, "Contacts Local")
            return local
        } else {
            loadContactsFromApi(userId)
            return contactDao.getAllContacts()
        }
    }

    suspend fun insertContact(contact : ContactEntity) =  contactDao.insertOneContact(contact)

    suspend fun updateContact(contact: ContactEntity) =  contactDao.updateContact(contact)

    suspend fun deleteContact(contactId: String) = contactDao.deleteContact(contactId)

    suspend fun getSyncContacts() = contactDao.getSyncContacts()

    suspend fun getSyncContactsDelete() = contactDao.getSyncContactsDelete()

    suspend fun updateSyncContactsDB(contactId: String) = contactDao.updateSyncContacts(contactId)

    suspend fun insertContactApi(contact: ContactDto) = contactApi.newContact(contact)

    suspend fun deleteContactApi(contact: ContactDto) = contactApi.deleteContact(contact)

    suspend fun getFriendsApi(userId: String, friendStatus: String) = contactApi.getFriends(userId, friendStatus)


    //_________________________________________Friends____________________________________________

     suspend fun loadFriendsFromApi(userId: String) : List<ContactEntity>{
        try {
            val friendStatus = "A"
            val friendsFromApi = contactApi.getFriends(userId, friendStatus)
            if (friendsFromApi.isSuccessful) {
                val contactList = friendsFromApi.body() ?: emptyList()
                val contacts = contactList
                    .filter { friend -> friend.contactStatus == "A"}
                    .map { contact ->
                    contact.toEntity()
                }
                //contactDao.clearAllFriends()
                //contactDao.insertContact(contacts)
                Log.d(Constants.GIL_TAG, "API Response: $contacts")
                return contacts

            } else if (friendsFromApi.code() == 401) {
                Log.d(Constants.GIL_TAG, "Error 401")
            } else if (friendsFromApi.code() == 404) {
                Log.d(Constants.GIL_TAG, "Error 404")
            } else if (friendsFromApi.code() == 500) {
                Log.d(Constants.GIL_TAG, "Error 500")
            }
        } catch (e: Exception) {
            Log.d(Constants.GIL_TAG, e.toString())
        }
         return emptyList()
    }


    //_________________________________________Received ____________________________________________

     suspend fun loadSolRecFromApi(userId: String) : List<ContactEntity> {
        try {
            val friendStatus = "R"
            val solRecFromApi = contactApi.getFriends(userId, friendStatus)
            if (solRecFromApi.isSuccessful) {
                val solRecList = solRecFromApi.body() ?: emptyList()


                val solRecs = solRecList
                    .filter { sol -> sol.contactStatus == "P" }
                    .map { solReceived ->
                        solReceived.toEntity()
                }
                //contactDao.clearAllFriends()
                Log.d(Constants.GIL_TAG, "API Response: $solRecs")
                return solRecs

            } else if (solRecFromApi.code() == 401) {
                Log.d(Constants.GIL_TAG, "Error 401")
            } else if (solRecFromApi.code() == 404) {
                Log.d(Constants.GIL_TAG, "Error 404")
            } else if (solRecFromApi.code() == 500) {
                Log.d(Constants.GIL_TAG, "Error 500")
            }
        } catch (e: Exception) {
            Log.d(Constants.GIL_TAG, e.toString())
        }
        return emptyList()
    }

    //_________________________________________Sent ____________________________________________

    suspend fun loadSolSendFromApi(userId: String) : List<ContactEntity> {
        try {
            val friendStatus = "S"
            val solSendFromApi = contactApi.getFriends(userId, friendStatus)
            if (solSendFromApi.isSuccessful) {
                val solRecList = solSendFromApi.body() ?: emptyList()


                val solSend = solRecList
                    .filter { sol -> sol.contactStatus == "P" }
                    .map { solSend ->
                        solSend.toEntity()
                    }
                //contactDao.clearAllFriends()
                Log.d(Constants.GIL_TAG, "API Response: $solSend")
                return solSend

            } else if (solSendFromApi.code() == 401) {
                Log.d(Constants.GIL_TAG, "Error 401")
            } else if (solSendFromApi.code() == 404) {
                Log.d(Constants.GIL_TAG, "Error 404")
            } else if (solSendFromApi.code() == 500) {
                Log.d(Constants.GIL_TAG, "Error 500")
            }
        } catch (e: Exception) {
            Log.d(Constants.GIL_TAG, e.toString())
        }
        return emptyList()
    }


}