package com.example.socialhelper.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentLoginBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_login, container, false)
        val viewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        val categoryList = resources.getStringArray(R.array.category)
        val categoryEng = resources.getStringArray(R.array.categoryEng)

        binding.loginInputEditText.addTextChangedListener {
            binding.loginTextInput.error = null
        }

        binding.passwordInputEditText.addTextChangedListener {
            binding.passwordTextInput.error = null
        }

        viewModel.navigateToMainFrag.observe(viewLifecycleOwner, {
            if (it == true) {
                val login = binding.loginInputEditText.text.toString()
                val passwordInput = binding.passwordInputEditText.text.toString()

                binding.loginTextInput.error = null
                binding.passwordTextInput.error = null

                if (login.isEmpty()) {
                        binding.loginTextInput.error = getString(R.string.user_input_error)
                        viewModel.onDoneNavigationToMain()
                }
                if (passwordInput.isEmpty()) {
                        binding.passwordTextInput.error = getString(R.string.password_input_error)
                        viewModel.onDoneNavigationToMain()
                }

                if (login.isNotEmpty() &&
                    passwordInput.isNotEmpty()) {

                    viewModel.login = login
                    viewModel.password = passwordInput.toInt()
                    viewModel.allInfo.observe(viewLifecycleOwner, { info ->

                        lifecycleScope.launch {
                            if (info == null) {
                                viewModel.connectToServer()
                                viewModel.requestServer()
                                if (!viewModel.readWrite.socket.isConnected) {
                                    Snackbar.make(
                                        binding.loginNextButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.onDoneNavigationToMain()

                                } else if (viewModel.serverID <= 0 ||
                                    viewModel.serverKey <= 0
                                ) {
                                        binding.loginTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        binding.passwordTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        binding.forgotPassword.visibility = View.VISIBLE
                                        viewModel.onDoneNavigationToMain()
                                }
                            }
                            if (info != null) {
                                if (info.serverID <= 0 && info.serverKey <= 0) {
                                        binding.loginTextInput.error =
                                            getString(R.string.wrong_login)
                                        binding.passwordTextInput.error =
                                            getString(R.string.wrong_password)
                                        viewModel.onDoneNavigationToMain()
                                } else {
                                    when {
                                        info.login != login -> {
                                            binding.loginTextInput.error =
                                                getString(R.string.wrong_login)
                                            viewModel.onDoneNavigationToMain()
                                        }
                                        info.password != passwordInput -> {
                                            binding.passwordTextInput.error =
                                                getString(R.string.wrong_password)
                                            viewModel.onDoneNavigationToMain()
                                            binding.forgotPassword.visibility = View.VISIBLE
                                        }
                                        else -> {
                                            Log.e("go", "loging")
                                            binding.loginTextInput.error = null
                                            binding.passwordTextInput.error = null

                                            if (this@LoginFragment
                                                    .findNavController()
                                                    .currentDestination?.id ==
                                                R.id.loginFragment
                                            ) {
                                                when (info.category) {
                                                    categoryList[0], categoryEng[0] -> {
                                                      this@LoginFragment.findNavController()
                                                          .navigate(LoginFragmentDirections
                                                              .actionLoginFragmentToWheelChair())
                                                        viewModel.onDoneNavigationToMain()
                                                    }
                                                    categoryList[1], categoryEng[1] -> {
                                                        this@LoginFragment.findNavController()
                                                            .navigate(LoginFragmentDirections
                                                                .actionLoginFragmentToPregnantFragment())
                                                        viewModel.onDoneNavigationToMain()
                                                    }
                                                    categoryList[2], categoryEng[2] -> {
                                                       this@LoginFragment.findNavController()
                                                           .navigate(LoginFragmentDirections
                                                               .actionLoginFragmentToSocialWorker())
                                                        viewModel.onDoneNavigationToMain()
                                                    }
                                                }
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

                    viewModel.navigateToSignInFrag.observe(viewLifecycleOwner, {
                        if (it == true) {
                            viewModel.allInfo.let {
                                viewModel.onClear()
                            }
                            if (this@LoginFragment
                                    .findNavController()
                                    .currentDestination?.id ==
                                R.id.loginFragment) {
                                this@LoginFragment.findNavController()
                                    .navigate(LoginFragmentDirections
                                        .actionLoginFragmentToRegistrationFragment())
                                viewModel.onDoneNavigationToSign()
                            }
                        }
                    })

                    viewModel.navigateToRestoreFrag.observe(viewLifecycleOwner, {
                        if (it == true) {
                            this.findNavController()
                                .navigate(LoginFragmentDirections.actionLoginFragmentToRestorePassword())
                            viewModel.onDoneNavigationToRestore()
                        }
                    })

                    binding.lifecycleOwner = this
                    return binding.root
                }
        }