package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.utils.Utils.getUserVals
import com.jbg.gil.databinding.FragmentContactsBinding
import kotlinx.coroutines.launch


class ContactsFragment : Fragment() {

    private var _binding : FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactsViewModel by viewModels()

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
        backAction()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                userPreferences = getUserVals(requireContext())
                viewModel.loadContacts("125478956")

            } catch (e: Exception) {
                Log.e("MyFragment", "Error loading user preferences or contacts", e)
                // Mostrar un mensaje al usuario si es necesariouserPreferences.userId
            }
        }

    }

    private fun backAction(){
        binding.imgBtBack.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_myGuestFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}