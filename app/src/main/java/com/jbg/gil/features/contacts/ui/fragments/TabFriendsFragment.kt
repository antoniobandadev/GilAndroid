package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabContactsBinding
import com.jbg.gil.features.contacts.data.model.ContactMapper.toDto
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.contacts.ui.dialogs.FriendDialog
import com.jbg.gil.features.contacts.ui.dialogs.SolDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabFriendsFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var friends: List<ContactEntity> = mutableListOf()

    private var _binding : FragmentTabContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabFriendsViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()

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
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        searchFriends()
        //selectFriends()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            //Log.d(Constants.GIL_TAG, "Conectado Friends: $isConnected")
            if (isConnected && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
                selectFriends()
                Log.d(Constants.GIL_TAG, "Conectado Friends: $isConnected")
            }else{
                showLoadConnection()
            }
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
            FriendDialog(
                new = false,
                updateUI = {
                    updateUI()
                },
                myFriend = selectedContact.toDto()
            ).show(parentFragmentManager, "friendDialog")

        }

        binding.btnAddFriend.setOnClickListener {
            FriendDialog(
                new = true,
                updateUI = {
                    updateUI()
                }
            ).show(parentFragmentManager, "friendDialog")
        }

        binding.rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.friends.observe(viewLifecycleOwner) { contactList ->
            updateUI()
            showData()
            Log.d(Constants.GIL_TAG, contactList.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        selectFriends()
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
                    showLoadConnection()
                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            friends = contactRepository.loadFriendsFromApi(userPreferences.getUserId().toString())
            if (friends.isEmpty()){
                binding.tvFriendsFound.text = getString(R.string.no_friends_found)
                binding.tvFriendsFound.visibility = View.VISIBLE
            }else{
                binding.tvFriendsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(friends)

        }
    }

    private fun searchFriends() {
        binding.etFriendSearch.addTextChangedListener { searchFriend ->
            val friendsFilter =
                friends.filter { contact ->
                    contact.contactName.lowercase().contains(searchFriend.toString().lowercase())
                }
            if (friendsFilter.isEmpty()){
                binding.tvFriendsFound.text = getString(R.string.no_results_for,searchFriend.toString())
                binding.tvFriendsFound.visibility = View.VISIBLE
            }else{
                binding.tvFriendsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(friendsFilter)
        }
    }

    private fun showData() {
        binding.viewFriendsLoad.visibility = View.GONE
        binding.rvFriends.visibility = View.VISIBLE
    }
    private fun showLoadConnection() {
        binding.viewFriendsLoad.visibility = View.VISIBLE
        binding.rvFriends.visibility = View.GONE
        binding.tvFriendsFound.visibility = View.INVISIBLE
        if (!Utils.isConnectedNow(requireContext()))
            getActivityRootView()?.rootView?.showSnackBarError(getString(R.string.no_internet_connection))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}