package com.example.socialhelper.socialworker

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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SocialWorker : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        
        /**
         * SocialWorker client fragment
         * @see R.layout.fragment_social_worker
         * This fragment is based on recycler view,
         * which updates when server send a new order
         * or SocialWorker complete one
         */

        val binding: FragmentSocialWorkerBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_social_worker, container, false)
        val viewModel = ViewModelProvider(this)
            .get(SocialViewModel::class.java)

        /**
         * Method connects app to the sever when
         * client enters or press update on the toolbar
         */
        fun connect(){
            lifecycleScope.launch {
                    viewModel.connectToServer()
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
                        val data = WheelData(
                            id = 0,
                            name = viewModel.name,
                            first = viewModel.first,
                            second = viewModel.second,
                            time = viewModel.time,
                            comment = viewModel.comment
                        )
                        viewModel.insert(data)
                        delay(1000)
                    }
                }
            }
        }

        if (!viewModel.madeFirstConnection){
            connect()
            viewModel.madeFirstConnection = true
        }
        /**
         * @see FragmentSocialWorkerBinding.toolbarSocial
         * @see R.menu.top_bar_social
         *
         */
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
                    viewModel.deleteAll()
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
         * Define adapter for recyclerView
         */
        val socAdapter = SocialAdapter()
        binding.recyclerView.apply {
            adapter = socAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL,
                false)
        }
        /**
         * Set list from WheelData database to adapter
         */
        viewModel.data.observe(viewLifecycleOwner, {
            it?.let {
                socAdapter.submitList(listOf(it))
            }
        })

        /**
         * Set different colors for recycler item states
         * if one order has been taken, every other is disabled
         *
         * Adapter uses LiveData<ViewHolder> to setOnClickListener for buttons on every item
         *
         * @see R.layout.recycler_item
         * @see com.example.socialhelper.socialworker.SocialAdapter
         */
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
            } else if (!viewModel.onProcess() &&
                        !socAdapter.currentList[adapter.adapterPosition].checked){
                        changeTheme(R.color.colorPrimary)
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
                                    /**
                                     *  order accepted
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
                                    viewModel.deleteData()
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
