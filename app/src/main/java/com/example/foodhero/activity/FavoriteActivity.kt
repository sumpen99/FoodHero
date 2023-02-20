package com.example.foodhero.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.foodhero.R
import com.google.firebase.firestore.FirebaseFirestore


private lateinit var favoriteDishesTextView: TextView
private lateinit var lastOrdersTextView: TextView
private val db = FirebaseFirestore.getInstance()

class FavoriteActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        // find the TextViews in the layout
        favoriteDishesTextView = findViewById(R.id.favorite_dishes_textview)
        lastOrdersTextView = findViewById(R.id.last_orders_textview)

        // retrieve the user's favorite dishes and last orders from the database
        retrieveFavoriteDishes()
        retrieveLastOrders()
    }

    private fun retrieveFavoriteDishes() {
        // make a database query to retrieve the user's favorite dishes
        db.collection("users")
            .document("user1")
            .collection("favorite_dishes")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteDishes = documents.map { it.getString("name") ?: "" }
                favoriteDishesTextView.text = favoriteDishes.joinToString("\n")
            }
            .addOnFailureListener { e ->
                Log.w("FavoriteActivity", "Error retrieving favorite dishes", e)
            }
    }

    private fun retrieveLastOrders() {
        // make a database query to retrieve the user's last orders
        db.collection("users")
            .document("user1")
            .collection("orders")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val lastOrders = documents.map { it.getString("description") ?: "" }
                lastOrdersTextView.text = lastOrders.joinToString("\n")
            }
            .addOnFailureListener { e ->
                Log.w("FavoriteActivity", "Error retrieving last orders", e)
            }
    }
    }
