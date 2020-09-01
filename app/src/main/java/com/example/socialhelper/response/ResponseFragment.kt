package com.example.socialhelper.response

import android.icu.text.IDNA
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentResponceBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class ResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentResponceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_responce, container, false)
        val viewModel =
            ViewModelProvider(this).get(ResponseViewModel::class.java)
        binding.viewModel = viewModel

        if (viewModel.serverKey == 0) {
            viewModel.onServerKey()
        }

        viewModel.userInfo.observe(viewLifecycleOwner, {
            if (viewModel.serverKey != 0){
                val info = Info(it.id,
                    it.name,
                    it.password,
                    it.group,
                    it.serverID,
                    viewModel.serverKey)
                viewModel.onUpdate(info)
                binding.serverKey.text = it.serverKey.toString()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}