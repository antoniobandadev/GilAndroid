package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.databinding.FragmentTabContactsBinding
import com.jbg.gil.features.contacts.data.model.ContactMapper.toDto
import com.jbg.gil.features.contacts.ui.ContactDialog
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabContactsFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var friends: List<ContactEntity> = mutableListOf()

    private var _binding : FragmentTabContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabContactsViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()
    private var isConnectedApp: Boolean = false

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            isConnectedApp = isConnected
            //Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            selectFriends()
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
           /* ContactDialog(
                newContact = false,
                updateUI = {
                    updateUI()
                },
                contact = selectedContact.toDto()
            ).show(parentFragmentManager, "friendDialog")*/
        }

        binding.rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.friends.observe(viewLifecycleOwner) { contactList ->
            //contactAdapter.updateData(contactList)
            updateUI()
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

    private fun selectFriends() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true) {
                    Log.d(Constants.GIL_TAG, "Connected")
                    viewModel.loadFriends(userPreferences.getUserId().toString())
                } else {
                    Log.d(Constants.GIL_TAG, "No Connected")
                    viewModel.loadFriendsDB()
                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            friends = contactRepository.getFriendsFromDb()
            if (friends.isEmpty()){
                binding.tvFriendsFound.text = getString(R.string.no_friends_found)
                binding.tvFriendsFound.visibility = View.VISIBLE
            }else{
                binding.tvFriendsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(friends)

        }
    }

    private fun showData() {
        binding.viewFriendsLoad.visibility = View.GONE
        binding.rvFriends.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}