package com.jbg.gil.features.contacts.ui.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository
): ViewModel() {

    private val _contacts = MutableLiveData<List<ContactEntity>>()
    val contacts: LiveData<List<ContactEntity>> = _contacts

    fun loadContacts(userId : String) {
        viewModelScope.launch {

            val response = contactRepository.getContacts(userId)

            if (response.isEmpty()){
                Log.d(Constants.GIL_TAG, "No Contacts")
                _contacts.value = response
            }else{
                _contacts.value = response
                //Log.d(Constants.GIL_TAG, response.toString())
            }
        }
    }

    suspend fun loadContactsDB(){
        val response = contactRepository.getContactsFromDb()
        _contacts.value = response
    }

}