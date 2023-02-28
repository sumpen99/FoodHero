package com.example.foodhero.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.foodhero.R
import com.example.foodhero.global.format
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase


@SuppressLint("ViewConstructor")
class AlexWidget(
    name:String,
    restaurantName:String,
    val id:String,
    val callbackDelete:(args:Any?)->Unit,
    context: Context?,
    attrs: AttributeSet?): LinearLayout(context,attrs) {

    init{
        LinearLayout.inflate(context, R.layout.alex_layout,this)
        val nameTextView: TextView = this.findViewById(R.id.nameTextView)
        val restaurantTextView: TextView = this.findViewById(R.id.restaurantTextView)
        val deleteButton: ImageButton = this.findViewById(R.id.deleteItemButton)

        nameTextView.text = name
        restaurantTextView.text = restaurantName

       auth = Firebase.auth

        deleteButton.setOnClickListener {
            callbackDelete(this)

        }
    }
}