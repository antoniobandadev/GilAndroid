package com.jbg.gil.features.contacts.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.model.EntityDtoMapper.toDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.databinding.FragmentContactsBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.contacts.ui.dialogs.ContactDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var contacts: List<ContactEntity> = mutableListOf()

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactsViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        backAction()
        colorIconButton()
        searchContact()
        //selectContacts()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            selectContacts()
            if (isConnected){
                contactsSync()
            }
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
            ContactDialog(
                newContact = false,
                updateUI = {
                    updateUI()
                },
                contact = selectedContact.toDto()
            ).show(parentFragmentManager, "contactDialog")
        }

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.contacts.observe(viewLifecycleOwner) { contactList ->
            //contactAdapter.updateData(contactList)
            updateUI()
            showData()
            Log.d(Constants.GIL_TAG, contactList.toString())
        }

        binding.btnAddContact.setOnClickListener {
            ContactDialog(
                newContact = true,
                updateUI = {
                    updateUI()
                }
            ).show(parentFragmentManager, "contactDialog")
        }

    }

   override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.myGuestFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }

    private fun backAction() {
        binding.imgBtBack.setOnClickListener {btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
        binding.tvBack.setOnClickListener {btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
    }

    private fun selectContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.loadContacts(userPreferences.getUserId().toString())
               /* val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true) {
                    Log.d(Constants.GIL_TAG, "Connected")
                    viewModel.loadContacts(userPreferences.getUserId().toString())
                } else {
                    Log.d(Constants.GIL_TAG, "No Connected")
                    viewModel.loadContactsDB()
                }*/

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }


    private fun showData() {
        binding.viewContactsLoad.visibility = View.GONE
        binding.rvContacts.visibility = View.VISIBLE
    }

    private fun colorIconButton() {
        val fab = binding.btnAddContact
        fab.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent))
    }

    private fun updateUI() {
        lifecycleScope.launch {
            contacts = contactRepository.getContactsFromDb()
            if (contacts.isEmpty()){
                binding.tvContactsFound.text = getString(R.string.no_contacts_found)
                binding.tvContactsFound.visibility = View.VISIBLE
            }else{
                binding.tvContactsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(contacts)

        }
    }

    private fun searchContact() {
        binding.etContactSearch.addTextChangedListener { searchContact ->
            val contactsFilter =
                contacts.filter { contact ->
                    contact.contactName.lowercase().contains(searchContact.toString().lowercase())
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

    private fun contactsSync(){
        lifecycleScope.launch {
            val contacts = contactRepository.getSyncContacts()
            for (contact in contacts) {
                try {
                    val updateContacts = contactRepository.insertContactApi(contact.toDto())
                    if(updateContacts.isSuccessful){
                        contactRepository.updateSyncContactsDB(contact.contactId)
                        Log.d(Constants.GIL_TAG, "Datos Actualizados en el servidor")
                    }else{
                        Log.d(Constants.GIL_TAG, "Error al enviar al servidor")
                    }
                }catch (e : Exception){
                    Log.d(Constants.GIL_TAG, e.toString())
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}