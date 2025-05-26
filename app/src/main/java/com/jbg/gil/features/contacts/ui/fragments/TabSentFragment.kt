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
import com.jbg.gil.databinding.FragmentTabSentBinding
import com.jbg.gil.features.contacts.data.model.ContactMapper.toDto
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.contacts.ui.dialogs.SolDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabSentFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var solSendList: List<ContactEntity> = mutableListOf()

    private var _binding : FragmentTabSentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabSentViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabSentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        searchSol()
        //selectSendSol()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->

            if (isConnected && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
                selectSendSol()
                Log.d(Constants.GIL_TAG, "Conectado Sent: $isConnected")
            }else{
                showLoadConnection()
            }
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
             SolDialog(
                 received = false,
                 updateUI = {
                     updateUI()
                 },
                 friendSol = selectedContact.toDto()
             ).show(parentFragmentManager, "friendDialog")
        }

        binding.rvSolSend.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.solSend.observe(viewLifecycleOwner) { solList ->
            contactAdapter.updateData(solList)
            solSendList = solList
            updateUI()
            showData()
            Log.d(Constants.GIL_TAG, solList.toString())
        }

    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.myGuestFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }

    override fun onResume() {
        super.onResume()
        selectSendSol()
    }

    private fun selectSendSol() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true) {
                    Log.d(Constants.GIL_TAG, "Connected")
                    viewModel.loadSolSend(userPreferences.getUserId().toString())
                } else {
                    showLoadConnection()
                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            solSendList = contactRepository.loadSolSendFromApi(userPreferences.getUserId().toString())
            if (solSendList.isEmpty()){
                binding.tvSolSendFound.text = getString(R.string.no_sol_send_found)
                binding.tvSolSendFound.visibility = View.VISIBLE
            }else{
                binding.tvSolSendFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(solSendList)

        }
    }

    private fun searchSol() {
        binding.etSolSendearch.addTextChangedListener { searchContact ->
            val solFilter =
                solSendList.filter { sol ->
                    sol.contactName.lowercase().contains(searchContact.toString().lowercase())
                }
            if (solFilter.isEmpty()){
                binding.tvSolSendFound.text = getString(R.string.no_results_for,searchContact.toString())
                binding.tvSolSendFound.visibility = View.VISIBLE
            }else{
                binding.tvSolSendFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(solFilter)
        }
    }

    private fun showData() {
        binding.viewSolSendLoad.visibility = View.GONE
        binding.rvSolSend.visibility = View.VISIBLE
    }
    private fun showLoadConnection() {
        binding.viewSolSendLoad.visibility = View.VISIBLE
        binding.rvSolSend.visibility = View.GONE
        binding.tvSolSendFound.visibility = View.INVISIBLE
        if (!Utils.isConnectedNow(requireContext()))
            getActivityRootView()?.rootView?.showSnackBarError(getString(R.string.no_internet_connection))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}