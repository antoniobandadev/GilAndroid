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
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabGuestContactBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.events.ui.dialogs.ContactGuestDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TabGuestContactFragment : Fragment() {

    private var _binding : FragmentTabGuestContactBinding?  = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private var contacts: List<ContactEntity> = mutableListOf()

    private lateinit var contactAdapter: ContactAdapter

    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabGuestContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getString(ARG_EVENT_ID).toString()


        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            if (isConnected){
                updateUI()
                searchContact()
            }else{
                showLoad()
               binding.root.showSnackBarError(getString(R.string.no_internet_connection))
            }
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
            ContactGuestDialog(
                eventId = eventId,
                updateUI = {
                    updateUI()
                },
                contact = selectedContact.toDto(),
                guestType = 0
            ).show(parentFragmentManager, "contactDialog")
            Log.d(Constants.GIL_TAG, "EventId: $eventId")
            Log.d(Constants.GIL_TAG, "Contact: $selectedContact")
        }
        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }


    }


    private fun updateUI() {
        showLoad()
        lifecycleScope.launch(Dispatchers.IO) {
            contacts = contactRepository.contactsGuestsFromApi(userPreferences.getUserId().toString(), eventId)

            Log.d(Constants.GIL_TAG, "UpdateUI: $contacts")

            if (contacts.isEmpty()){
                binding.tvContactsFound.text = getString(R.string.no_contacts_found)
                binding.tvContactsFound.visibility = View.VISIBLE
            }else{

                binding.tvContactsFound.visibility = View.INVISIBLE
            }
            withContext(Dispatchers.Main) {
                contactAdapter.updateData(contacts)
                showData()
            }
        }
    }

    private fun searchContact() {
        binding.etContactSearch.addTextChangedListener { searchContact ->
            val contactsFilter =
                contacts.filter { contact ->
                    contact.contactName.toString().lowercase().contains(searchContact.toString().lowercase())

                }
            if (contactsFilter.isEmpty()){
                binding.tvContactsFound.text = getString(R.string.no_results_for,searchContact.toString())
                binding.tvContactsFound.visibility = View.VISIBLE
            }else{
                binding.tvContactsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(contactsFilter)
        }
    }

    private fun showData() {
        binding.viewContactsLoad.visibility = View.GONE
        binding.rvContacts.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewContactsLoad.visibility = View.VISIBLE
        binding.rvContacts.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.eventsFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }
    override fun onResume() {
        super.onResume()
        updateUI()
        searchContact()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_EVENT_ID = "event_id"

        fun newInstance(eventId: String): TabGuestContactFragment {
            val fragment = TabGuestContactFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_EVENT_ID, eventId)
            }
            return fragment
        }
    }


}

/*
Dispatchers
Dispatchers.Main: UI thread.
Dispatchers.IO: Lectura/escritura (network, base de datos, archivos).
Dispatchers.Default: CPU intensivo (sorting, cálculos).
 */