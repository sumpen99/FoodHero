package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityOrderBinding
import com.example.foodhero.global.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderActivity : AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var imageBackOrderButton: ImageButton
    lateinit var bottomOrderNavMenu : BottomNavigationView
    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding!!
    private val intentFilter = IntentFilter()
    

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        setOnBackNavigation()
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomOrderNavigationMenu()
        setCloseAppCallback()
        
        imageBackOrderButton = findViewById<ImageButton>(R.id.imageBackOrderButton)
        imageBackOrderButton.setOnClickListener {
            goBackTwo()
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

    /*
    *   ##########################################################################
    *               SET NAVIGATION IF USER DRAGS LEFT OR RIGHT SIDE OF SCREEN
    *   ##########################################################################
    */

    private fun setOnBackNavigation(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){navigateOnBackPressed()}
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun navigateOnBackPressed(){
        moveToActivityAndReOrder(Intent(this, MainActivity::class.java))
    }

    fun goBackTwo(){
        //super.onBackPressed() is deprecated but works
        //finish()
        moveToActivityAndReOrder(Intent(this, MainActivity::class.java))
        //val intent = Intent(this, MainActivity::class.java)
        //moveToActivity(intent)

    }
    private fun setBottomOrderNavigationMenu(){
        bottomOrderNavMenu = binding.bottomOrderNavMenu
        bottomOrderNavMenu.setOnItemSelectedListener {it: MenuItem ->
           when(it.itemId){
               // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
              R.id.navigateProfile->moveToActivityAndReOrder(Intent(this,BuyActivity::class.java))
              R.id.navigateSearch->moveToActivityAndReOrder(Intent(this, FavoriteActivity::class.java))

           }
            true
        }
    }

}