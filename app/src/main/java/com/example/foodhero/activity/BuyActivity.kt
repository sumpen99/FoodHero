package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityBuyBinding
import com.example.foodhero.global.APP_ACTION_LOG_OUT
import com.example.foodhero.global.moveToActivityAndFinish
import com.example.foodhero.global.moveToActivityAndPutOnTop
import com.example.foodhero.global.moveToActivityAndReOrder
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyActivity : AppCompatActivity() {

    lateinit var imageBackOrderButton: ImageButton
    lateinit var bottomNavBuyMenu : BottomNavigationView
    private var _binding: ActivityBuyBinding? = null
    private val binding get() = _binding!!
    lateinit var imageBuyButton: ImageButton
    lateinit var imageProfileButton : ImageButton
    lateinit var textPriceView : TextView
    private val intentFilter = IntentFilter()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        _binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCloseAppCallback()
        // setBottomBuyNavigationMenu()

        textPriceView = findViewById(R.id.textPriceView)
        textPriceView.text = intent.getDoubleExtra("Summan",0.0).toString()+"kr"

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
            moveToActivityAndReOrder(Intent(this, ProfilActivity::class.java))
            //val intent = Intent(this,ProfilActivity::class.java)
            //moveToActivityAndFinish(intent)
        }
    }

    private fun setCloseAppCallback(){
        intentFilter.addAction(APP_ACTION_LOG_OUT)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }, intentFilter)
    }

    fun goBack(){
        //super.onBackPressed() is deprecated but works
        //finish()
        moveToActivityAndReOrder(Intent(this, OrderActivity::class.java))
        //val intent = Intent(this, MainActivity::class.java)
        //moveToActivity(intent)
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