package com.jbg.gil.features.events.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jbg.gil.R
import com.jbg.gil.core.ui.ViewPagerAdapter
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.databinding.FragmentGuestsBinding
import com.jbg.gil.features.events.ui.fragments.EventsDetailFragmentArgs
import com.jbg.gil.features.events.ui.fragments.TabGuestContactFragment
import com.jbg.gil.features.events.ui.fragments.TabGuestsFragment
import com.jbg.gil.features.events.ui.fragments.TabGuestsFriendsFragment

class GuestsFragment : Fragment() {
    private var _binding: FragmentGuestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val args: EventsDetailFragmentArgs by navArgs()

    private lateinit var eventId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGuestsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventId  =args.eventId
        binding.tvTitleInvitations.text = args.eventName
        createEventsTabs()
        backAction()
    }


    private fun createEventsTabs(){

        viewPager = binding.viewPageGuests
        tabLayout = binding.tabLayGuests

        val fragments = listOf(
            TabGuestsFragment.newInstance(eventId),
            TabGuestsFriendsFragment.newInstance(eventId),
            TabGuestContactFragment.newInstance(eventId)
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        val tabTitles = listOf(getString(R.string.my_guests), getString(R.string.my_friends), getString(R.string.my_contacts))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    private fun backAction() {
        binding.imgBtBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigateUp()
        }
        binding.tvBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}