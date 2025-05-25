package com.jbg.gil.core.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.utils.Constants

@Dao
interface ContactDao {

    @Query("SELECT * FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactStatus != 'C' ORDER BY contactName ASC ")
    suspend fun getAllContacts() : List<ContactEntity>

    @Insert
    suspend fun insertContact(contact: List<ContactEntity>)

    @Insert
    suspend fun insertOneContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Query("DELETE FROM ${Constants.DATABASE_CONTACTS_TABLE} WHERE contactId = :contactId")
    suspend fun deleteContact(contactId: String)

}