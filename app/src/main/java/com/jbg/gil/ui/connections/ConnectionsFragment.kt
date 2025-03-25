package com.jbg.gil.ui.connections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jbg.gil.databinding.FragmentConnectionsBinding


class ConnectionsFragment : Fragment() {

    private lateinit var binding: FragmentConnectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConnectionsBinding.inflate(inflater, container, false)
        return binding.root
    }
}