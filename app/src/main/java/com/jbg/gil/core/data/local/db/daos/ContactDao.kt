package com.jbg.gil.core.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.utils.Constants

@Dao
interface ContactDao {

    @Query("SELECT * FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactStatus != 'C' and contactType = 'C' ORDER BY contactName ASC ")
    suspend fun getAllContacts() : List<ContactEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactStatus ='P' and contactType = 'C' ORDER BY contactName ASC ")
    suspend fun getSyncContacts() : List<ContactEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactStatus ='C' and contactType = 'C' ORDER BY contactName ASC ")
    suspend fun getSyncContactsDelete() : List<ContactEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactStatus ='A' and contactType = 'F' ORDER BY contactName ASC ")
    suspend fun getFriendsDB() : List<ContactEntity>

    @Query("UPDATE ${Constants.DATABASE_CONTACTS_TABLE} SET contactStatus = 'A' and contactType = 'C' WHERE contactId = :contactId ")
    suspend fun updateSyncContacts(contactId: String)

    @Insert
    suspend fun insertContact(contact: List<ContactEntity>)

    @Insert
    suspend fun insertOneContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Query("DELETE FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactId = :contactId")
    suspend fun deleteContact(contactId: String)

    @Query("DELETE FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactType = 'F'")
    suspend fun clearAllFriends()

}