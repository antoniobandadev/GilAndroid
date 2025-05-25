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
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentTabReceivedBinding
import com.jbg.gil.databinding.FragmentTabSentBinding
import com.jbg.gil.features.contacts.ui.adapters.ContactAdapter
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
    private var isConnectedApp: Boolean = false

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

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            isConnectedApp = isConnected
            //Log.d(Constants.GIL_TAG, "Conectado: $isConnected")

            if (isConnected){
                selectSendSol()
            }else{
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
            }
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

    private fun selectSendSol() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val isConnected = networkViewModel.getNetworkStatus().value

                if (isConnected == true) {
                    Log.d(Constants.GIL_TAG, "Connected")
                    viewModel.loadSolRec(userPreferences.getUserId().toString())
                } else {
                    Log.d(Constants.GIL_TAG, "No Connected")

                }

            } catch (e: Exception) {
                Log.e("Contacts", "Error loading contacts", e)
            }
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            if (solSendList.isEmpty()){
                binding.tvSolSendFound.text = getString(R.string.no_sol_send_found)
                binding.tvSolSendFound.visibility = View.VISIBLE
            }else{
                binding.tvSolSendFound.visibility = View.INVISIBLE
            }

        }
    }

    private fun showData() {
        binding.viewSolSendLoad.visibility = View.GONE
        binding.rvSolSend.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}