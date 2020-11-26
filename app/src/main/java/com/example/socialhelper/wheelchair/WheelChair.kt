package com.example.socialhelper.wheelchair

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WheelChair : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /**
         * WheelChair fragment where client can make an order to transfer in Moscow subway
         * @see R.layout.fragment_wheel_chair
         */

        val binding: FragmentWheelChairBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_wheel_chair, container, false)
        val viewModel =
            ViewModelProvider(this).get(WheelChairViewModel::class.java)
        binding.viewModel = viewModel
        binding.textTimeResult.text = viewModel.timeField

        /**
         * @see FragmentWheelChairBinding.toolbarWheelchair
         * @see R.menu.top_bar_social
         */
        binding.toolbarWheelchair.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.exit_from_main -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выйти из аккаунта?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->

                            this.findNavController()
                                .navigate(
                                    WheelChairDirections
                                        .actionWheelChairToLoginFragment())

                            viewModel.onClear()
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
                 * @see com.example.socialhelper.dialog.DialogMap
                 */
                R.id.map -> {
                    this.findNavController()
                        .navigate(WheelChairDirections.actionWheelChairToDialogMap())
                    true
                }
                else -> false
            }
        }

        /**
         * Arrays of stations and their images
         */
        val stations =
            arrayOf(
                resources.getStringArray(R.array.line_one),
                resources.getStringArray(R.array.line_two),
                resources.getStringArray(R.array.line_three),
                resources.getStringArray(R.array.line_four),
                resources.getStringArray(R.array.line_five),
                resources.getStringArray(R.array.line_six),
                resources.getStringArray(R.array.line_seven),
                resources.getStringArray(R.array.line_eight),
                resources.getStringArray(R.array.line_eight_a),
                resources.getStringArray(R.array.line_nine),
                resources.getStringArray(R.array.line_ten),
                resources.getStringArray(R.array.line_eleven),
                resources.getStringArray(R.array.line_twelve),
                resources.getStringArray(R.array.line_thirteen),
                resources.getStringArray(R.array.line_fourteen),
                resources.getStringArray(R.array.line_fifteen),
            )
        val img = arrayOf(
            R.drawable.circle_one,
            R.drawable.circle_two,
            R.drawable.circle_three,
            R.drawable.circle_four,
            R.drawable.circle_five,
            R.drawable.circle_six,
            R.drawable.circle_seven,
            R.drawable.circle_eight,
            R.drawable.circle_eight,
            R.drawable.circle_nine,
            R.drawable.circle_ten,
            R.drawable.circle_eleven,
            R.drawable.circle_twelve,
            R.drawable.circle_thirteen,
            R.drawable.circle_fourteen,
            R.drawable.circle_fifteen,
        )
        val allStations = mutableListOf("")
        for (line in stations){
            for (st in line){
                allStations.add(st)
            }
        }
        /**
         * Time picker
         */

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
                        viewModel.timeField =
                            SimpleDateFormat("HH:mm", Locale.ENGLISH).format(cal.time)
                    }
                    TimePickerDialog(
                        context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE), true
                    ).show()
                }
                else -> {
                    binding.textTimeResult.text = ""
                    viewModel.timeField = binding.textTimeResult.text.toString()
                }
            }
        }

        /**
         * Send order to the server and navigate to WheelChairWait
         * @see com.example.socialhelper.wheelchair.WheelChairWait
         */

        viewModel.send.observe(viewLifecycleOwner, {
            if (it == true) {
                val startStation = binding.startStationEdit.text.toString()
                val endStation = binding.endStationEdit.text.toString()
                var time = binding.timeDropdown.text.toString()
                val explicitTime = binding.textTimeResult.text.toString()
                val comment = binding.commentEdit.text.toString()

                if (startStation.isEmpty()) {
                    binding.startStation.error = getString(R.string.empty_field_error)
                    viewModel.onDoneSending()
                }

                if (endStation.isEmpty()) {
                    binding.endStation.error = getString(R.string.empty_field_error)
                    viewModel.onDoneSending()
                }

                if (time.isEmpty()) {
                    binding.timeMenu.error = getString(R.string.empty_field_error)
                    viewModel.onDoneSending()
                }
                if ( !allStations.contains(startStation) &&
                    startStation.isNotEmpty()){
                    binding.startStation.error =
                        "Выберите станцию, предложенную в выпадающем списке"
                    viewModel.onDoneSending()
                }

                if (!allStations.contains(endStation) &&
                    endStation.isNotEmpty()){
                    binding.endStation.error =
                        "Выберите станцию, предложенную в выпадающем списке"
                    viewModel.onDoneSending()
                }

                if (startStation.isNotEmpty() &&
                    endStation.isNotEmpty() &&
                    time.isNotEmpty() &&
                    allStations.contains(startStation) &&
                    allStations.contains(endStation)) {

                    viewModel.allInfo.observe(viewLifecycleOwner, { info ->
                    if (explicitTime.isNotEmpty()){
                        time = explicitTime
                    }
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

                                    lifecycleScope.launch {
                                    viewModel.data.observe(viewLifecycleOwner, {
                                        val sendData = WheelData(
                                            id = 1,
                                            name = info.name,
                                            first = startStation,
                                            second = endStation,
                                            time = time,
                                            comment = comment,
                                            ordered = true
                                        )
                                        viewModel.onUpdate(sendData)
                                    })

                                        binding.buttonHelpRequest.isEnabled = false
                                        binding.buttonHelpRequest.text =
                                            getString(R.string.wait)
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
                                        if (this@WheelChair
                                                .findNavController()
                                                .currentDestination?.id ==
                                            R.id.wheelChair){
                                            this@WheelChair
                                                .findNavController()
                                                .navigate(
                                                    WheelChairDirections
                                                        .actionWheelChairToWheelChairWait()
                                                )
                                            viewModel.onDoneSending()
                                            }
                                        }
                                    }
                            }.show()
                    })
                }
            }
        })


        fun setImg(text: String, imageView: ImageView, textView: TextView){

            imageView.setImageResource(R.drawable.circle)
            textView.text = ""
            for(line in stations.indices){
                if (stations[line].contains(text)){
                    imageView.setImageResource(img[line])
                    when {
                        line < 7 -> {
                            textView.text = (line+1).toString()
                            textView.setTextColor(
                                ContextCompat
                                    .getColor(requireContext(), R.color.text_white)
                            )

                        }
                        line in 7..8 -> {
                            textView.setTextColor(
                                ContextCompat
                                    .getColor(requireContext(), R.color.text_black)
                            )
                            if (line == 7){
                                textView.text = (line+1).toString()
                            }
                            else textView.text = getString(R.string.eight_a)

                        } line in 9..stations.size -> {
                            textView.text = line.toString()
                        if (line == 14){
                            textView.setTextColor(
                                ContextCompat
                                    .getColor(requireContext(), R.color.text_black)
                            )
                        } else{
                            textView.setTextColor(
                                ContextCompat
                                    .getColor(requireContext(), R.color.text_white)
                            )
                                }
                         }
                    }
                }
            }
        }

        /**
         * Adapter for AutoCompleteTextViews with all stations from Moscow subway
         * @see FragmentWheelChairBinding.startStation
         * @see FragmentWheelChairBinding.endStation
         */
        val adapter =  ArrayAdapter(
            requireContext(),
            R.layout.drop_down_menu, allStations
        )

        binding.startStationEdit.setAdapter(adapter)
        binding.endStationEdit.setAdapter(adapter)

        val inputManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        /**
         * textChangeListener removes EditText errors from fields
         */

        binding.timeDropdown.addTextChangedListener {
            binding.timeMenu.error = null
        }


        binding.startStationEdit.addTextChangedListener {
            binding.startStation.error = null
            setImg(
                binding.startStationEdit.text.toString(),
                binding.circleFirstStation,
                binding.textFirstCircle
            )
        }
        binding.endStationEdit.addTextChangedListener {
            binding.endStation.error = null
            setImg(
                binding.endStationEdit.text.toString(),
                binding.circleSecondStation,
                binding.textSecondCircle
            )
        }

        /**
         * set *hideSoftInputFromWindow()* only in itemClickListener to prevent bug,
         * because *fun setImg()* hides keyboard, when you try to type again after
         * the name of the station is selected
         */


        binding.startStationEdit.setOnItemClickListener { _, _, _, _ ->
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }

        binding.endStationEdit.setOnItemClickListener { _, _, _, _ ->
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }


        binding.lifecycleOwner = this
        return binding.root
    }
}

