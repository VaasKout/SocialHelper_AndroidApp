package com.example.socialhelper.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentDialogMapBinding

class DialogMap : Fragment() {

    /**
     * Fragment that shows Moscow subway map
     * when WheelChair makes an order or SocialWorker
     * takes an order for better orientation
     *
     * this Fragment uses 'com.github.MikeOrtiz:TouchImageView:3.0.3'
     * library to change the scale of the map
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDialogMapBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_dialog_map, container, false)

        binding.closeMap.setOnClickListener {
            this.findNavController().popBackStack()
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}