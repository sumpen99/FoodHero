package com.example.foodhero.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityBuyBinding
import com.example.foodhero.global.moveToActivityAndFinish
import com.example.foodhero.global.moveToActivityAndPutOnTop
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyActivity : AppCompatActivity() {

    lateinit var imageBackOrderButton: ImageButton
    lateinit var bottomNavBuyMenu : BottomNavigationView
    private var _binding: ActivityBuyBinding? = null
    private val binding get() = _binding!!
   lateinit var imageBuyButton: ImageButton
   lateinit var imageProfileButton : ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        _binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    // setBottomBuyNavigationMenu()

        imageBackOrderButton = findViewById<ImageButton>(R.id.imageBackOrderButton)
        imageBackOrderButton.setOnClickListener {

            goBack()

        }

        imageBuyButton = findViewById<ImageButton>(R.id.imageBuyButton)
        imageBuyButton.setOnClickListener {

            Toast.makeText(applicationContext, "Nu är din mat beställd! Tack för ditt köp önskar FoodHero!", Toast.LENGTH_LONG).show()
            val intent = Intent(this,FavoriteActivity::class.java)
            moveToActivityAndPutOnTop(intent)
        }

        imageProfileButton = findViewById<ImageButton>(R.id.imageProfileButton)
        imageProfileButton.setOnClickListener {
           val intent = Intent(this,ProfilActivity::class.java)
            moveToActivityAndFinish(intent)

        }



        }
    fun goBack(){
        //super.onBackPressed() is deprecated but works
        finish()
        //val intent = Intent(this, MainActivity::class.java)
        //moveToActivity(intent)
     // hem
    }
    //private fun setBottomBuyNavigationMenu(){
    //    bottomNavBuyMenu = binding.bottomNavBuyMenu
     //   bottomNavBuyMenu.setOnItemSelectedListener {it: MenuItem ->
       //     when(it.itemId){
                // R.id.navigateCart->moveToActivity(Intent(this,OrderActivity::class.java))
                // R.id.navigateProfile->moveToActivity(Intent(this, ProfilActivity::class.java))

        //    }
        //    true
       //}



   // }





}