package com.example.socialhelper.pregnant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentPregnantBinding
import com.example.socialhelper.network.ReadWrite
import java.io.IOException
import java.net.ConnectException

class PregnantFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentPregnantBinding=
            DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant, container, false)
        val viewModel = ViewModelProvider(this).get(PregnantViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.spotIsFree.observe(viewLifecycleOwner, {
            if (it == true) {
                viewModel.onRequest()
                viewModel.onDoneSetSpotFree()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}