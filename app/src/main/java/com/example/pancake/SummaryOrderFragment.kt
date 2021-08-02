package com.example.pancake


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pancake.databinding.FragmentSummaryOrderBinding
import com.example.pancake.model.PancakeViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*


class SummaryOrderFragment : Fragment() {

    /**
     * Binding
     */
    private var binding: FragmentSummaryOrderBinding? = null

    /**
     * SharedViewModel
     */
    private val sharedViewModel: PancakeViewModel by activityViewModels()

    /**
     * RecyclerView
     */
    private lateinit var recyclerView: RecyclerView
    private lateinit var pancakeList: MutableList<Pancake>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSummaryOrderBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            summaryOrderFragment = this@SummaryOrderFragment
        }

        /**
         * Set the recyclerView
         * It was one of my problems, how to design nested vertical RecyclerView inside ScrollView in Android.
         * The answer is:  Use NestedScrollView instead ScrollView inside the layout file.
         * In the  layout file, add android:overScrollMode="never" attribute to recyclerView
         */
        pancakeList = sharedViewModel.cart
        recyclerView = binding!!.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PancakeViewModel.PancakeAdapter(pancakeList)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    fun datePicker() {
        // Date formatter
        val formatter = SimpleDateFormat("E, MMM dd", Locale.getDefault())
        // Create the date picker builder and set the title
        // Makes only dates from today forward selectable
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pick up date")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        // Set listener when date is selected
        datePickerBuilder.addOnPositiveButtonClickListener {
            // Create calendar object and set the date to be that returned from selection
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            val formattedCalendar = formatter.format(calendar.time)
            sharedViewModel.setDate(formattedCalendar)
        }
        datePickerBuilder.show(parentFragmentManager, "myTag")
    }


    fun sendOrder() {

        if(sharedViewModel.date.value != "") {
            var orderSummary = ""
            repeat(sharedViewModel.cart.size) {
                orderSummary +=
                            "${resources.getString(R.string.selected_type)}: ${sharedViewModel.cart[it].type}" +
                            "\n" +
                            "${resources.getString(R.string.selected_flavor)}: ${sharedViewModel.cart[it].flavor}" +
                            "\n" +
                            "${resources.getString(R.string.selected_quantity)}: ${sharedViewModel.cart[it].quantity}"+
                            "\n\n"
            }
            orderSummary += "\n ${resources.getString(R.string.selected_pick_up_date)}: ${sharedViewModel.date.value.toString()}" + "\n\n" + resources.getString(R.string.thank_you)

            val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.new_pancake_order))
                .putExtra(Intent.EXTRA_TEXT, orderSummary)

            if(activity?.packageManager?.resolveActivity(intent, 0) != null){
                startActivity(intent)
            }
        } else {
            Toast.makeText(requireContext(), "Please select a pick up date!", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelOrder() {
        sharedViewModel.resetOrder()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun cancelOrderDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel)
            .setMessage(R.string.cancel_message)
            .setPositiveButton(R.string.yes) {_,_ -> cancelOrder()}
            .setNegativeButton(R.string.no) {_,_ -> }
            .show()
    }

}