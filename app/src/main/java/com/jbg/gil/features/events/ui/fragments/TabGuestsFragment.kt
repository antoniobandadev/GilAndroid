package com.jbg.gil.features.events.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.model.EntityDtoMapper.toDto
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.GuestRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabGuestsBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.events.ui.dialogs.ContactGuestDialog
import com.jbg.gil.features.events.ui.fragments.TabGuestContactFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabGuestsFragment : Fragment() {

    private var _binding : FragmentTabGuestsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var guestsRepository: GuestRepository

    private val networkViewModel : NetworkStatusViewModel by viewModels()

    private var guests: List<ContactEntity> = mutableListOf()

    private lateinit var eventId: String

    private lateinit var contactAdapter: ContactAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabGuestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventId = arguments?.getString(ARG_EVENT_ID).toString()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            if (isConnected){
                updateUI()
                searchGuests()
                showData()
            }else{
                showLoad()
                binding.root.showSnackBarError(getString(R.string.no_internet_connection))
            }
        }


        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
            Log.d(Constants.GIL_TAG, "Contact: $selectedContact")
            /*ContactGuestDialog(
                eventId = eventId,
                updateUI = {
                    updateUI()
                },
                contact = selectedContact.toDto(),
                guestType = 1
            ).show(parentFragmentManager, "contactDialog")
            Log.d(Constants.GIL_TAG, "EventId: $eventId")
            Log.d(Constants.GIL_TAG, "Contact: $selectedContact")*/
        }

        binding.rvGuests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }


    }


    private fun updateUI() {
        showLoad()
        lifecycleScope.launch {
            guests = guestsRepository.getAllGuests(eventId)
            if (guests.isEmpty()){
                binding.tvGuestsFound.text = getString(R.string.no_guests_found)
                binding.tvGuestsFound.visibility = View.VISIBLE
            }else{
                binding.tvGuestsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(guests)
            showData()
        }
    }

    private fun searchGuests() {
        binding.etGuestsSearch.addTextChangedListener { searchFriend ->
            val guestsFilter =
                guests.filter { contact ->
                    contact.contactName.toString().lowercase().contains(searchFriend.toString().lowercase())
                }
            if (guestsFilter.isEmpty()){
                binding.tvGuestsFound.text = getString(R.string.no_results_for,searchFriend.toString())
                binding.tvGuestsFound.visibility = View.VISIBLE
            }else{
                binding.tvGuestsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(guestsFilter)
        }
    }




    private fun showData() {
        binding.viewGuestsLoad.visibility = View.GONE
        binding.rvGuests.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewGuestsLoad.visibility = View.VISIBLE
        binding.rvGuests.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.eventsFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_EVENT_ID = "event_id"

        fun newInstance(eventId: String): TabGuestsFragment {
            val fragment = TabGuestsFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_EVENT_ID, eventId)
            }
            return fragment
        }
    }

}