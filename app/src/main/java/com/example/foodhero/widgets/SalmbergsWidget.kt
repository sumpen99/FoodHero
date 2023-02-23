package com.example.foodhero.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.example.foodhero.R
import com.example.foodhero.global.format

class SalmbergsWidget(
    name:String,
    price:Double,
    id:String,
    // val callbackUnCheck:(args:String)->Unit,
    context: Context?,
    attrs: AttributeSet?): LinearLayout(context,attrs) {

    init{
        LinearLayout.inflate(context, R.layout.salmberg_layout,this)
        val nameTextView: TextView = this.findViewById(R.id.nameTextView)
        val priceTextView: TextView = this.findViewById(R.id.priceTextView)

        nameTextView.text = name
        priceTextView.text = price.format(2)

    }

}