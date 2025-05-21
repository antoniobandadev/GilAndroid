package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.getUserVals
import com.jbg.gil.databinding.FragmentContactsBinding
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
        Log.d(Constants.GIL_TAG, "Regreso")

        backAction()
        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner){ isConnected ->
            isConnectedApp = isConnected
        }

        selectContacts()

        viewModel.contacts.observe(viewLifecycleOwner){ contacts ->

            Log.d(Constants.GIL_TAG, contacts.toString())

        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(Constants.GIL_TAG, "Pausa")

    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        //bottomNavView.selectedItemId = R.id.myGuestFragment
        bottomNavView.menu.findItem(R.id.myGuestFragment).isChecked = true
        Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
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
                if (isConnectedApp){
                    userPreferences = getUserVals(requireContext())
                    viewModel.loadContacts(userPreferences.userId)
                }else{
                    viewModel.loadContactsDB()
                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}