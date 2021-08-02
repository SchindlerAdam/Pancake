package com.example.pancake


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pancake.databinding.FragmentPancakeCreatorBinding
import com.example.pancake.model.PancakeViewModel


class PancakeCreatorFragment : Fragment() {

    /**
     * Binding
     */
    private var binding: FragmentPancakeCreatorBinding? = null

    /**
     * SharedViewModel
     */
    private val sharedViewModel: PancakeViewModel by activityViewModels()


    /**
     * Temporary variable to set quantity
     */
    private var tempSetQuantity = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentPancakeCreatorBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            panCakeCreatorFragment = this@PancakeCreatorFragment
        }
    }

    fun quantityPlus() {
        tempSetQuantity++
        sharedViewModel.setQuantity(tempSetQuantity)
    }

    fun quantityMinus() {
        if(tempSetQuantity > 0) {
            tempSetQuantity--
            sharedViewModel.setQuantity(tempSetQuantity)
        }
    }

    fun addToCart() {
        if(sharedViewModel.checkAllDataExist()) {
            sharedViewModel.addCart()
        }else {
            Toast.makeText(requireContext(), "Select all required data!", Toast.LENGTH_LONG).show()
        }
    }



    fun goToNextFragment() {
        if(sharedViewModel.checkAllDataExist()) {
            val action = PancakeCreatorFragmentDirections.actionPancakeCreatorFragmentToSummaryOrderFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Select all required data!", Toast.LENGTH_LONG).show()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}