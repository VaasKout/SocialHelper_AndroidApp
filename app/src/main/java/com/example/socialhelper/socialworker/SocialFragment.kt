package com.example.socialhelper.socialworker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentSocialBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentSocialBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_social, container, false)
        val viewModel =
            ViewModelProvider(this).get(SocialViewModel::class.java)
        binding.viewModel = viewModel
        // Inflate the layout for this fragment

        viewModel.exit.observe(viewLifecycleOwner, {
            if (it == true){
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Выйти из аккаунта")
                    .setNegativeButton("Нет") { _, _ ->
                        viewModel.onDoneExit()
                    }
                    .setPositiveButton("Да") { _, _ ->
                        viewModel.onClear()
                        this.findNavController()
                            .navigate(
                                SocialFragmentDirections
                                    .actionSocialFragmentToLoginFragment())
                        viewModel.onDoneExit()
                    }.show()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}