package com.jbg.gil.features.scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.jbg.gil.databinding.FragmentScanBinding
import com.jbg.gil.features.events.ui.adapters.EventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScanFragment : Fragment() {

    private  var _binding : FragmentScanBinding? = null
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
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        eventAdapter = EventAdapter(emptyList()) {event ->
            Log.d(Constants.GIL_TAG, event.eventName)
            Log.d(Constants.GIL_TAG, event.eventId)
            findNavController().navigate(R.id.action_scanFragment_to_scannerFragment)
           /* val action = ScanFragmentDirections.actionScanFragmentToScannerFragment()
            findNavController().navigate(action)*/
           /* val action = EventsFragmentDirections.actionEventsFragmentToInvitationDetailFragment(eventId = event.eventId)
            findNavController().navigate(action)*/
        }

        binding.rvScan.apply {
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
                    val eventsApi = eventRepository.getAllEventsScanApi(userPreferences.getUserId().toString())
                    if (eventsApi.isSuccessful){
                        val eventList = eventsApi.body() ?: emptyList()
                        val events = eventList.map { event->
                            event.toEntity()
                        }
                        eventAdapter.updateEvents(events)
                        Log.d(Constants.GIL_TAG, "Adapter" )
                        showData()
                        binding.tvNoScan.visibility = View.INVISIBLE
                    }else{
                        if(eventsApi.code() == 404){
                            getActivityRootView()?.showSnackBar(getString(R.string.no_pending_events))
                            binding.tvNoScan.text = getString(R.string.no_invites_found)
                            binding.tvNoScan.visibility = View.VISIBLE
                            showData()
                        }else{
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                            showData()
                        }

                    }

                    // eventAdapter.updateEvents(events)
                }

            }else{
                showLoad()
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
                binding.tvNoScan.text = getString(R.string.no_invites_found)
                binding.tvNoScan.visibility = View.VISIBLE
                showData()
            }
        }

    }

    private fun showData() {
        binding.viewScanLoad.visibility = View.GONE
        binding.rvScan.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewScanLoad.visibility = View.VISIBLE
        binding.rvScan.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}