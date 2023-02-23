package com.example.foodhero.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreRepository
import com.example.foodhero.struct.Restaurant

class AdminActivity : AppCompatActivity() {
    private val auth = AuthRepo()
    private val firestore = FirestoreRepository();

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton = findViewById<ImageButton>(R.id.BackButtonImage)
        val logoutButton = findViewById<ImageButton>(R.id.ImageRestLogButton)
        val submitButton = findViewById<Button>(R.id.SubmitButton)
        backButton.setOnClickListener(clickListener)
        logoutButton.setOnClickListener(clickListener)
        submitButton.setOnClickListener(clickListener)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.BackButtonImage -> {
                finish()
            }
            R.id.ImageRestLogButton -> {
                auth.signOut()
            }
            R.id.SubmitButton -> {
                val nameField = findViewById<EditText>(R.id.EditRestNameText)
                val locationField = findViewById<EditText>(R.id.EditRestAdressText)
                val descriptionField = findViewById<EditText>(R.id.EditRestCityText)
                val restaurant = Restaurant(name = nameField.text.toString(), adress = locationField.text.toString(), description = descriptionField.text.toString())
                firestore.saveRestaurant(restaurant)
            }
        }
    }
}