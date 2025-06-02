package com.jbg.gil.features.events.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jbg.gil.R
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.GuestRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentInvitationDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InvitationDetailFragment : Fragment() {

    private var _binding : FragmentInvitationDetailBinding? = null
    private val binding get() = _binding!!

    private val args: InvitationDetailFragmentArgs by navArgs()

    private lateinit var eventId: String

    @Inject
    lateinit var guestRepository: GuestRepository
    @Inject
    lateinit var userPreferences: UserPreferences

    private val networkViewModel: NetworkStatusViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInvitationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventId = args.eventId.toString()
        Log.d(Constants.GIL_TAG, "event: $eventId")
        backAction()

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            if (isConnected){
                showLoad()
                lifecycleScope.launch {

                    val guestDet = guestRepository.getEventGuestDet(eventId, userPreferences.getUserId().toString())

                    if (guestDet.isSuccessful){

                         val guestList = guestDet.body()?: emptyList()
                         val guest = guestList[0]

                        binding.apply {

                            Glide.with(binding.root.context)
                                .load(guest.guestsQR)
                                .into(object : CustomTarget<Drawable>() {
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        transition: Transition<in Drawable>?
                                    ) {
                                        ivGuestsQR.setImageDrawable(resource)
                                        tvTitleInvName.text = guest.guestInvName
                                        tvTitleEvent.text = guest.eventName
                                        tvDescInvitation.text = guest.eventDesc
                                        tvTypeInvitation.text = guest.eventType
                                        tvDateStInvitation.text = guest.eventDateStart
                                        tvDateEndInvitation.text = guest.eventDateEnd
                                        tvStreetInvitation.text = guest.eventStreet
                                        tvCityInvitation.text = guest.eventCity
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        //ivGuestsQR.setImageDrawable(errorDrawable)
                                    }
                                })


                        }

                        showData()


                    }else{
                        getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        showLoad()
                    }


                    //eventsPendingDeleteDB()
                    // eventsPendingDB()

                    // eventRepository.deleteAllEventsDB()
                    /*val eventsApi = eventRepository.getAllEventsInviteApi(userPreferences.getUserId().toString())
                    if (eventsApi.isSuccessful){
                        val eventList = eventsApi.body() ?: emptyList()
                        val events = eventList.map { event->
                            event.toEntity()
                        }
                        Log.d(Constants.GIL_TAG, "GET" )

                        eventRepository.deleteAllEventsDB()
                        Log.d(Constants.GIL_TAG, "Delete" )

                        eventRepository.insertEventsDB(events)
                        Log.d(Constants.GIL_TAG, "Insert" )

                        eventAdapter.updateEvents(events)
                        Log.d(Constants.GIL_TAG, "Adapter" )

                        binding.tvNoInvites.visibility = View.INVISIBLE
                        showData()
                    }else{
                        if(eventsApi.code() == 404){
                            getActivityRootView()?.showSnackBar(getString(R.string.no_pending_events))
                            binding.tvNoInvites.text = getString(R.string.no_invites_found)
                            binding.tvNoInvites.visibility = View.VISIBLE
                            showData()
                        }else{
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                            showData()
                        }

                    }*/

                    // eventAdapter.updateEvents(events)
                }

            }else{
                getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                showLoad()
            }
        }


    }



    private fun showData() {
        binding.viewInvitationDetLoad.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }

    private fun showLoad() {
        binding.viewInvitationDetLoad.visibility = View.VISIBLE
        binding.mainView.visibility = View.GONE
    }



    private fun backAction() {
        binding.imgBtBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            //(requireParentFragment().requireParentFragment() as? EventsFragment)?.goToTabEvent()
            findNavController().popBackStack()
            //(requireParentFragment() as? EventsFragment)?.goToTab(1)
           // findNavController().navigate(InvitationDetailFragmentDirections.actionInvitationDetailFragmentToEventsFragment())
        }
        binding.tvBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
           // (requireParentFragment().requireParentFragment() as? EventsFragment)?.goToTabEvent()
            findNavController().popBackStack()
            //(requireParentFragment() as? EventsFragment)?.goToTab(1)
            //(activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as? FragmentPadre)?.goToTab(1)
            //findNavController().navigate(InvitationDetailFragmentDirections.actionInvitationDetailFragmentToEventsFragment())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}