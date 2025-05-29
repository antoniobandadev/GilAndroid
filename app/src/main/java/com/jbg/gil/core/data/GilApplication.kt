package com.jbg.gil.core.data

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class GilApplication : Application(), Configuration.Provider  {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // sourceCompatibility = JavaVersion.VERSION_1_8
   // targetCompatibility = JavaVersion.VERSION_1_8
}