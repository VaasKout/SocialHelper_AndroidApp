package com.example.socialhelper.wheelchair

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.databinding.FragmentWheelChairBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WheelChair : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentWheelChairBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_wheel_chair, container, false)
        val viewModel =
            ViewModelProvider(this).get(WheelChairViewModel::class.java)
        binding.viewModel = viewModel
        binding.textTimeResult.text = viewModel.timeField

        binding.toolbarWheelchair.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.exit_from_main -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выйти из аккаунта?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.onClear()
                            this.findNavController()
                                .navigate(
                                    WheelChairDirections
                                        .actionWheelChairToLoginFragment()
                                )
                        }.show()

                    true
                }
                R.id.change_pass -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Сменить пароль?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            this.findNavController()
                                .navigate(
                                    WheelChairDirections
                                        .actionWheelChairToChangePassword()
                                )
                        }.show()
                    true
                }
                /**
                 * map here
                 */
                R.id.map -> {
                    this.findNavController()
                        .navigate(WheelChairDirections.actionWheelChairToDialogMap())
                    true
                }
                else -> false
            }
        }

        val application = requireNotNull(activity).application
        val timeArray = resources.getStringArray(R.array.time)
        ArrayAdapter.createFromResource(
            application,
            R.array.time,
            R.layout.drop_down_menu
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.timeDropdown.setAdapter(it)
        }

        binding.timeDropdown.setOnItemClickListener { adapterView, _, position, _ ->
            when(adapterView.getItemAtPosition(position).toString()){
                timeArray[1] -> {
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                         binding.textTimeResult.text =
                             SimpleDateFormat("HH:mm", Locale.ENGLISH).format(cal.time)
                        viewModel.timeField = binding.textTimeResult.text.toString()
                    }
                    TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE), true).show()
                    }
                else -> {
                    binding.textTimeResult.text = ""
                    viewModel.timeField = binding.textTimeResult.text.toString()
                }
            }
        }

        viewModel.send.observe(viewLifecycleOwner, {
            if (it == true){
                val startStation = binding.editStartStation.text.toString()
                val endStation = binding.endStationEdit.text.toString()
                val time = binding.timeDropdown.text.toString()
                val comment = binding.commentEdit.text.toString()

                if (startStation.isEmpty()) {
                    lifecycleScope.launch {
                        binding.startStation.error = getString(R.string.empty_field_error)
                        viewModel.onDoneSending()
                        delay(3000)
                        binding.startStation.error = null
                    }
                }

                if (endStation.isEmpty()) {
                    lifecycleScope.launch {
                        binding.endStation.error = getString(R.string.empty_field_error)
                        viewModel.onDoneSending()
                        delay(3000)
                        binding.endStation.error = null
                    }
                }

                if (time.isEmpty()) {
                    lifecycleScope.launch {
                        binding.timeMenu.error = getString(R.string.empty_field_error)
                        viewModel.onDoneSending()
                        delay(3000)
                        binding.timeMenu.error = null
                    }
                }

                if (startStation.isNotEmpty() &&
                        endStation.isNotEmpty() &&
                        time.isNotEmpty()){
                        viewModel.allInfo.observe(viewLifecycleOwner, {info ->
                                    val data = WheelData(
                                        id = 1,
                                        name = info.name,
                                        first = startStation,
                                        second = endStation,
                                        time = time,
                                        comment = comment
                                    )
                                viewModel.onInsert(data)

                                MaterialAlertDialogBuilder(requireContext())
                                        .setMessage("Данные введены верно?")
                                        .setNegativeButton("Нет") { _, _ ->
                                            viewModel.onDoneSending()
                                        }
                                        .setPositiveButton("Да") { _, _ ->
                                            viewModel.data.observe(viewLifecycleOwner, {
                                        lifecycleScope.launch {
                                            binding.buttonHelpRequest.isEnabled = false
                                            binding.buttonHelpRequest.text = getString(R.string.wait)
                                            viewModel.connectToServer()
                                            viewModel.requestServer()
                                            if (!viewModel.readWrite.socket.isConnected) {
                                                Snackbar.make(
                                                    binding.buttonHelpRequest,
                                                    getString(R.string.retry_later),
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                                binding.buttonHelpRequest.isEnabled = true
                                                binding.buttonHelpRequest.text =
                                                    getString(R.string.send)
                                                viewModel.onDoneSending()
                                            } else {
                                                this@WheelChair
                                                    .findNavController()
                                                    .navigate(WheelChairDirections
                                                        .actionWheelChairToWheelChairWait())
                                                viewModel.onDoneSending()
                                            }
                                        }
                                            })
                                        }.show()

                        })
                }
            }
        })
        binding.lifecycleOwner = this
        return binding.root
    }
}