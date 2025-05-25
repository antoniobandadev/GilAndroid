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

@HiltViewModel
class TabSentViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _solSend = MutableLiveData<List<ContactEntity>>()
    val solSend: LiveData<List<ContactEntity>> = _solSend

    fun loadSolRec(userId : String) {
        viewModelScope.launch {

            val response = contactRepository.loadSolSendFromApi(userId)
            _solSend.value = response

        }
    }


}