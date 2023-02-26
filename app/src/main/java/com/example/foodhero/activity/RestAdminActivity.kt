package com.example.foodhero.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityRestAdminBinding
import com.example.foodhero.global.*
import com.example.foodhero.messaging.FirestoreMessaging
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.GetTokenResult

class RestAdminActivity : AppCompatActivity() {
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var firestoreMessaging: FirestoreMessaging
    lateinit var restButton: Button
    lateinit var EditRestNameText : EditText
    lateinit var EditRestAdressText : EditText
    lateinit var EditRestCityText : EditText
    lateinit var imageRestLogButton : ImageButton
    lateinit var bottomRestNavMenu : BottomNavigationView
    private var _binding: ActivityRestAdminBinding? = null
    private val binding get() = _binding!!
    private val auth = AuthRepo()
    private var myRestaurants: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_admin)
        _binding = ActivityRestAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageRestLogButton = binding.ImageRestLogButton
        imageRestLogButton.setOnClickListener {
        }
        loadMyRestaurants()
    }

    private fun loadMyRestaurants(){
        auth.userCustomClaims().addOnSuccessListener(
            OnSuccessListener<GetTokenResult> { result ->
                val value = result.claims[CLAIMS_RESTAURANTIDS]
                if(value != null && value is String){
                    myRestaurants = value
                    initMessageService()
                }
            })
    }

    private fun initMessageService(){
        firestoreMessaging = FirestoreMessaging.getInstance()
        firestoreViewModel = FirestoreViewModel()
        FirestoreMessaging.setTokenRefreshCallback{token->String
            firestoreViewModel.
            updateRestaurantNotificationReciever(token,myRestaurants)
        }
        FirestoreMessaging.initFirebaseMessaging()
        FirestoreMessaging.refreshToken()
    }

    private fun setBottomOrderNavigationMenu(){
        //bottomRestNavMenu = binding.bottomOrderNavMenu
        bottomRestNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)

            }
            true
        }
    }
}

