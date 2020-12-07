package com.example.socialhelper.ui


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
import com.example.socialhelper.viewmodels.ResponseViewModel

class ResponseFragment : Fragment() {
    /**
     * Fragment is used to show, that you've registered successfully
     * @see R.layout.fragment_response
     * it shows your name, surname and category
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentResponseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_response, container, false)
        val viewModel =
            ViewModelProvider(this).get(ResponseViewModel::class.java)
        binding.viewModel = viewModel
        val categoryList = resources.getStringArray(R.array.category)


        viewModel.userInfo.observe(viewLifecycleOwner, {
            if (it.serverID > 0) {
                binding.respondText.text = getString(R.string.positive_response)

            } else {
                binding.respondText.text = getString(R.string.retry_register)
                binding.buttonEnterAccount.visibility = View.GONE
            }

            viewModel.enter.observe(viewLifecycleOwner, { enter ->
                if (enter == true && it.serverID > 0) {
                    when (it.category) {
                        categoryList[0] -> {
                            this.findNavController()
                                .navigate(ResponseFragmentDirections.actionResponseFragmentToWheelChair())
                            viewModel.onDoneEnterNavigation()
                        }
                        categoryList[1] -> {
                            this.findNavController()
                                .navigate(ResponseFragmentDirections.actionResponseFragmentToPregnantFragment())
                            viewModel.onDoneEnterNavigation()
                        }
                        categoryList[2] -> {
                            this.findNavController()
                                .navigate(ResponseFragmentDirections.actionResponseFragmentToSocialWorker())
                            viewModel.onDoneEnterNavigation()
                        }
                    }
                }
            })
        })

        viewModel.navigateBack.observe(viewLifecycleOwner, {
            if (it == true) {
                this.findNavController()
                    .navigate(ResponseFragmentDirections.actionResponseFragmentToLoginFragment())
                viewModel.onDoneBackNavigation()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}