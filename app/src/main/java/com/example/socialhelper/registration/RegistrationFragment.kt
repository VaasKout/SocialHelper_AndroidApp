package com.example.socialhelper.registration

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isNotEmpty
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
import kotlinx.coroutines.launch

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
        val categoryList = resources.getStringArray(R.array.category)

        /**
         * Handle Server Request
         */

        viewModel.navigateToWait.observe(viewLifecycleOwner, {
            val userName = binding.textInputUserEditReg.text.toString()
            val password = binding.textInputPassEditReg.text.toString()
            val category = binding.exposeDownMenu.text.toString()
            val number = binding.numberReference
            val numberEdit = binding.numberReferenceInputEdit.text.toString()
            val surname = binding.surnameEditInput.text.toString()

            binding.userInputReg.error = null
            binding.passwordInputReg.error = null
            binding.exposeDownMenu.error = null
            binding.surnameInputReg.error = null
            number.error = null


            if (userName.isEmpty())
                binding.userInputReg.error = getString(R.string.user_input_error)
            if(password.isEmpty())
                binding.passwordInputReg.error = getString(R.string.password_input_error)
            if (numberEdit.isEmpty())
                number.error = getString(R.string.choose_category)
            if (surname.isEmpty())
                binding.surnameInputReg.error = getString(R.string.surname_input_error)
            if (numberEdit.isEmpty() && number.isVisible){
                number.error = getString(R.string.enter_reference_number)
            } else if (number.isGone){
                number.error = null
            }

            if (it == true &&
                userName.isNotEmpty() &&
                password.isNotEmpty() &&
                category.isNotEmpty() &&
                surname.isNotEmpty()  &&
                number.error.isNullOrEmpty()){

                var info = Info(id = 1, name = userName, surname = surname,
                                password = password, group = category)

                if (number.isVisible){
                     info = Info(id = 1, name = userName, surname = surname,
                                password = password, group = category,
                                reference = numberEdit.toInt())
                }

                viewModel.onInsert(info)

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Данные введены верно?")
                        .setNegativeButton("Нет"){ _, _ ->
                        }
                        .setPositiveButton("Да"){ _, _ ->

                            lifecycleScope.launch {
                              viewModel.connectToServer()
                                viewModel.requestServer()
                                if (!viewModel.readWrite.socket.isConnected && viewModel.serverId == 0){
                                    Snackbar.make(binding.materialButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT).show()
                                } else {
                                    info = Info(
                                        id = 1,
                                        name = userName,
                                        surname = surname,
                                        password = password,
                                        group = category,
                                        serverID = viewModel.serverId,
                                        serverKey = viewModel.serverKey)
                                    viewModel.onUpdate(info)
                                    Log.e("serverID", viewModel.serverId.toString())
                                    Log.e("serverKey", viewModel.serverKey.toString())

                                    this@RegistrationFragment.findNavController()
                                        .navigate(
                                            RegistrationFragmentDirections.
                                            actionRegistrationFragmentToResponseFragment())
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

        binding.exposeDownMenu.setOnItemClickListener { adapterView, _, position, _ ->
            when(adapterView.getItemAtPosition(position).toString()){
                categoryList[0], categoryList[2] -> {
                    binding.numberReference.visibility = View.GONE
                    binding.numberReferenceInputEdit.visibility = View.GONE
                }
                else -> {
                    binding.numberReference.visibility = View.VISIBLE
                    binding.numberReferenceInputEdit.visibility = View.VISIBLE
                }
            }
        }


        binding.lifecycleOwner = this
        return binding.root
    }
}