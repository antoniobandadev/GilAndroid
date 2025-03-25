package com.jbg.gil.ui.newevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jbg.gil.databinding.FragmentNewEventBinding

class NewEventFragment : Fragment() {

    private lateinit var binding : FragmentNewEventBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewEventBinding.inflate(inflater, container, false)
        return binding.root
    }

}