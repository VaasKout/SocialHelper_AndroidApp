package com.example.socialhelper.response

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
        viewModel.onServerKey()

    lifecycleScope.launch {
        delay(2000)
        if (viewModel.readWrite.isAlive) {
            binding.serverKey.text = viewModel.serverKey.toString()
            Log.e("serverKey", viewModel.serverKey.toString())
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.retry_later),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

        binding.lifecycleOwner = this
        return binding.root
    }
}