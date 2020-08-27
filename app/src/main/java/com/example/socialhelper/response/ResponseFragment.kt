package com.example.socialhelper.response

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentResponceBinding

class ResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentResponceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_responce, container, false)

        binding.lifecycleOwner = this
        return binding.root
    }
}