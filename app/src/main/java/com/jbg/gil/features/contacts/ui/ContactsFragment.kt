package com.jbg.gil.features.contacts.ui

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
import com.jbg.gil.databinding.FragmentContactsBinding
import com.jbg.gil.features.contacts.ui.fragments.TabContactsFragment
import com.jbg.gil.features.contacts.ui.fragments.TabReceivedFragment
import com.jbg.gil.features.contacts.ui.fragments.TabSentFragment


class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

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
        createContactsTabs()
    }



    private fun createContactsTabs(){

        viewPager = binding.viewPageContacts
        tabLayout = binding.tabLayContacts

        val fragments = listOf(
            TabContactsFragment(),
            TabReceivedFragment(),
            TabSentFragment()
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        val tabTitles = listOf(getString(R.string.my_contacts), getString(R.string.received_request), getString(R.string.sent_request))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}