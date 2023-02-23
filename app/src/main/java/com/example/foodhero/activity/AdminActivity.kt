package com.example.foodhero.activity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton = findViewById<ImageButton>(R.id.BackButtonImage)
        val logoutButton = findViewById<ImageButton>(R.id.ImageProfilButton)
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
            R.id.ImageProfilButton -> {
                auth.signOut()
            }
            R.id.SubmitButton -> {
                val nameField = findViewById<EditText>(R.id.nameEditText)
                val countryField = findViewById<EditText>(R.id.countryEditText)
                val cityField = findViewById<EditText>(R.id.cityEditText)
                val orgNumberField = findViewById<EditText>(R.id.orgNumberEditText)
                val adressField = findViewById<EditText>(R.id.adressEditText)
                val phonenumberField = findViewById<EditText>(R.id.phonenumberEditText)
                val emailField = findViewById<EditText>(R.id.emailEditText)
                val descriptionEditTextField = findViewById<EditText>(R.id.descriptionEditText)
                val geohashField = findViewById<EditText>(R.id.geohashEditText)
                val restaurantIdField = findViewById<EditText>(R.id.restaurantIdEditText)
                val lonField = findViewById<EditText>(R.id.lonEditText)
                val latField = findViewById<EditText>(R.id.latEditText)
                //val cathegoriesDishesField = findViewById<EditText>(R.id.cathegoriesDishesEditText)
                //val keyWordsField = findViewById<EditText>(R.id.keyWordsEditText)



                val restaurant = Restaurant(name = nameField.text.toString(), country = countryField.text.toString(),
                    city = cityField.text.toString(), orgNumber= orgNumberField.text.toString(),
                    adress = adressField.text.toString(), phonenumber= phonenumberField.text.toString(),
                    email = emailField.text.toString(), description= descriptionEditTextField.text.toString(),
                    geohash = geohashField.text.toString(), restaurantId = restaurantIdField.text.toString(),
                    lon = lonField.text.toString().toDouble(),lat = latField.text.toString().toDouble())


                firestore.saveRestaurant(restaurant)
            }
        }
    }
}