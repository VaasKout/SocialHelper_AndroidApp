package com.example.socialhelper.restoration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentRestorePasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RestorePassword : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val binding: FragmentRestorePasswordBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_restore_password, container, false)
        val viewModel
                = ViewModelProvider(this).get(RestoreViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateBackToLogin.observe(viewLifecycleOwner, {
            if (it == true){
                this.findNavController().popBackStack()
                viewModel.onDoneNavigateBack()
            }
        })

        viewModel.restoreNavigateToLogin.observe(viewLifecycleOwner, {
            if (it == true){

                val login = binding.loginRestoreEdit.text.toString()
                val post = binding.restoreEmailEdit.text.toString()

                if (login.isEmpty()){
                    lifecycleScope.launch {
                        binding.loginRestore.error = getString(R.string.empty_field_error)
                        viewModel.onDoneRestore()
                        delay(3000)
                        binding.loginRestore.error = null
                    }
                }

                if (post.isEmpty()){
                    lifecycleScope.launch{
                        binding.emailRestore.error = getString(R.string.empty_field_error)
                        viewModel.onDoneRestore()
                        delay(3000)
                        binding.emailRestore.error = null
                    }
                }

                if (login.isNotEmpty() &&
                    post.isNotEmpty()){
                    lifecycleScope.launch {
                        viewModel.loginRestore = login
                        viewModel.postRestore = post
                        viewModel.connectToServer()
                        viewModel.requestServer()
                        if (!viewModel.readWrite.socket.isConnected) {
                            Snackbar.make(
                                binding.sendRestoreRequestButton,
                                getString(R.string.retry_later),
                                Snackbar.LENGTH_SHORT).show()
                            viewModel.onDoneRestore()
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Пароль отправлен на почту")
                                .setMessage("Если письма нет в папке \"Входящие\", " +
                                            "проверьте папку \"Спам\"")
                                .setPositiveButton("Ок"){ _, _ ->
                                    this@RestorePassword.findNavController().popBackStack()
                                    viewModel.onDoneRestore()
                                }.show()
                        }
                    }
                }
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }
}