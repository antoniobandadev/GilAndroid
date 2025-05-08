package com.jbg.gil.features.events.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jbg.gil.R
import com.jbg.gil.core.ui.ViewPagerAdapter
import com.jbg.gil.databinding.FragmentEventsBinding
import com.jbg.gil.features.events.ui.fragments.TabEventsFragment
import com.jbg.gil.features.events.ui.fragments.TabInvitesFragment

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createEventsTabs()
    }


    private fun createEventsTabs(){

        viewPager = binding.viewPageEvents
        tabLayout = binding.tabLayEvents

        val fragments = listOf(
            TabEventsFragment(),
            TabInvitesFragment()
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        val tabTitles = listOf(getString(R.string.my_events), getString(R.string.my_invites))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}