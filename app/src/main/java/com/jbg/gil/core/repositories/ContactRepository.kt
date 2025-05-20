package com.jbg.gil.core.repositories

import android.util.Log
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.utils.Constants

class ContactRepository (
    private val api: ContactApi,
    private val dao: ContactDao
) {

    // Cargar contactos desde base de datos local
    suspend fun getContactsFromDb(): List<ContactEntity>{
        return dao.getAllContacts()
    }

    // Cargar contactos desde API y guardar localmente
    private suspend fun loadContactsFromApi(userId : String) {
        val contactsFromApi = api.getContacts(userId)
        val contacts = contactsFromApi.map {contact ->
            contact.toEntity()
        }
        //dao.clearAll()
        dao.insertContact(contacts)
        Log.d(Constants.GIL_TAG, "Inserto: $contacts")
    }

    // Estrategia combinada: primero local, luego actualizar si es necesario
    suspend fun getContacts(userId: String): List<ContactEntity> {
        val local = dao.getAllContacts()
        if (local.isNotEmpty()) {
            Log.d(Constants.GIL_TAG, "Local")
            return local
        } else {
            loadContactsFromApi(userId)
            return dao.getAllContacts()
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