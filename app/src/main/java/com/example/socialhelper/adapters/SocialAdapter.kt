package com.example.socialhelper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialhelper.R
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.databinding.RecyclerItemBinding

/**
 * Adapter for recyclerview in SocialWorker fragment
 * it extends ListAdapter class, which is inherited from RecyclerView
 * @see androidx.recyclerview.widget.ListAdapter
 * @see com.example.socialhelper.ui.SocialWorkerFragment
 */

class SocialAdapter :
    ListAdapter<WheelData, SocialAdapter.ViewHolder>(NoteDiffCallBack()) {

    //LiveData
    private val _viewAdapter = MutableLiveData<ViewHolder>()
    val viewAdapter: LiveData<ViewHolder> = _viewAdapter

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RecyclerItemBinding = DataBindingUtil
            .inflate(layoutInflater, R.layout.recycler_item, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wheelData: WheelData) {
            binding.wheelData = wheelData
            _viewAdapter.value = this
        }
    }
}

class NoteDiffCallBack : DiffUtil.ItemCallback<WheelData>() {
    override fun areItemsTheSame(oldItem: WheelData, newItem: WheelData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WheelData, newItem: WheelData): Boolean {
        return oldItem == newItem
    }
}
