package com.jbg.gil.features.contacts.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.repositories.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class ContactsViewModel(
    private val contactRepository: ContactRepository
): ViewModel() {

    private val _contacts = MutableLiveData<List<ContactEntity>>()
    val contacts: LiveData<List<ContactEntity>> = _contacts

    fun loadContacts(userId : String) {
        viewModelScope.launch {
            _contacts.value = contactRepository.getContacts(userId)
        }
    }


}