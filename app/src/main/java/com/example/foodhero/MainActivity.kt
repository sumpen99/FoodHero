package com.example.foodhero
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.ActivityMainBinding
import com.example.foodhero.global.logMessage
import com.example.foodhero.global.moveToActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(userIsLoggedIn()){
            setContentView(R.layout.activity_main)
            setDataBinding()
            setOnBackNavigation()

        }
    }


    /*
    *   ##########################################################################
    *               SET BINDING
    *   ##########################################################################
    */

    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        logMessage("navigate me")
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BASED ON CURRENT USER STATUS
    *   ##########################################################################
    */

    private fun navigateOnResume(){
        if(!userIsLoggedIn()){
            moveToActivity(Intent(this,LoginActivity::class.java))
        }


    }

    /*
    *   ##########################################################################
    *               USER SIGNED IN USER SIGN OUT
    *   ##########################################################################
    */

    private fun userIsLoggedIn():Boolean{
        return Firebase.auth.currentUser!=null

    }

    fun signUserOut(){
        Firebase.auth.signOut()
    }

    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */
    override fun onResume(){
        super.onResume()
        navigateOnResume()
        logMessage("on resume main")

    }

    override fun onPause(){
        super.onPause()
        logMessage("on pause main")

    }

    override fun onStop(){
        super.onStop()
        logMessage("on stop main")

    }
}