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



lateinit var auth: FirebaseAuth
lateinit var db: FirebaseFirestore
private var firestoreListener: ListenerRegistration?=null

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

        auth = Firebase.auth

        deleteButton.setOnClickListener {
            callbackDelete(this)

        }
    }
}