package com.jbg.gil.features.events.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jbg.gil.R
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.isInteger
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabEventsBinding
import com.jbg.gil.features.events.ui.EventsFragmentDirections
import com.jbg.gil.features.events.ui.adapters.EventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class TabEventsFragment : Fragment() {

    private lateinit var eventAdapter: EventAdapter

    private var _binding : FragmentTabEventsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var eventRepository: EventRepository

    private val networkViewModel: NetworkStatusViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(emptyList()) {event ->
            Log.d(Constants.GIL_TAG, event.eventName)
            val action = EventsFragmentDirections.actionEventsFragmentToEventsDetailFragment(eventId = event.eventId)
            findNavController().navigate(action)
        }

        binding.rvEvents.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            if (isConnected){
                showLoad()
                lifecycleScope.launch {
                    eventsPendingDeleteDB()
                    eventsPendingDB()

                   // eventRepository.deleteAllEventsDB()
                    val eventsApi = eventRepository.getAllEventsApi(userPreferences.getUserId().toString())
                    if (eventsApi.isSuccessful){
                        val eventList = eventsApi.body() ?: emptyList()
                        val events = eventList.map { event->
                            event.toEntity()
                        }
                        Log.d(Constants.GIL_TAG, "GET" )

                        eventRepository.deleteAllEventsDB()
                        Log.d(Constants.GIL_TAG, "Delete" )

                        eventRepository.insertEventsDB(events)
                        Log.d(Constants.GIL_TAG, "Insert" )

                        eventAdapter.updateEvents(events)
                        Log.d(Constants.GIL_TAG, "Adapter" )

                        showData()
                    }else{
                        getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                    }

                   // eventAdapter.updateEvents(events)
                }

            }else{
                lifecycleScope.launch {
                    showLoad()
                    val events = eventRepository.getAllEventsDB()
                    eventAdapter.updateEvents(events)
                    showData()
                    Log.d(Constants.GIL_TAG, "Desde DB")
                }
            }
        }

    }

     private suspend fun eventsPendingDB(){
            val events = eventRepository.getEventSyncDB()

            for (event in events) {
                try {
                    val eventId =
                        Utils.createPartFromString(event.eventId)
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

                    var eventImage :MultipartBody.Part? = null
                    Log.d(Constants.GIL_TAG, "Image: ${event.eventId} - ${event.eventImg}" )

                    if(event.eventImg == "null"){

                    }else{
                         eventImage = event.eventImg.let { path ->
                             val file = File(path)
                             val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                             MultipartBody.Part.createFormData("eventImage", file.name, requestFile)
                         }
                    }



                    Log.d(Constants.GIL_TAG, "Event_ mi db: $event.eventId")

                    if(isInteger(event.eventId)){
                        Log.d(Constants.GIL_TAG, "Update: ${event.eventId} - ${event.eventImg}" )
                        val response = eventRepository.updateEvent(
                            eventImage,
                            eventId,
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
                            val myEvent = response.body()
                            eventRepository.updateEventSyncDB(myEvent?.eventId.toString(),myEvent?.eventImg.toString())
                            Log.d(Constants.GIL_TAG, "Updated: ${event.eventId}" )

                        }else{
                            Log.d(Constants.GIL_TAG, "Error Update: ${response.errorBody()}" )
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        }


                    }else{
                        Log.d(Constants.GIL_TAG, "Insert: ${event.eventId}" )
                        val response = eventRepository.uploadEvent(
                            eventImage,
                            eventId,
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
                            val myEvent = response.body()
                            eventRepository.updateEventSyncDB(myEvent?.eventId.toString(),myEvent?.eventImg.toString())
                        }else{
                            Log.d(Constants.GIL_TAG, "Error Insert: ${response.errorBody()}" )
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        }

                    }

                } catch (e: Exception) {
                    Log.e(Constants.GIL_TAG, "Error al subir evento: ${event.eventImg}", e)
                    getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                }
            }
    }


    private suspend fun eventsPendingDeleteDB() {
        val events = eventRepository.getEventsDelete()
        for (event in events) {
            try {
                val eventDto = EventDto(eventId = event.eventId)
                val eventDelete = eventRepository.deleteEventApi(eventDto)
                if(eventDelete.isSuccessful){
                    eventRepository.deleteEventDB(event.eventId)
                }else{
                    Log.d(Constants.GIL_TAG, "Error Delete Event canceled DB: ${event.eventId}")
                    getActivityRootView()?.showSnackBarError(getString(R.string.server_error))

                }
            }catch (e: Exception){
                Log.d(Constants.GIL_TAG, "Error Delete Events canceled DB")
                getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
            }
        }

    }


    private fun showData() {
        binding.viewEventsLoad.visibility = View.GONE
        binding.rvEvents.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewEventsLoad.visibility = View.VISIBLE
        binding.rvEvents.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}