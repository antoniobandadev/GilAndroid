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
class TabReceivedViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _solRec = MutableLiveData<List<ContactEntity>>()
    val solRec: LiveData<List<ContactEntity>> = _solRec

    fun loadSolRec(userId : String) {
        viewModelScope.launch {

            val response = contactRepository.loadSolRecFromApi(userId)
            _solRec.value = response

        }
    }


}