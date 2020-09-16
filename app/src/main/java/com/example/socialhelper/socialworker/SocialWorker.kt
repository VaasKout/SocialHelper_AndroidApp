package com.example.socialhelper.socialworker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialhelper.R
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.databinding.FragmentSocialWorkerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SocialWorker : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentSocialWorkerBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_social_worker, container, false)
        val viewModel = ViewModelProvider(this)
            .get(SocialViewModel::class.java)

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

        /**
         * Test
         */

        val data1 = WheelData(
            id = 1,
            name = "Bruh1",
            first = "Динамо",
            second = "Выхино",
            time = "14:30",
            comment = "u gay",
        )

        val data2 = WheelData(
            id = 2,
            name = "Bruh2",
            first = "Выхино",
            second = "Динамо",
            time = "4:20",
        )

        viewModel.onInsert(data1)
        viewModel.onInsert(data2)
        /**
         * Test
         */

        val socAdapter = SocialAdapter()
        binding.recyclerView.apply {
            adapter = socAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL,
                false)
        }

        viewModel.allData.observe(viewLifecycleOwner, {
            it?.let {
                socAdapter.submitList(it)
            }
        })


        socAdapter.viewAdapter.observe(viewLifecycleOwner, {adapter ->
            if (viewModel.onProcess() && !socAdapter.currentList[adapter.adapterPosition].checked){
                adapter.binding.recyclerButton.isEnabled = false
                adapter.binding.materialCard.strokeColor =
                    ContextCompat.getColor(requireContext(), R.color.disabled)
                adapter.binding.view.setBackgroundResource(R.color.disabled)
                adapter.binding.view2.setBackgroundResource(R.color.disabled)
                adapter.binding.recyclerButton
                    .setTextColor(ContextCompat
                        .getColor(requireContext(), R.color.disabled)
                    )
                adapter.binding.recyclerButton
                    .setIconTintResource(R.color.disabled)

            } else
                if (viewModel.onProcess() &&
                    socAdapter.currentList[adapter.adapterPosition].checked){

                adapter.binding.recyclerButton.text =
                    getString(R.string.to_complete)
                adapter.binding.materialCard.strokeColor =
                    ContextCompat.getColor(requireContext(), R.color.colorInProcess)
                adapter.binding.view.setBackgroundResource(R.color.colorInProcess)
                adapter.binding.view2.setBackgroundResource(R.color.colorInProcess)
                adapter.binding.recyclerButton
                    .setTextColor(ContextCompat
                        .getColor(requireContext(), R.color.colorInProcess)
                    )
                adapter.binding.recyclerButton.setIconTintResource(R.color.colorInProcess)
            } else
                    if (!viewModel.onProcess() &&
                        !socAdapter.currentList[adapter.adapterPosition].checked){
                        adapter.binding.materialCard.strokeColor =
                            ContextCompat.getColor(requireContext(),
                                R.color.colorPrimary)
                        adapter.binding.view
                            .setBackgroundResource(R.color.colorPrimary)
                        adapter.binding.view2
                            .setBackgroundResource(R.color.colorPrimary)
                        adapter.binding.recyclerButton
                            .setIconTintResource(R.color.colorPrimary)
                        adapter.binding.recyclerButton
                            .setTextColor(ContextCompat
                                .getColor(requireContext(), R.color.colorPrimary)
                            )
                adapter.binding.recyclerButton.isEnabled = true
            }
            adapter.binding.recyclerButton.setOnClickListener {
                if (!viewModel.onProcess()){
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Принять заказ?")
                            .setNegativeButton("Нет") { _, _ ->
                            }
                            .setPositiveButton("Да") { _, _ ->
                                lifecycleScope.launch {
                                    socAdapter.currentList[adapter.adapterPosition].checked = true
                                    viewModel.updateData(socAdapter.
                                    currentList[adapter.adapterPosition])
                                    /**
                                     *
                                     */
                                    socAdapter.notifyDataSetChanged()
                                }
                            }.show()
                } else if (viewModel.onProcess()){
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выполнить заказ?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            lifecycleScope.launch {
                                socAdapter.currentList[adapter.adapterPosition].checked = false
                                viewModel.updateData(socAdapter.
                                currentList[adapter.adapterPosition])
                                adapter.binding.recyclerButton.visibility = View.GONE
                                adapter.binding.textCompleted.visibility = View.VISIBLE

                                adapter.binding.materialCard.strokeColor =
                                    ContextCompat.getColor(requireContext(),
                                        R.color.colorPrimary)
                                adapter.binding.view
                                    .setBackgroundResource(R.color.colorPrimary)
                                adapter.binding.view2
                                    .setBackgroundResource(R.color.colorPrimary)
                                adapter.binding.recyclerButton
                                    .setIconTintResource(R.color.colorPrimary)
                                socAdapter.notifyDataSetChanged()
                                delay(3000)
                                viewModel.deleteData(socAdapter.currentList[adapter.adapterPosition])
                                /**
                                 *
                                 */
                            }
                        }.show()
                }
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}
