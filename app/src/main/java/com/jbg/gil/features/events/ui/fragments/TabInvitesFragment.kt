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
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabInvitesBinding
import com.jbg.gil.features.events.ui.EventsFragmentDirections
import com.jbg.gil.features.events.ui.adapters.EventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabInvitesFragment : Fragment() {

    private var _binding : FragmentTabInvitesBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

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
        _binding = FragmentTabInvitesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(emptyList()) {event ->
            Log.d(Constants.GIL_TAG, event.eventName)
            Log.d(Constants.GIL_TAG, event.eventId)
            val action = EventsFragmentDirections.actionEventsFragmentToInvitationDetailFragment(eventId = event.eventId)
            findNavController().navigate(action)
        }

        binding.rvInvites.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            if (isConnected){
                showLoad()
                lifecycleScope.launch {
                    //eventsPendingDeleteDB()
                   // eventsPendingDB()

                    // eventRepository.deleteAllEventsDB()
                    val eventsApi = eventRepository.getAllEventsInviteApi(userPreferences.getUserId().toString())
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

                        binding.tvNoInvites.visibility = View.INVISIBLE
                        showData()
                    }else{
                        if(eventsApi.code() == 404){
                            getActivityRootView()?.showSnackBar(getString(R.string.no_pending_events))
                            binding.tvNoInvites.text = getString(R.string.no_invites_found)
                            binding.tvNoInvites.visibility = View.VISIBLE
                            showData()
                        }else{
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                            showData()
                        }

                    }

                    // eventAdapter.updateEvents(events)
                }

            }else{
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
                binding.tvNoInvites.text = getString(R.string.no_invites_found)
                binding.tvNoInvites.visibility = View.VISIBLE
                showData()
            }
        }

    }



    private fun showData() {
        binding.viewInvitesLoad.visibility = View.GONE
        binding.rvInvites.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewInvitesLoad.visibility = View.VISIBLE
        binding.rvInvites.visibility = View.GONE
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}