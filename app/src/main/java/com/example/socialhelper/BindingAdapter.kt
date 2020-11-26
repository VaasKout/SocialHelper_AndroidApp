package com.example.socialhelper

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.socialhelper.database.WheelData

@BindingAdapter ("android:commentContent")
    fun TextView.setContent(wheelData: WheelData?){
    wheelData?.let {
        if (it.comment.isEmpty()) visibility = View.GONE
        else text = context.getString(R.string.full_comment, it.comment)
    }
}