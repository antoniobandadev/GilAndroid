package com.jbg.gil.features.contacts.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jbg.gil.R
import com.jbg.gil.core.ui.ViewPagerAdapter
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.databinding.FragmentFriendsBinding


class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backAction()
        createContactsTabs()
    }



    private fun createContactsTabs(){

        viewPager = binding.viewPageContacts
        tabLayout = binding.tabLayContacts

        val fragments = listOf(
            TabFriendsFragment(),
            TabReceivedFragment(),
            TabSentFragment()
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        val tabTitles = listOf(getString(R.string.my_friends), getString(R.string.received_request), getString(R.string.sent_request))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    private fun backAction(){
        binding.imgBtBack.setOnClickListener {btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(R.id.action_connectionsFragment_to_myGuestFragment)
        }
        binding.tvBack.setOnClickListener {btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(R.id.action_connectionsFragment_to_myGuestFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}