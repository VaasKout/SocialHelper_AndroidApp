package com.example.socialhelper.registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.IOException

class RegistrationFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val binding: FragmentRegistrationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)
        val application = requireNotNull(this.activity).application
        val viewModel =
            ViewModelProvider(this).get(RegistrationViewModel::class.java)
        binding.viewModel = viewModel



        /**
         * Handle Server Request
         */

        viewModel.navigateToWait.observe(viewLifecycleOwner, {
            val userName = binding.textInputUserEditReg.text.toString()
            val password = binding.textInputPassEditReg.text.toString()
            val category = binding.exposeDownMenu.text.toString()

            binding.userInputReg.error = null
            binding.passwordInputReg.error = null
            if (userName.isEmpty())binding.userInputReg.error = getString(R.string.user_input_error)
            if(password.isEmpty())binding.passwordInputReg.error = getString(R.string.password_input_error)

            if (it == true && userName.isNotEmpty() && password.isNotEmpty() && category.isNotEmpty()){

                var info =
                    Info(id = 1, name = userName, password = password, group = category, key = 0)
                viewModel.onInsert(info)



                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Данные введены верно?")
                        .setNegativeButton("Нет"){ _, _ ->
                        }
                        .setPositiveButton("Да"){ _, _ ->
                            viewModel.onRequestServer()
                            lifecycleScope.launch {
                                delay(1000)
                                if (!viewModel.readWrite.isAlive && viewModel.keyId == 0){
                                    Snackbar.make(binding.materialButton, getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT).show()
                                } else {
                                    info = Info(id = 1,
                                        name = userName,
                                        password = password,
                                        group = category,
                                        viewModel.keyId)
                                    viewModel.onUpdate(info)
                                    Log.e("regKey", viewModel.keyId.toString())
                                    this@RegistrationFragment.findNavController()
                                        .navigate(
                                            RegistrationFragmentDirections.
                                            actionRegistrationFragmentToResponseFragment()
                                        )
                                    viewModel.onDoneNavigating()
                                }
                            }

                        }.show()
                    }
                 })

        /**
         * DropDownMenu
         */

        ArrayAdapter.createFromResource(
            application,
            R.array.category,
            R.layout.drop_down_menu
        ).also {
           it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.exposeDownMenu.setAdapter(it)
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}