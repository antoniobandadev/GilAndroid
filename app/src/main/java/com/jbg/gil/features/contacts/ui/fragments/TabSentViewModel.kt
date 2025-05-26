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
class TabSentViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _solSend = MutableLiveData<List<ContactEntity>>()
    val solSend: LiveData<List<ContactEntity>> = _solSend

    fun loadSolSend(userId : String) {
        viewModelScope.launch {
            Log.d(Constants.GIL_TAG, "Solicitadas")
            val response = contactRepository.loadSolSendFromApi(userId)
            _solSend.value = response

        }
    }


}