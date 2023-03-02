package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.databinding.ActivityRestAdminBinding
import com.example.foodhero.global.APP_ACTION_LOG_OUT
import com.example.foodhero.global.moveToActivityAndClearTop
import com.example.foodhero.global.moveToActivityAndFinish
import com.example.foodhero.global.moveToActivityAndReOrder
import com.google.android.material.bottomnavigation.BottomNavigationView

class RestAdminActivity : AppCompatActivity() {
    private val intentFilter = IntentFilter()
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var _binding: ActivityRestAdminBinding? = null
    lateinit var bottomRestNavMenu : BottomNavigationView
    private val binding get() = _binding!!
    private val auth = AuthRepo()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setBottomNavigationMenu()
        setLogOutButton()
        setOnBackNavigation()
        setNameField()
        setCloseAppCallback()
    }

    private fun setCloseAppCallback(){
        intentFilter.addAction(APP_ACTION_LOG_OUT)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                moveToActivityAndFinish(Intent(context,LoginActivity::class.java))
            }
        }, intentFilter)
    }

    private fun setLogOutButton(){
        val logOut = binding.ImageRestLogButton
        logOut.setOnClickListener{
            auth.signOut()
            moveToActivityAndClearTop()
        }
    }

    private fun setNameField(){
        val nameEdittext = binding.EditRestNameText
        nameEdittext.hint = auth.getEmail()
    }

    private fun setBinding(){
        _binding = ActivityRestAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setOnBackNavigation(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){}
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBottomNavigationMenu(){
        bottomRestNavMenu = binding.bottomRestNavMenu
        bottomRestNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                R.id.navigateRestRestaurant->{moveToActivityAndReOrder(Intent(this,AdminActivity::class.java))}
                R.id.navigateRestMenu->{}
            }
            true
        }
    }

}

