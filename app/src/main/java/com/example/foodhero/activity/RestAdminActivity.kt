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
import com.example.foodhero.databinding.ActivityRestAdminBinding
import com.example.foodhero.global.moveToActivityAndReOrder
import com.google.android.material.bottomnavigation.BottomNavigationView

class RestAdminActivity : AppCompatActivity() {
    private var _binding: ActivityRestAdminBinding? = null
    private val binding get() = _binding!!
    lateinit var restButton: Button
    lateinit var EditRestNameText : EditText
    lateinit var EditRestAdressText : EditText
    lateinit var EditRestCityText : EditText
    lateinit var imageRestLogButton : ImageButton
    lateinit var bottomRestNavMenu : BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_admin)
        _binding = ActivityRestAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageRestLogButton = binding.ImageRestLogButton
        imageRestLogButton.setOnClickListener {
        }


    }

    private fun setBottomOrderNavigationMenu(){
        //bottomRestNavMenu = binding.bottomOrderNavMenu
        bottomRestNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                R.id.navigateSearch->moveToActivityAndReOrder(Intent(this, FavoriteActivity::class.java))

            }
            true
        }
    }
}

