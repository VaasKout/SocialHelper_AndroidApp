package com.example.socialhelper.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = loginViewModel
        val categoryList = resources.getStringArray(R.array.category)

//        var userInfo = loginViewModel.allInfo.value

// Navigation to fragment_pregnant
            loginViewModel.navigateToMainFrag.observe(viewLifecycleOwner, {
            val userInput = binding.userInputEditText.text.toString()
            val passwordInput = binding.passwordInputEditText.text.toString()

            binding.userTextInput.error = null
            binding.passwordTextInput.error = null

            if (userInput.isEmpty()){
                binding.userTextInput.error = getString(R.string.user_input_error)}
            if (passwordInput.isEmpty()){
                binding.passwordTextInput.error = getString(R.string.password_input_error)}

            if (it == true &&
                userInput.isNotEmpty() &&
                passwordInput.isNotEmpty()) {

                loginViewModel.allInfo.observe(viewLifecycleOwner, {info ->

                        lifecycleScope.launch {
                            if (info == null){
                            loginViewModel.connectToServer()
                            loginViewModel.requestServer()
                            if (!loginViewModel.readWrite.socket.isConnected){
                                Snackbar.make(binding.loginNextButton,
                                    getString(R.string.retry_later),
                                    Snackbar.LENGTH_SHORT).show()
                            } else if (loginViewModel.check == 0){
                                binding.userTextInput.error = getString(R.string.wrong_login_or_password)
                                binding.passwordTextInput.error = getString(R.string.wrong_login_or_password)
                            }
                        }
                            if (info != null){
                                when {
                                    info.login != userInput -> {
                                        binding.userTextInput.error = getString(R.string.wrong_login)
                                    }
                                    info.password != passwordInput -> {
                                        binding.passwordTextInput.error = getString(R.string.wrong_password)
                                    }
                                    else -> {
                                        when(info.group){
                                            categoryList[0] -> {
                                                this@LoginFragment.findNavController()
                                                    .navigate(LoginFragmentDirections.
                                                    actionLoginFragmentToDisabledFragment())
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                            categoryList[1] -> {
                                                this@LoginFragment.findNavController()
                                                    .navigate(LoginFragmentDirections
                                                        .actionLoginFragmentToPregnantFragment())
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                            categoryList[2] -> {
                                                this@LoginFragment.findNavController()
                                                    .navigate(LoginFragmentDirections
                                                        .actionLoginFragmentToSocialFragment())
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            })

        loginViewModel.navigateToSignInFrag.observe(viewLifecycleOwner, {
            if (it == true){
                loginViewModel.onClear()
                this.findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
                loginViewModel.onDoneNavigationToSign()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}