package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentMyGuestBinding
import com.jbg.gil.features.contacts.data.model.OpContactItem
import com.jbg.gil.features.contacts.ui.adapters.OpContactAdapter


class MyGuestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    lateinit var options : List<OpContactItem>

    private var _binding : FragmentMyGuestBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyGuestBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        options = listOf(
            OpContactItem(
                title = getString(R.string.contacts),
                imgStart = R.drawable.ic_contact,
                imgEnd = R.drawable.ic_arrow_forward
            ),
            OpContactItem(
                title = getString(R.string.friends),
                imgStart = R.drawable.ic_friends,
                imgEnd = R.drawable.ic_arrow_forward
            )
        )

        recyclerView = binding.optionsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = OpContactAdapter(options) { option ->
            when (option) {
                getString(R.string.contacts) -> findNavController().navigate(R.id.action_myGuestFragment_to_contactsFragment)
                getString(R.string.friends) ->  findNavController().navigate(R.id.action_myGuestFragment_to_connectionsFragment)
            }
        }

        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}