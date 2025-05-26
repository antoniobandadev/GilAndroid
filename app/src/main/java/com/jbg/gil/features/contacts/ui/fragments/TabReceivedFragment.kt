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
import com.jbg.gil.databinding.FragmentTabReceivedBinding
import com.jbg.gil.features.contacts.data.model.ContactMapper.toDto
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
import com.jbg.gil.features.contacts.ui.dialogs.SolDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TabReceivedFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var contactRepository: ContactRepository

    private var solReceivedList: List<ContactEntity> = mutableListOf()

    private var _binding : FragmentTabReceivedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabReceivedViewModel by viewModels()

    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTabReceivedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        searchSol()
        //selectReceivedSol()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->

            if (isConnected && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
                selectReceivedSol()
                Log.d(Constants.GIL_TAG, "Conectado Recived: $isConnected")
            }else{
                showLoadConnection()
            }
        }

        contactAdapter = ContactAdapter(emptyList()) { selectedContact ->
             SolDialog(
                 received = true,
                 updateUI = {
                     updateUI()
                 },
                 friendSol = selectedContact.toDto()
             ).show(parentFragmentManager, "friendDialog")
        }

        binding.rvSolRec.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        viewModel.solRec.observe(viewLifecycleOwner) { solList ->
            contactAdapter.updateData(solList)
            solReceivedList = solList
            updateUI()
            showData()
            Log.d(Constants.GIL_TAG, solList.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        selectReceivedSol()
    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.myGuestFragment).isChecked = true
        selectReceivedSol()
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }

    private fun selectReceivedSol() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true) {
                    Log.d(Constants.GIL_TAG, "Connected")
                    viewModel.loadSolRec(userPreferences.getUserId().toString())
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
            solReceivedList = contactRepository.loadSolRecFromApi(userPreferences.getUserId().toString())
            if (solReceivedList.isEmpty()){
                binding.tvSolRecFound.text = getString(R.string.no_sol_rec_found)
                binding.tvSolRecFound.visibility = View.VISIBLE
            }else{
                binding.tvSolRecFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(solReceivedList)
        }
    }

    private fun searchSol() {
        binding.etSolRecearch.addTextChangedListener { searchContact ->
            val solFilter =
                solReceivedList.filter { sol ->
                    sol.contactName.lowercase().contains(searchContact.toString().lowercase())
                }
            if (solFilter.isEmpty()){
                binding.tvSolRecFound.text = getString(R.string.no_results_for,searchContact.toString())
                binding.tvSolRecFound.visibility = View.VISIBLE
            }else{
                binding.tvSolRecFound.visibility = View.INVISIBLE
            }
            contactAdapter.updateData(solFilter)
        }
    }

    private fun showData() {
        binding.viewSolRecLoad.visibility = View.GONE
        binding.rvSolRec.visibility = View.VISIBLE
    }
    private fun showLoadConnection() {
        binding.viewSolRecLoad.visibility = View.VISIBLE
        binding.rvSolRec.visibility = View.GONE
        binding.tvSolRecFound.visibility = View.INVISIBLE
        if (!Utils.isConnectedNow(requireContext()))
            getActivityRootView()?.rootView?.showSnackBarError(getString(R.string.no_internet_connection))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}