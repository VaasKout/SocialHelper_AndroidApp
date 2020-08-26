package com.example.socialhelper.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.loginViewModel = loginViewModel

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


            /**
             * Navigation to fragments depends on username(for now)
             */
            if (it == true && userInput.isNotEmpty() && passwordInput.isNotEmpty()){
                if(userInput == "1") {
                    this.findNavController()
                        .navigate(LoginFragmentDirections.actionLoginFragmentToPregnantFragment())
                    loginViewModel.onDoneNavigationToMain()
                }
                    if (userInput == "2"){
                        this.findNavController()
                            .navigate(LoginFragmentDirections.actionLoginFragmentToDisabledFragment())
                        loginViewModel.onDoneNavigationToMain()
                    }
                if (userInput == "3"){
                    this.findNavController()
                        .navigate(LoginFragmentDirections.actionLoginFragmentToSocialFragment())
                    loginViewModel.onDoneNavigationToMain()
                }
            }
        })
//        Navigation to fragment_registration

        loginViewModel.navigateToSignInFrag.observe(viewLifecycleOwner, {
            if (it == true){
                this.findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
                loginViewModel.onDoneNavigationToSign()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}