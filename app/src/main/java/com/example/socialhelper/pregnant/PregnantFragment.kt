package com.example.socialhelper.pregnant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentPregnantBinding

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
            if (it == true){
                binding.result.visibility = View.VISIBLE
                viewModel.onDoneSetSpotFree()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}