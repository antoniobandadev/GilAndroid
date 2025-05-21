package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.getUserVals
import com.jbg.gil.databinding.FragmentContactsBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private var _binding : FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactsViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()
    private var isConnectedApp : Boolean = false

    private lateinit var userPreferences: UserPreferences

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
        backAction()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner){ isConnected ->
            isConnectedApp = isConnected
            //Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            selectContacts()
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
            Toast.makeText(
                context,
                "Contact: ${selectedContact.contactName}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.contacts.observe(viewLifecycleOwner) { contactList ->
            contactAdapter.updateData(contactList)
            showData()
            Log.d(Constants.GIL_TAG, contactList.toString())
        }

    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.myGuestFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }

    private fun backAction(){
        binding.imgBtBack.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
    }

    private fun selectContacts(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true){
                    Log.d(Constants.GIL_TAG, "Connected")
                    userPreferences = getUserVals(requireContext())
                    viewModel.loadContacts(userPreferences.userId)
                }else{
                    Log.d(Constants.GIL_TAG, "No Connected")
                    viewModel.loadContactsDB()
                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }
    private fun showData(){
        binding.viewContactsLoad.visibility = View.GONE
        binding.rvContacts.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}