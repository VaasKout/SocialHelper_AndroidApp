package com.example.socialhelper.socialworker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.databinding.FragmentSocialWorkerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SocialWorker : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentSocialWorkerBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_social_worker, container, false)
        val viewModel = ViewModelProvider(this)
            .get(SocialViewModel::class.java)

        val data1 = WheelData(name = "Серафим", first = "Динамо",
            second = "ВДНХ", time = "14:30", comment = " ")

        val data2 = WheelData(name = "Михаил", first = "ВДНХ",
            second = "Динамо", time = "15:50", comment = " ")

        val data = listOf(data1, data2)
        binding.toolbarSocial.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.exit_from_main -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выйти из аккаунта?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            this.findNavController()
                                .navigate(
                                    SocialWorkerDirections
                                        .actionSocialWorkerToLoginFragment()
                                )
                        }.show()
                    true
                }

                R.id.change_pass -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выйти из аккаунта?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            this.findNavController()
                                .navigate(
                                    SocialWorkerDirections
                                        .actionSocialWorkerToChangePassword()
                                )
                        }.show()
                    true
                }
                else -> false
            }
        }

        val adapter = SocialAdapter()
        binding.socialRecycler.setHasFixedSize(true)
        binding.socialRecycler.adapter = adapter
        adapter.data = data
        adapter.notifyDataSetChanged()
        lifecycleScope.launch {
//           while (!viewModel.readWrite.socket.isConnected){
//               viewModel.connectToServer()
//           }
//            while(viewModel.readWrite.socket.isConnected){
//                viewModel.requestServer()
//                viewModel.data.observe(viewLifecycleOwner, {
//                    adapter.data = data
//                    adapter.notifyDataSetChanged()
//                })
//            }
        }





        binding.lifecycleOwner = this
        return binding.root
    }
}
