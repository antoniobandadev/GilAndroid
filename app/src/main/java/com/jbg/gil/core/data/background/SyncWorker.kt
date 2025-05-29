package com.jbg.gil.core.data.background

import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.prepareImagePart
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventRepository: EventRepository
) : CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result {
        val events = eventRepository.getEventSyncDB()

        for (event in events) {
            try {
                val eventName =
                    Utils.createPartFromString(event.eventName)
                val eventDesc =
                    Utils.createPartFromString(event.eventDesc)
                val eventTypeBody = Utils.createPartFromString(event.eventType)
                val eventDateStart =
                    Utils.createPartFromString(event.eventDateStart)
                val eventDateEnd =
                    Utils.createPartFromString(event.eventDateEnd)
                val eventStreet =
                    Utils.createPartFromString(event.eventStreet)
                val eventCity =
                    Utils.createPartFromString(event.eventCity)
                val eventStatus = Utils.createPartFromString(event.eventStatus)
                val userId = Utils.createPartFromString(event.userId)

                val eventImage = event.eventImg.let { path ->
                    val file = File(path)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("eventImage", file.name, requestFile)
                }

                val response = eventRepository.uploadEvent(
                    eventImage,
                    eventName,
                    eventDesc,
                    eventTypeBody,
                    eventDateStart,
                    eventDateEnd,
                    eventStreet,
                    eventCity,
                    eventStatus,
                    userId
                )

                if (response.isSuccessful) {
                    eventRepository.updateEventSyncDB(event.eventId)
                }



            } catch (e: Exception) {
                return Result.retry()
                Log.e("SyncWorker", "Error al subir evento: ${e.message}", e)
            }
        }
        return Result.success()

    }

}
