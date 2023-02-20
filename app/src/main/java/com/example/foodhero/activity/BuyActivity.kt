package com.example.foodhero.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityBuyBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyActivity : AppCompatActivity() {

    lateinit var imageBackOrderButton: ImageButton
    lateinit var bottomNavBuyMenu : BottomNavigationView
    private var _binding: ActivityBuyBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        _binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
     setBottomBuyNavigationMenu()

        imageBackOrderButton = findViewById<ImageButton>(R.id.imageBackOrderButton)
        imageBackOrderButton.setOnClickListener {

            goBack()

        }



        }
    fun goBack(){
        //super.onBackPressed() is deprecated but works
        finish()
        //val intent = Intent(this, MainActivity::class.java)
        //moveToActivity(intent)

    }
    private fun setBottomBuyNavigationMenu(){
        bottomNavBuyMenu = binding.bottomNavBuyMenu
        bottomNavBuyMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                // R.id.navigateCart->moveToActivity(Intent(this,OrderActivity::class.java))
                // R.id.navigateProfile->moveToActivity(Intent(this, ProfilActivity::class.java))

            }
            true
        }



    }





}