package com.example.foodhero.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.databinding.FragmentLoginmainBinding
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.fragment.LoginMainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ProfilActivity : AppCompatActivity() {


    lateinit var imageBackButton : ImageButton
    lateinit var imageLogOutButton: ImageButton
    lateinit var EditNameText : EditText
    lateinit var EditMailText : EditText
    lateinit var EditPhoneText : EditText
    lateinit var EditPasswordText : EditText



    @SuppressLint("MissingInflatedId")
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



    }


    fun goBack(){
        val intent = Intent(this,HomeFragment()::class.java)
        HomeFragment(intent)
        // Samma här kan ej skicka vidare

    }

    fun signOut(){

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginMainFragment()::class.java)
         LoginMainFragment(intent)
       // Den ska dock skickas till LoginMainFragment , kan ej få det att funka.

    }
}