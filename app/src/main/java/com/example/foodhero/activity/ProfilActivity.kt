package com.example.foodhero.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.example.foodhero.R
import com.example.foodhero.global.moveToActivityAndFinish
import com.google.firebase.auth.FirebaseAuth

class ProfilActivity : AppCompatActivity() {


    lateinit var imageBackButton : ImageButton
    lateinit var imageLogOutButton: ImageButton
    lateinit var EditNameText : EditText
    lateinit var EditMailText : EditText
    lateinit var EditPhoneText : EditText
    lateinit var EditPasswordText : EditText


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        imageBackButton = findViewById<ImageButton>(R.id.imageBackButton)
        imageBackButton.setOnClickListener {

           goBack()

        }

        EditNameText = findViewById(R.id.EditNameText)
        EditMailText = findViewById(R.id.EditMailText)
        EditPhoneText = findViewById(R.id.EditPhoneText)
        EditPasswordText = findViewById(R.id.EditPasswordText)


        imageLogOutButton = findViewById<ImageButton>(R.id.imageLogOutButton)

        val imageLogOutButton = findViewById<ImageButton>(R.id.imageLogOutButton)

        imageLogOutButton.setOnClickListener {
            signOut()
        }

        val imageAccountView = findViewById<ImageView>(R.id.imageAccountView)
            imageAccountView.animate().apply {

                duration = 1000
                rotationXBy(360f)

            } .start()


    }


    fun goBack(){
        //super.onBackPressed() is deprecated but works
        finish()
        //val intent = Intent(this,MainActivity::class.java)
        //moveToActivity(intent)

    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
         moveToActivityAndFinish(intent)

    }
}