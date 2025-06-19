package com.jbg.gil.features.events.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.core.utils.Utils.convertDate
import com.jbg.gil.core.utils.Utils.copyUriToInternalStorage
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.prepareImagePart
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentEventsDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EventsDetailFragment : Fragment() {

    private var _binding : FragmentEventsDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var eventRepository: EventRepository
    @Inject
    lateinit var userPreferences: UserPreferences
    @Inject
    lateinit var contactRepository : ContactRepository

    private val args: EventsDetailFragmentArgs by navArgs()

    private lateinit var eventId: String

    private var  eventType : String = ""

    private val locale = Locale.getDefault().language
    private val strDateFormat = when (locale) {
        "es" -> "dd/MM/yyyy HH:mm"
        "en" -> "MM/dd/yyyy HH:mm"
        else -> "MM/dd/yyyy HH:mm"
    }
    private val strDateFormatBD = "yyyy-MM-dd HH:mm"

    private var eventImage : MultipartBody.Part? = null

    private lateinit var listOfFriends : List<ContactEntity>

    private lateinit var userIdScan : String

    private lateinit var imageSelected : ImageView
    private lateinit var textAdd : TextView
    private lateinit var textDelete : TextView

    private var uriDB : String = "null"
    private var imagePath : String = "null"

        private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        eventImage = prepareImagePart(uri, requireContext(), "eventImage")
        if (uri != null){
            uriDB = copyUriToInternalStorage(requireContext(),uri).toString()
            Log.d(Constants.GIL_TAG, "Image select")
            imageSelected.visibility = View.VISIBLE
            imageSelected.setImageURI(uri)
            textDelete.visibility = View.VISIBLE
            textAdd.setText(R.string.edit_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))

        }else{
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))
            /*uriDB = "null"
            Log.d(Constants.GIL_TAG, "Image NO select")
            imageSelected.visibility = View.GONE
            imageSelected.setImageResource(R.drawable.ic_add_image)
            textAdd.setText(R.string.add_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))*/
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(binding.root, requireActivity())
        focusAndTextListener()
        backAction()
        goMyGuests()

        imageSelected = binding.ivEvent
        textAdd = binding.tvAddImage
        textDelete = binding.tvDeleteImage

        eventId = args.eventId

        Log.d(Constants.GIL_TAG, "Event: $eventId")
        loadInfoEvent(eventId)

        binding.tvAddImage.setOnClickListener {
            binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            // binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            //Type en specific
            // pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("img/gif")))
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        binding.tvDeleteImage.setOnClickListener {
            uriDB = "null"
            Log.d(Constants.GIL_TAG, "Image NO selected")
            imageSelected.visibility = View.GONE
            imageSelected.setImageResource(R.drawable.ic_add_image)
            textAdd.setText(R.string.add_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))
            textDelete.visibility = View.GONE

        }


        binding.acEventType.setOnItemClickListener { parent, _, position, _ ->
            eventType = parent.getItemAtPosition(position) as String
        }

        binding.acUserScan.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectFriend = listOfFriends.find { it.contactName == selectedName }
            selectFriend?.let {
                userIdScan = selectFriend.contactId
                Log.d(Constants.GIL_TAG, "Selecciono: ${userIdScan}")
            }
        }

        binding.etEventDateStart.setOnClickListener {
            showStartDateTimePicker()
        }

        binding.etEventDateEnd.setOnClickListener {
            showEndDateTimePicker()
        }


        binding.btSaveEvent.setOnClickListener {

            if(validateInputs()) {
                DialogUtils.showLoadingDialog(requireContext())

                val currentDate = Date()
                val formatter = SimpleDateFormat(strDateFormatBD, Locale.getDefault())
                val dateNow = formatter.format(currentDate)

                if(uriDB != "null"){
                    imagePath = uriDB
                }

                val etEventDateStartDB = convertDate(binding.etEventDateStart.text.toString(), strDateFormat, strDateFormatBD)
                val etEventDateEndDB = convertDate(binding.etEventDateEnd.text.toString(), strDateFormat, strDateFormatBD)

                val myEvent = EventDto(
                    eventId= eventId,
                    eventName = binding.etEventName.text.toString(),
                    eventDesc = binding.etEventDesc.text.toString(),
                    eventType = eventType,
                    eventDateStart = etEventDateStartDB,
                    eventDateEnd =etEventDateEndDB,
                    eventStreet = binding.etEventStreet.text.toString(),
                    eventCity = binding.etEventCity.text.toString(),
                    eventStatus = "A",
                    eventImg = imagePath,
                    eventCreatedAt = dateNow,
                    userId = userPreferences.getUserId().toString(),
                    eventSync = 0,
                    userIdScan = userIdScan
                )

                try {
                    lifecycleScope.launch {

                        val result = async {
                            eventRepository.updateEvent(myEvent.toEntity())
                        }
                        result.await()

                        getActivityRootView()?.showSnackBar(getString(R.string.event_update_success))
                        findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
                        DialogUtils.dismissLoadingDialog()
                    }

                } catch (e: Exception) {
                    Log.d(Constants.GIL_TAG, "Error Insert Event: $e")
                    DialogUtils.dismissLoadingDialog()
                    binding.root.showSnackBarError(getString(R.string.server_error))
                }
            }
        }

        binding.btCancelEvent.setOnClickListener {
            val myEventId = args.eventId
            Utils.showConfirmAlertDialog(
                context = requireContext(),
                title = getString(R.string.cancel_event),
                message = getString(R.string.confirm_cancel_event),
                confirmText = getString(R.string.yes),
                cancelText = getString(R.string.no),
                confirmColor = ContextCompat.getColor(requireContext(), R.color.red),
                onConfirm = {
                    lifecycleScope.launch {
                       deleteEvents(myEventId)
                    }
                }

            )
        }


    }



    private fun loadInfoEvent(eventId : String){
        lifecycleScope.launch {

            val events = eventRepository.getEvent(eventId)

            if (events.isNotEmpty()) {
                val event = events.first()

                val eventStartDateFormat = convertDate(event.eventDateStart, strDateFormatBD, strDateFormat)
                val eventEndDateFormat = convertDate(event.eventDateEnd, strDateFormatBD, strDateFormat)

                binding.apply {
                    btSaveEvent.setText(R.string.update)
                    //tvTitleEvents.text = getString(R.string.edit_event)
                    etEventName.setText(event.eventName)
                    etEventDesc.setText(event.eventDesc)
                    loadEventTypes(event.eventType)
                    etEventDateStart.setText(eventStartDateFormat)
                    etEventDateEnd.setText(eventEndDateFormat)
                    etEventStreet.setText(event.eventStreet)
                    etEventCity.setText(event.eventCity)
                    loadFriends(event.userIdScan)

                    if (event.eventImg != "null") {
                        imagePath = event.eventImg

                        Glide.with(binding.root.context)
                            .load(event.eventImg)
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    ivEvent.setImageDrawable(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    ivEvent.setImageDrawable(errorDrawable)
                                }
                            })

                        ivEvent.visibility = View.VISIBLE
                        tvAddImage.text = getString(R.string.edit_image)
                        tvDeleteImage.visibility = View.VISIBLE

                    }

                    showData()

                }
            }

        }
    }

    private suspend fun deleteEvents(eventId: String){
        Log.d(Constants.GIL_TAG,"Event: $eventId")
        DialogUtils.showLoadingDialog(requireContext())
        if (Utils.isConnectedNow(requireContext())){
            try {
                val eventDto = EventDto(eventId = eventId)
                val deleteEvent = eventRepository.deleteEventApi(eventDto)
                if (deleteEvent.isSuccessful) {
                    eventRepository.deleteEventDB(eventId)
                    getActivityRootView()?.showSnackBar(getString(R.string.event_cancel_success))
                    DialogUtils.dismissLoadingDialog()
                    findNavController().navigateUp()
                }
            }catch (e: Exception){
                Log.d(Constants.GIL_TAG, "Error Delete:")
                getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                DialogUtils.dismissLoadingDialog()
            }
        }else{
            eventRepository.updateEventDelete(eventId)
            getActivityRootView()?.showSnackBar(getString(R.string.event_cancel_success))
            DialogUtils.dismissLoadingDialog()
            findNavController().navigateUp()
        }
    }


    private fun validateInputs() : Boolean{
        binding.apply {
            if (etEventName.text.toString().isBlank()){
                lbEventName.error = getString(R.string.required_field)
                return false
            }

            if (etEventDesc.text.toString().isBlank()){
                lbEventDesc.error = getString(R.string.required_field)
                return false
            }

            if (eventType.isBlank()){
                lbEventType.error = getString(R.string.required_field)
                return false
            }

            if (etEventDateStart.text.toString().isBlank()){
                lbEventDateStart.error = getString(R.string.required_field)
                return false
            }

            if(!Utils.isDateEarlierToday(etEventDateStart.text.toString(), strDateFormat)){
                lbEventDateStart.error = getString(R.string.invalid_date)
                return false
            }

            if (etEventDateEnd.text.toString().isBlank()){
                lbEventDateEnd.error = getString(R.string.required_field)
                return false
            }

            if (etEventDateStart.text.toString() > etEventDateEnd.text.toString()){
                lbEventDateEnd.error = getString(R.string.invalid_date_bigger)
                return false
            }

            if (etEventStreet.text.toString().isBlank()){
                lbEventStreet.error = getString(R.string.required_field)
                return false
            }

            if (etEventCity.text.toString().isBlank()){
                lbEventCity.error = getString(R.string.required_field)
                return false
            }

            val selectFriend = listOfFriends.find { it.contactName == acUserScan.text.toString() }
            if (selectFriend == null){
                Log.d(Constants.GIL_TAG, selectFriend.toString())
                acUserScan.text.clear()
                lbUserScan.error = getString(R.string.required_field)
                return false
            }
        }

        return true
    }


    private fun loadEventTypes(typeSelected : String){
        val typesEvents = Utils.getEventTypes(requireContext())

        binding.acEventType.setDropDownBackgroundResource(android.R.color.transparent)


        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, typesEvents)
        binding.acEventType.setAdapter(adapter)

        if (typesEvents.contains(typeSelected)) {
            binding.acEventType.setText(typeSelected, false)
            eventType = typeSelected
        }

    }

    private fun loadFriends(friendSelected : String){
        lifecycleScope.launch {
            if (Utils.isConnectedNow(requireContext())) {

                val updateFriends = contactRepository.getFriendsToContacts(userPreferences.getUserId().toString())
                if (updateFriends.isSuccessful){
                    contactRepository.deleteFriendsToContacts()
                    val contactList = updateFriends.body() ?: emptyList()
                    val contacts = contactList.map { contact ->
                        contact.toEntity()
                    }

                    contactRepository.insertContactList(contacts)
                    listOfFriends = contacts
                }


                val friends = contactRepository.getFriendsApi(userPreferences.getUserId().toString(), "A")
                binding.acUserScan.setDropDownBackgroundResource(android.R.color.transparent)

                if (friends.isSuccessful) {
                    Log.d(Constants.GIL_TAG, "Select Friends")
                    val friendList = friends.body()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        friendList?.map { it.contactName } ?: emptyList())
                    binding.acUserScan.setAdapter(adapter)

                    val selectFriend = listOfFriends.find { it.contactId == friendSelected }
                    if (selectFriend == null) {
                        Log.d(Constants.GIL_TAG, "Sin valor:")
                    }else{
                        userIdScan = selectFriend.contactId
                        binding.acUserScan.setText(selectFriend.contactName, false)
                        Log.d(Constants.GIL_TAG, "Selecciono: ${userIdScan}")
                    }

                }
            }else{
                val friends = contactRepository.getFriendsDB()
                binding.acUserScan.setDropDownBackgroundResource(android.R.color.transparent)
                if (friends.isNotEmpty()){
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        friends.map { it.contactName }
                    )
                    binding.acUserScan.setAdapter(adapter)
                    listOfFriends = friends
                    val selectFriend = listOfFriends.find { it.contactId == friendSelected }

                    if (selectFriend == null) {
                        Log.d(Constants.GIL_TAG, "Sin valor")
                    }else{
                        userIdScan = selectFriend.contactId
                        binding.acUserScan.setText(selectFriend.contactName, false)
                        Log.d(Constants.GIL_TAG, "Selecciono: ${userIdScan}")
                    }
                }
            }

        }
    }

    /*private fun showCalendar(){
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .build()

        binding.spDate.visibility = View.VISIBLE
        picker.show(parentFragmentManager, "DATE_PICKER")

        picker.addOnDismissListener {
            binding.spDate.visibility = View.GONE
        }

        picker.addOnPositiveButtonClickListener { selection ->
            binding.spDate.visibility = View.GONE

            val startDate = selection.first
            val endDate = selection.second

            val formatter = SimpleDateFormat(strDateFormat , Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")

            val formattedDateIni = formatter.format(Date(startDate))
            val formattedDateEnd = formatter.format(Date(endDate))
            binding.etEventDateStart.setText(formattedDateIni)
            binding.etEventDateEnd.setText(formattedDateEnd)
        }
    }*/

    private fun showStartDateTimePicker() {
        val calendarStart = Calendar.getInstance()

        // Selecciona fecha de inicio
        DatePickerDialog(requireContext(),
            R.style.CustomDatePickerDialogStyle,
            { _, year, month, dayOfMonth ->
                calendarStart.set(year, month, dayOfMonth)

                // Luego selecciona hora de inicio
                TimePickerDialog(requireContext(),
                    R.style.CustomDatePickerDialogStyle,
                    { _, hourOfDay, minute ->
                        calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendarStart.set(Calendar.MINUTE, minute)

                        val formatter = SimpleDateFormat(strDateFormat, Locale.getDefault())
                        binding.etEventDateStart.setText(formatter.format(calendarStart.time))

                    },
                    calendarStart.get(Calendar.HOUR_OF_DAY),
                    calendarStart.get(Calendar.MINUTE),
                    true
                ).show()

            },
            calendarStart.get(Calendar.YEAR),
            calendarStart.get(Calendar.MONTH),
            calendarStart.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showEndDateTimePicker() {
        val calendarEnd = Calendar.getInstance()

        // Selecciona fecha de fin
        DatePickerDialog(requireContext(),
            R.style.CustomDatePickerDialogStyle,
            { _, year, month, dayOfMonth ->
                calendarEnd.set(year, month, dayOfMonth)

                // Luego selecciona hora de fin
                TimePickerDialog(requireContext(),
                    R.style.CustomDatePickerDialogStyle,
                    { _, hourOfDay, minute ->
                        calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendarEnd.set(Calendar.MINUTE, minute)

                        // Finalmente, formateamos y mostramos los valores
                        val formatter = SimpleDateFormat(strDateFormat, Locale.getDefault())
                        binding.etEventDateEnd.setText(formatter.format(calendarEnd.time))

                    },
                    calendarEnd.get(Calendar.HOUR_OF_DAY),
                    calendarEnd.get(Calendar.MINUTE),
                    true
                ).show()

            },
            calendarEnd.get(Calendar.YEAR),
            calendarEnd.get(Calendar.MONTH),
            calendarEnd.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etEventName, lbEventName)
            Utils.setupFocusAndTextListener(etEventDesc, lbEventDesc)
            Utils.setupFocusAndTextListener(acEventType, lbEventType)
            Utils.setupFocusAndTextListener(etEventDateStart, lbEventDateStart)
            Utils.setupFocusAndTextListener(etEventDateEnd, lbEventDateEnd)
            Utils.setupFocusAndTextListener(etEventStreet, lbEventStreet)
            Utils.setupFocusAndTextListener(etEventCity, lbEventCity)
            Utils.setupFocusAndTextListener(acUserScan, lbUserScan)
        }
    }



    private fun showData() {
        binding.viewEventDetLoad.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }

    private fun backAction() {
        binding.imgBtBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
        }
        binding.tvBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
        }
    }

    private fun goMyGuests() {
        binding.tvAddGuests.setOnClickListener { addGuests ->
            if (Utils.isConnectedNow(requireContext())) {
                addGuests.applyClickAnimation()
                binding.tvAddGuests.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent
                    )
                )
                findNavController().navigate(
                    EventsDetailFragmentDirections.actionEventsDetailFragmentToGuestsFragment(
                        eventId,
                        binding.etEventName.text.toString()
                    )
                )
            }else{
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.eventsFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}