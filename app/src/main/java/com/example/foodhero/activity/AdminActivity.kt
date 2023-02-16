package com.example.foodhero.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.database.AuthRepo

class AdminActivity : AppCompatActivity() {
    private val auth = AuthRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton = findViewById<ImageButton>(R.id.BackButtonImage)
        val logoutButton = findViewById<ImageButton>(R.id.LogoutImageButton)
        backButton.setOnClickListener(clickListener)
        logoutButton.setOnClickListener(clickListener)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.BackButtonImage -> {
                finish()
            }
            R.id.LogoutImageButton -> {
                auth.signOut()
            }
        }
    }
}