package com.jbg.gil.features.newevent.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentNewEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NewEventFragment : Fragment() {

    private  var _binding : FragmentNewEventBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typesEvents = listOf(getString(R.string.event_category_social),
                             getString(R.string.event_category_corporate),
                             getString(R.string.event_category_academic),
                             getString(R.string.event_category_other)
                            )

        binding.autoCompleteTextView.setDropDownBackgroundResource(android.R.color.transparent)


        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, typesEvents)
        binding.autoCompleteTextView.setAdapter(adapter)

        // Optional: Handle item selected
        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            //Toast.makeText(requireContext(), "Selected: $selected", Toast.LENGTH_SHORT).show()
        }

        binding.etEventDateIni.setOnClickListener {
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
                // formatear y mostrar las fechas

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")

                val formattedDateIni = formatter.format(Date(startDate))
                val formattedDateEnd = formatter.format(Date(endDate))
                binding.etEventDateIni.setText(formattedDateIni)
                binding.etEventDateEnd.setText(formattedDateEnd)
            }

        }

        binding.etEventDateEnd.setOnClickListener {
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
                // formatear y mostrar las fechas

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")

                val formattedDateIni = formatter.format(Date(startDate))
                val formattedDateEnd = formatter.format(Date(endDate))
                binding.etEventDateIni.setText(formattedDateIni)
                binding.etEventDateEnd.setText(formattedDateEnd)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}