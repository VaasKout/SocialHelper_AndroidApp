package com.example.socialhelper.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentDialogMapBinding

class DialogMap : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDialogMapBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_dialog_map, container, false)
        val viewModel =
            ViewModelProvider(this).get(DialogViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.close.observe(viewLifecycleOwner, {
            if (it == true){
                this.findNavController().popBackStack()
                viewModel.onDoneClose()
            }
        })

        // Inflate the layout for this fragment
        binding.lifecycleOwner = this
        return binding.root
    }
}