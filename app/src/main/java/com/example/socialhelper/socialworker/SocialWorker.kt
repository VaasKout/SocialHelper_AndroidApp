package com.example.socialhelper.socialworker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.google.android.material.snackbar.Snackbar
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

        fun connect(){
            lifecycleScope.launchWhenResumed {
                if (viewModel.readWrite.socket == null){
                    viewModel.connectToServer()
                }
                if (!viewModel.readWrite.socket.isConnected){
                    Snackbar.make(
                        binding.recyclerView,
                        getString(R.string.retry_later),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.triedToConnect = true
                } else {
                    if (viewModel.triedToConnect){
                        Snackbar.make(
                            binding.recyclerView,
                            getString(R.string.connected),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    while (viewModel.readWrite.socket.isConnected){
                        viewModel.readData()
                        delay(1000)
                    }
                }
            }
        }

        if (!viewModel.madeFirstConnection){
            connect()
            viewModel.madeFirstConnection = true
        }

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
                        .setMessage("Сменить пароль?")
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
                R.id.refresh -> {
                    connect()
                    true
                }
                R.id.map -> {
                    this.findNavController()
                        .navigate(SocialWorkerDirections
                            .actionSocialWorkerToDialogMap())
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
            first = "Библиотека им.Ленина",
            second = "Александровский сад",
            time = "14:30",
            comment = "u gay"
        )

        val data2 = WheelData(
            id = 2,
            name = "Bruh2",
            first = "Выхино",
            second = "Комсомольская",
            time = "4:20",
        )

        val data3 = WheelData(
            id = 3,
            name = "Bruh3",
            first = "Проспект Большевиков",
            second = "Проспект Большевиков",
            time = "Cейчас",
            comment = "No, u"
        )

        viewModel.onInsert(data1)
        viewModel.onInsert(data2)
        viewModel.onInsert(data3)
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

            fun changeTheme(id: Int){
                adapter.binding.materialCard.strokeColor =
                    ContextCompat.getColor(requireContext(), id)
                adapter.binding.view.setBackgroundResource(id)
                adapter.binding.view2.setBackgroundResource(id)
                adapter.binding.recyclerButton
                    .setTextColor(ContextCompat
                        .getColor(requireContext(), id)
                    )
                adapter.binding.recyclerButton
                    .setIconTintResource(id)
                adapter.binding.textStatus
                    .setTextColor(ContextCompat
                        .getColor(requireContext(), id)
                    )
            }

            if (viewModel.onProcess() && !socAdapter.currentList[adapter.adapterPosition].checked){
                adapter.binding.recyclerButton.isEnabled = false
                changeTheme(R.color.disabled)
                adapter.binding.textStatus.text =
                    getString(R.string.status_wait)

            } else
                if (viewModel.onProcess() &&
                    socAdapter.currentList[adapter.adapterPosition].checked){
                adapter.binding.recyclerButton.text =
                    getString(R.string.to_complete)
                    changeTheme(R.color.colorInProcess)
                    adapter.binding.textStatus.text =
                        getString(R.string.status_in_progress)
                    adapter.binding.buttonCancelRecycler.visibility = View.VISIBLE
            } else if (!viewModel.onProcess() &&
                        !socAdapter.currentList[adapter.adapterPosition].checked){
                        changeTheme(R.color.colorPrimary)
                        adapter.binding.recyclerButton.isEnabled = true
                        adapter.binding.buttonCancelRecycler.visibility = View.GONE
            }

            adapter.binding.buttonCancelRecycler.setOnClickListener {
                if (viewModel.onProcess()){
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Принять заказ?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            /**
                             * cancel order
                             */
                            lifecycleScope.launch {
                                if (viewModel.readWrite.socket.isConnected){
                                    viewModel.cancelOrder()
                                } else{
                                    Snackbar.make(
                                        binding.recyclerView,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.triedToConnect = true
                                }
                            }
                        }.show()
                }
            }
            adapter.binding.recyclerButton.setOnClickListener {
                if (!viewModel.onProcess()){
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Принять заказ?")
                            .setNegativeButton("Нет") { _, _ ->
                            }
                            .setPositiveButton("Да") { _, _ ->
                                lifecycleScope.launch {
                                    /**
                                     *   order received
                                     */

                                    if (viewModel.readWrite.socket.isConnected){
                                        viewModel.acceptOrder()
                                        socAdapter.currentList[adapter.adapterPosition].checked = true
                                        viewModel.updateData(socAdapter.
                                        currentList[adapter.adapterPosition])
                                        socAdapter.notifyDataSetChanged()
                                    } else {
                                        Snackbar.make(
                                            binding.recyclerView,
                                            getString(R.string.retry_later),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        viewModel.triedToConnect = true
                                    }
                                }
                            }.show()
                } else if (viewModel.onProcess()){
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выполнить заказ?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            lifecycleScope.launch {
                                /**
                                 *   order is complete
                                 */
                                if (viewModel.readWrite.socket.isConnected){
                                    viewModel.completeOrder()
                                    adapter.binding.recyclerButton.text = ""
                                    adapter.binding.recyclerButton.isEnabled = false
                                    changeTheme(R.color.colorAccent)
                                    adapter.binding.textStatus.text =
                                        getString(R.string.status_complete)
                                    delay(3000)
                                    socAdapter.currentList[adapter.adapterPosition].checked = false
                                    viewModel.updateData(socAdapter.
                                    currentList[adapter.adapterPosition])
                                    viewModel.deleteData(socAdapter.currentList[adapter.adapterPosition])
                                    socAdapter.notifyDataSetChanged()
                                } else {
                                    Snackbar.make(
                                        binding.recyclerView,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.triedToConnect = true
                                }
                            }
                        }.show()
                }
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }
}
