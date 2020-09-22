package com.example.socialhelper.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {
    /**
     * Fragment is used to sign in clients
     * @see R.layout.fragment_registration
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentRegistrationBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_registration, container, false)
        val viewModel =
            ViewModelProvider(this).get(RegistrationViewModel::class.java)
        binding.viewModel = viewModel

        /**
         * @see FragmentRegistrationBinding.toolbarReg
         * @see R.menu.top_bar_reg
         */
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
                        binding.numberReferenceInputEdit.setText("")
                        binding.surnameEditInput.setText("")
                        binding.loginEdit.setText("")
                        binding.emailEdit.setText("")
                        binding.exposeDownMenu.setText("")
                        viewModel.onClear()
                    }.show()
                true
            } else false
        }
        /**
         * textChangeListeners remove errors
         */

        binding.textInputUserEditReg.addTextChangedListener {
            binding.userInputReg.error = null
        }

        binding.textInputPassEditReg.addTextChangedListener {
            binding.passwordInputReg.error = null
        }

        binding.loginEdit.addTextChangedListener {
            binding.login.error = null
        }

        binding.surnameEditInput.addTextChangedListener{
            binding.surnameInputReg.error = null
        }

        binding.confirmPasswordEdit.addTextChangedListener {
            binding.confirmPassword.error = null
        }

        binding.emailEdit.addTextChangedListener {
            binding.email.error = null
        }

        binding.numberReferenceInputEdit.addTextChangedListener {
            binding.numberReference.error = null
        }


        /**
         * Check if all EditText fields is not empty else show error,
         * then app tries to connect to the server and send registration data
         *
         * App shows Snackbar if connection is failed and if user exist
         * or reference for Pregnant category is not found on the server
         *
         * if everything is correct navigate to KeyVerification fragment
         *
         * @see FragmentRegistrationBinding.regButton
         * @see com.example.socialhelper.verification.KeyVerification
         *
         *
         */
        viewModel.navigateToWait.observe(viewLifecycleOwner, {

            if (it == true) {
                val userName = binding.textInputUserEditReg.text.toString()
                val password = binding.textInputPassEditReg.text.toString()
                val passwordConfirm = binding.confirmPasswordEdit.text.toString()
                val numberEdit = binding.numberReferenceInputEdit.text.toString()
                val surname = binding.surnameEditInput.text.toString()
                val login = binding.loginEdit.text.toString()
                val email = binding.emailEdit.text.toString()
                val category = binding.exposeDownMenu.text.toString()



                if (userName.isEmpty()) {
                        binding.userInputReg.error = getString(R.string.user_input_error)
                        viewModel.onDoneNavigating()
                }

                if (password.isEmpty()) {
                        binding.passwordInputReg.error = getString(R.string.password_input_error)
                        viewModel.onDoneNavigating()
                }

                if (login.isEmpty()) {
                        binding.login.error = getString(R.string.enter_login)
                        viewModel.onDoneNavigating()
                }

                if (surname.isEmpty()) {
                        binding.surnameInputReg.error = getString(R.string.surname_input_error)
                        viewModel.onDoneNavigating()
                }

                if (passwordConfirm.isEmpty()) {
                        binding.confirmPassword.error = getString(R.string.confirm_password)
                        viewModel.onDoneNavigating()
                }

                if (email.isEmpty()) {
                        binding.email.error = "Введите почту"
                        viewModel.onDoneNavigating()

                } else if (email.isNotEmpty() &&
                    (!email.contains("@") ||
                            !email.contains("."))) {
                        binding.email.error = getString(R.string.wrong_email_input)
                }

                if (category.isEmpty()) {
                        binding.spinner.error = getString(R.string.choose_category)
                        viewModel.onDoneNavigating()
                }

                if (numberEdit.isEmpty() && binding.numberReference.isVisible) {
                        binding.numberReference.error = getString(R.string.enter_reference_number)
                        viewModel.onDoneNavigating()
                }

                if (passwordConfirm != password) {
                    binding.confirmPassword.error = getString(R.string.password_mismatch)
                    viewModel.onDoneNavigating()
                }

                if (userName.isNotEmpty() &&
                    (password.isNotEmpty() && password.length <= 8) &&
                    passwordConfirm.isNotEmpty() &&
                    (email.isNotEmpty() &&
                            email.contains("@") &&
                            email.contains(".")) &&
                    login.isNotEmpty() &&
                    surname.isNotEmpty() &&
                    binding.numberReference.error.isNullOrEmpty() &&
                    category.isNotEmpty() &&
                    password == passwordConfirm) {


                    val info = Info(
                        id = 1, name = userName, surname = surname,
                        password = password, login = login,
                        email = email, category = category,
                        reference = numberEdit.toInt())

                    viewModel.onInsert(info)

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Данные введены верно?")
                        .setNegativeButton("Нет") { _, _ ->
                            viewModel.onDoneNavigating()
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.allInfo.observe(viewLifecycleOwner, {info ->
                                lifecycleScope.launch {
                                    binding.regButton.isEnabled = false
                                    binding.regButton.text = getString(R.string.wait)
                                    viewModel.connectToServer()
                                    viewModel.requestServer()
                                    if (!viewModel.readWrite.socket.isConnected) {
                                        Snackbar.make(
                                            binding.regButton,
                                            getString(R.string.retry_later),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        binding.regButton.isEnabled = true
                                        binding.regButton.text = getString(R.string.register)
                                        viewModel.onDoneNavigating()
                                    } else {
                                        when (viewModel.state) {
                                            "wrong" -> {

                                                Log.e("state", viewModel.state)
                                                binding.login.error =
                                                    getString(R.string.try_another_login)
                                                binding.numberReference.error =
                                                    getString(R.string.check_reference)
                                                if (info.category == viewModel.categoryList[1]){
                                                    Snackbar.make(
                                                        binding.regButton,
                                                        getString(R.string.user_exist),
                                                        Snackbar.LENGTH_SHORT
                                                    ).show()
                                                } else{
                                                    Snackbar.make(
                                                        binding.regButton,
                                                        getString(R.string.pregnant_exist),
                                                        Snackbar.LENGTH_LONG
                                                    ).show()
                                                }
                                                binding.regButton.isEnabled = true
                                                binding.regButton.text = getString(R.string.register)
                                                viewModel.onDoneNavigating()
                                            } "wait" -> {
                                            Log.e("state", viewModel.state)
                                                val infoReference = Info(
                                                    id = info.id,
                                                    name = info.name,
                                                    surname = info.surname,
                                                    password = info.password,
                                                    login = info.login,
                                                    email = info.email,
                                                    reference = viewModel.referenceNumber,
                                                    needVerification = true,
                                                    category = category)
                                                viewModel.updateInfo(infoReference)
                                                this@RegistrationFragment.findNavController()
                                                    .navigate(
                                                        RegistrationFragmentDirections
                                                            .actionRegistrationFragmentToKeyVerification())
                                                viewModel.onDoneNavigating()
                                            } else ->{
                                            Log.e("error",
                                                "unknown response: ${viewModel.state}")
                                            Snackbar.make(
                                                binding.regButton,
                                                getString(R.string.retry_later),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                            binding.regButton.isEnabled = true
                                            binding.regButton.text = getString(R.string.register)
                                            viewModel.onDoneNavigating()
                                            }
                                        }
                                    }
                                }
                            })
                        }.show()
                }
            }
        })

        /**
         * Adapter and clickListener for
         * @see FragmentRegistrationBinding.spinner
         *
         * if category equals Pregnant
         * @see FragmentRegistrationBinding.numberReference appears
         * and user have to enter the number of reference
         */

        val application = requireNotNull(activity).application
        ArrayAdapter.createFromResource(
            application,
            R.array.category,
            R.layout.drop_down_menu
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.exposeDownMenu.setAdapter(it)
        }

        binding.exposeDownMenu.setOnItemClickListener { adapterView, _, position, _ ->
            when(adapterView.getItemAtPosition(position).toString()){
                viewModel.categoryList[0], viewModel.categoryList[2] -> {
                    binding.numberReference.visibility = View.GONE
                    binding.numberReference.error = null
                    binding.spinner.error = null
                }
                else -> {
                    binding.numberReference.visibility = View.VISIBLE
                    binding.spinner.error = null
                }
            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}