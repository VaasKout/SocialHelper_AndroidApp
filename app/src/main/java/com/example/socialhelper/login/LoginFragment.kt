package com.example.socialhelper.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentLoginBinding.inflate(inflater)
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = loginViewModel

        val categoryList =
            resources.getStringArray(R.array.categoryEng)

        loginViewModel.navigateToMainFrag.observe(viewLifecycleOwner, {
            if (it == true) {
                val userInput = binding.userInputEditText.text.toString()
                val passwordInput = binding.passwordInputEditText.text.toString()

                binding.userTextInput.error = null
                binding.passwordTextInput.error = null

                if (userInput.isEmpty()) {
                    lifecycleScope.launch {
                        binding.userTextInput.error = getString(R.string.user_input_error)
                        loginViewModel.onDoneNavigationToMain()
                        delay(3000)
                        binding.userTextInput.error = null
                    }

                }
                if (passwordInput.isEmpty()) {
                    lifecycleScope.launch {
                        binding.passwordTextInput.error = getString(R.string.password_input_error)
                        loginViewModel.onDoneNavigationToMain()
                        delay(3000)
                        binding.passwordTextInput.error = null
                    }
                }

                if (userInput.isNotEmpty() &&
                    passwordInput.isNotEmpty()) {

                    loginViewModel.login = userInput
                    loginViewModel.password = passwordInput.toInt()
                    loginViewModel.allInfo.observe(viewLifecycleOwner, { info ->

                        lifecycleScope.launch {
                            if (info == null) {
                                loginViewModel.connectToServer()
                                loginViewModel.requestServer()
                                if (!loginViewModel.readWrite.socket.isConnected) {
                                    Snackbar.make(
                                        binding.loginNextButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT).show()
                                    loginViewModel.onDoneNavigationToMain()

                                } else if (loginViewModel.serverID <= 0 ||
                                            loginViewModel.serverKey <= 0) {
                                    lifecycleScope.launch {
                                        binding.userTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        binding.passwordTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        loginViewModel.onDoneNavigationToMain()

                                        delay(3000)
                                        binding.userTextInput.error = null
                                        binding.passwordTextInput.error = null
                                    }
                                }
                            }
                            if (info != null) {
                                when {
                                    info.login != userInput -> {
                                        binding.userTextInput.error =
                                            getString(R.string.wrong_login)
                                    }
                                    info.password != passwordInput -> {
                                        binding.passwordTextInput.error =
                                            getString(R.string.wrong_password)
                                    }
                                    else -> {
                                        Log.e("info", "info is not null")
                                        when (info.group) {
                                            categoryList[0] -> {
                                                val infoInstance = Info(
                                                    id = 1,
                                                    name = info.name,
                                                    surname = info.surname,
                                                    login = info.login,
                                                    password = info.password,
                                                    group = info.group,
                                                    serverID = info.serverID,
                                                    serverKey = info.serverKey,
                                                    wasLoggedIn = true)
                                                loginViewModel.onUpdate(infoInstance)

                                                this@LoginFragment.findNavController()
                                                    .navigate(
                                                        LoginFragmentDirections
                                                            .actionLoginFragmentToDisabledFragment())
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                            categoryList[1] -> {
                                                Log.e("pregnant", "eee")
                                                val infoInstance = Info(
                                                    id = 1,
                                                    name = info.name,
                                                    surname = info.surname,
                                                    login = info.login,
                                                    password = info.password,
                                                    group = info.group,
                                                    serverID = info.serverID,
                                                    serverKey = info.serverKey,
                                                    wasLoggedIn = true)
                                                loginViewModel.onUpdate(infoInstance)

                                                this@LoginFragment.findNavController()
                                                    .navigate(
                                                        LoginFragmentDirections
                                                            .actionLoginFragmentToPregnantFragment())
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                            categoryList[2] -> {
                                                val infoInstance = Info(
                                                    id = 1,
                                                    name = info.name,
                                                    surname = info.surname,
                                                    login = info.login,
                                                    password = info.password,
                                                    group = info.group,
                                                    serverID = info.serverID,
                                                    serverKey = info.serverKey,
                                                    wasLoggedIn = true)
                                                loginViewModel.onUpdate(infoInstance)

                                                this@LoginFragment.findNavController()
                                                    .navigate(
                                                        LoginFragmentDirections
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
            }
        })

        loginViewModel.navigateToSignInFrag.observe(viewLifecycleOwner, {
            if (it == true) {
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