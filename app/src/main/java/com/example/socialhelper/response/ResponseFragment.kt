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
import com.example.socialhelper.databinding.FragmentResponseBinding

class ResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentResponseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_response, container, false)
        val viewModel =
            ViewModelProvider(this).get(ResponseViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.userInfo.observe(viewLifecycleOwner, {
            if (it.serverID > 0) {
                binding.respondText.text = getString(R.string.positive_response)

            } else {
                binding.respondText.text = getString(R.string.retry_register)
                binding.buttonEnterAccount.visibility = View.GONE
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

        viewModel.enter.observe(viewLifecycleOwner, {
            if (it == true){
                this.findNavController().navigate(ResponseFragmentDirections
                    .actionResponseFragmentToPregnantFragment())
                viewModel.onDoneEnterNavigation()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}