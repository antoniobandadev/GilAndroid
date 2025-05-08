package com.jbg.gil.core.network

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class NetworkStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val networkStatusLiveData = NetworkStatusLiveData(application)

    fun getNetworkStatus(): LiveData<Boolean> = networkStatusLiveData
}