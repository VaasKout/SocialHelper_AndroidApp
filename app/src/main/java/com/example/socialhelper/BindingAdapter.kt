package com.example.socialhelper

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text

@BindingAdapter("intToText")
fun TextView.setText(int: Int){
    this.text = int.toString()
}
@BindingAdapter("liveIntText")
fun TextView.setLiveDataText(value: LiveData<Int>){
    if (value.value != null){
        this.text = value.value.toString()
    } else{
        this.text = "-1"
    }
}