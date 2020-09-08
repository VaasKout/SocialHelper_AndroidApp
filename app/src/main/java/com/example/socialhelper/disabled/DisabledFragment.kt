package com.example.socialhelper.disabled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentDisabledBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DisabledFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDisabledBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_disabled, container, false)
        val viewModel =
            ViewModelProvider(this).get(DisabledViewModel::class.java)
        binding.viewModel = viewModel
        // Inflate the layout for this fragment

        viewModel.exit.observe(viewLifecycleOwner, {
            if (it == true){
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Выйти из приложения?")
                    .setNegativeButton("Нет") { _, _ ->
                        viewModel.onDoneExit()
                    }
                    .setPositiveButton("Да") { _, _ ->
                        viewModel.onClear()
                        this.findNavController()
                            .navigate(DisabledFragmentDirections
                                    .actionDisabledFragmentToLoginFragment())
                        viewModel.onDoneExit()
                    }.show()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }

}