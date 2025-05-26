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
class TabFriendsViewModel @Inject constructor(
     private val contactRepository: ContactRepository
) : ViewModel() {

    private val _friends = MutableLiveData<List<ContactEntity>>()
    val friends: LiveData<List<ContactEntity>> = _friends

    fun loadFriends(userId : String) {
        viewModelScope.launch {

            val response = contactRepository.loadFriendsFromApi(userId)

            if (response.isEmpty()){
                Log.d(Constants.GIL_TAG, "No Contacts")
                _friends.value = response
            }else{
                _friends.value = response
            }
        }
    }

  /*  suspend fun loadFriendsDB(){
        val response = contactRepository.getFriendsFromDb()
        _friends.value = response
    }*/

}