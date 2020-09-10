package com.example.socialhelper.response


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentResponceBinding

class ResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentResponceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_responce, container, false)
        val viewModel =
            ViewModelProvider(this).get(ResponseViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.userInfo.observe(viewLifecycleOwner, {
            if (it.serverID == 0 || it.serverID == -1) {
                binding.respondText.text = getString(R.string.registration_denied)
            } else {
                binding.respondText.text = getString(R.string.positive_response)
            }
        })

        viewModel.navigateBack.observe(viewLifecycleOwner, {
            if (it == true) {
                this.findNavController()
                    .navigate(ResponseFragmentDirections
                        .actionResponseFragmentToLoginFragment())
                viewModel.onDoneBackNavigation()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}