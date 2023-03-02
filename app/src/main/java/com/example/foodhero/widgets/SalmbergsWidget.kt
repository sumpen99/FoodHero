package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.foodhero.R
import com.example.foodhero.global.format


@SuppressLint("ViewConstructor")
class SalmbergsWidget(
    name:String,

    val price:Double,
    val id:String,
    val callbackDelete:(args:Any?)->Unit,
    context: Context?,
    attrs: AttributeSet?): LinearLayout(context,attrs) {

    init{
        LinearLayout.inflate(context, R.layout.salmberg_layout,this)
        val nameTextView: TextView = this.findViewById(R.id.nameTextView)
        val priceTextView: TextView = this.findViewById(R.id.priceTextView)
        val deleteButton: ImageButton = this.findViewById(R.id.deleteItemButton)

        nameTextView.text = name
        priceTextView.text = price.format(2) + "Kr"

        deleteButton.setOnClickListener {
            callbackDelete(this)
        }
    }
}