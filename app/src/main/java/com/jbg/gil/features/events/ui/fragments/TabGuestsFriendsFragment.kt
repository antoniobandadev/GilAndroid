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
import com.jbg.gil.databinding.FragmentTabGuestsFriendsBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.events.ui.dialogs.ContactGuestDialog
import com.jbg.gil.features.events.ui.fragments.TabGuestContactFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabGuestsFriendsFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var friends: List<ContactEntity> = mutableListOf()

    private var _binding : FragmentTabGuestsFriendsBinding?  = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var contactAdapter: ContactAdapter

    private lateinit var eventId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabGuestsFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getString(ARG_EVENT_ID).toString()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Conectado: $isConnected")
            if (isConnected){
                updateUI()
                searchFriends()
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
                guestType = 1
            ).show(parentFragmentManager, "contactDialog")
            Log.d(Constants.GIL_TAG, "EventId: $eventId")
            Log.d(Constants.GIL_TAG, "Contact: $selectedContact")
        }

        binding.rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

    }

    private fun updateUI() {
        showLoad()
        lifecycleScope.launch {
            friends = contactRepository.friendsGuestsFromApi(userPreferences.getUserId().toString(), eventId)
            if (friends.isEmpty()){
                binding.tvFriendsFound.text = getString(R.string.no_friends_found)
                binding.tvFriendsFound.visibility = View.VISIBLE
            }else{
                binding.tvFriendsFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(friends)
            showData()
        }
    }

    private fun searchFriends() {
        binding.etFriendSearch.addTextChangedListener { searchFriend ->
            val friendsFilter =
                friends.filter { contact ->
                    contact.contactName.toString().lowercase().contains(searchFriend.toString().lowercase())
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

    private fun showLoad() {
        binding.viewFriendsLoad.visibility = View.VISIBLE
        binding.rvFriends.visibility = View.GONE
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

        fun newInstance(eventId: String): TabGuestsFriendsFragment {
            val fragment = TabGuestsFriendsFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_EVENT_ID, eventId)
            }
            return fragment
        }
    }


}