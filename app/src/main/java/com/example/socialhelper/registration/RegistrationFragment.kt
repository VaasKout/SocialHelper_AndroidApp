package com.example.socialhelper.registration


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

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
        val categoryList = resources.getStringArray(R.array.category)

        binding.toolbarReg.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.toolbarReg.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.clear) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Очистить поля ввода?")
                    .setNegativeButton("Нет") { _, _ ->
                    }
                    .setPositiveButton("Да") { _, _ ->
                        binding.textInputUserEditReg.setText("")
                        binding.textInputPassEditReg.setText("")
                        binding.confirmPasswordEdit.setText("")
                        binding.exposeDownMenu.setText("")
                        binding.numberReferenceInputEdit.setText("")
                        binding.surnameEditInput.setText("")
                        binding.loginEdit.setText("")
                        binding.numberReference.visibility = View.GONE
                        viewModel.onClear()
                    }.show()
                true
            } else false
        }

        viewModel.navigateToWait.observe(viewLifecycleOwner, {

            if (it == true) {
                val userName = binding.textInputUserEditReg.text.toString()
                val password = binding.textInputPassEditReg.text.toString()
                val passwordConfirm = binding.confirmPasswordEdit.text.toString()
                val category = binding.exposeDownMenu.text.toString()
                val numberEdit = binding.numberReferenceInputEdit.text.toString()
                val surname = binding.surnameEditInput.text.toString()
                val login = binding.loginEdit.text.toString()
                val email = binding.emailEdit.text.toString()
                val number = binding.numberReference


                if (userName.isEmpty()) {
                    lifecycleScope.launch {
                        binding.userInputReg.error = getString(R.string.user_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.userInputReg.error = null

                    }
                }

                if (password.isEmpty()) {
                    lifecycleScope.launch {
                        binding.passwordInputReg.error = getString(R.string.password_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.passwordInputReg.error = null
                    }
                }

                if (category.isEmpty()) {
                    lifecycleScope.launch {
                        binding.spinner.error = getString(R.string.choose_category)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.spinner.error = null

                    }
                }

                if (login.isEmpty()) {
                    lifecycleScope.launch {
                        binding.login.error = getString(R.string.enter_login)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.login.error = null
                    }
                }

                if (surname.isEmpty()) {
                    lifecycleScope.launch {
                        binding.surnameInputReg.error = getString(R.string.surname_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.surnameInputReg.error = null
                    }
                }

                if (passwordConfirm.isEmpty()) {
                    lifecycleScope.launch {
                        binding.confirmPassword.error = getString(R.string.confirm_password)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.confirmPassword.error = null
                    }
                }

                if (email.isEmpty()) {
                    lifecycleScope.launch {
                        binding.email.error = "Введите почту"
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.email.error = null
                    }
                } else if (email.isNotEmpty() &&
                    (!email.contains("@") || !email.contains("."))
                ) {
                    lifecycleScope.launch {
                        binding.email.error = getString(R.string.wrong_email_input)
                        delay(3000)
                        binding.email.error = null
                    }
                }


                if (numberEdit.isEmpty() && number.isVisible) {
                    lifecycleScope.launch {
                        number.error = getString(R.string.enter_reference_number)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        number.error = null
                    }
                } else if (number.isGone) {
                    number.error = null
                }


                if (passwordConfirm != password) {
                    binding.confirmPassword.error = getString(R.string.password_mismatch)
                    viewModel.onDoneNavigating()
                }

                if (userName.isNotEmpty() &&
                    (password.isNotEmpty() && password.length <= 8) &&
                    passwordConfirm.isNotEmpty() &&
                    login.isNotEmpty() &&
                    category.isNotEmpty() &&
                    surname.isNotEmpty() &&
                    number.error.isNullOrEmpty() &&
                    password == passwordConfirm
                ) {

                    var referenceNumber = 0

                    var info = Info(
                        id = 1, name = userName, surname = surname,
                        password = password, group = category,
                        login = login, email = email, wasLoggedIn = false
                    )

                    if (number.isVisible) {
                        info = Info(
                            id = 1, name = userName, surname = surname,
                            password = password, group = category,
                            reference = numberEdit.toInt(),
                            login = login, email = email, wasLoggedIn = false
                        )

                        referenceNumber = numberEdit.toInt()
                    }

                    viewModel.onInsert(info)

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Данные введены верно?")
                        .setNegativeButton("Нет") { _, _ ->
                            viewModel.onDoneNavigating()
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.allInfo.observe(viewLifecycleOwner, {
                                lifecycleScope.launch {
                                    viewModel.connectToServer()
                                    viewModel.requestServer()
                                    if (!viewModel.readWrite.socket.isConnected) {
                                        Snackbar.make(
                                            binding.materialButton,
                                            getString(R.string.retry_later),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        viewModel.onDoneNavigating()
                                    } else {
                                        info = Info(
                                            id = 1,
                                            name = userName,
                                            surname = surname,
                                            password = password,
                                            group = category,
                                            login = login,
                                            email = email,
                                            reference = referenceNumber,
                                            needVerification = true
                                        )
                                        viewModel.updateInfo(info)

//                                Log.e("serverID", viewModel.serverId.toString())
//                                Log.e("serverKey", viewModel.serverKey.toString())

                                        this@RegistrationFragment.findNavController()
                                            .navigate(
                                                RegistrationFragmentDirections
                                                    .actionRegistrationFragmentToKeyVerification()
                                            )

                                        viewModel.onDoneNavigating()
                                    }
                                }
                            })
                        }.show()
                }
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

        binding.exposeDownMenu.setOnItemClickListener { adapterView, _, position, _ ->
            when (adapterView.getItemAtPosition(position).toString()) {
                categoryList[0], categoryList[2] -> {
                    binding.numberReference.visibility = View.GONE
                    binding.exposeDownMenu.error = null
                }
                else -> {
                    binding.numberReference.visibility = View.VISIBLE
                    binding.exposeDownMenu.error = null
                }
            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}