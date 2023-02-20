package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityOrderBinding
import com.example.foodhero.global.moveToActivityAndPutOnTop
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderActivity : AppCompatActivity() {

    lateinit var imageBackOrderButton: ImageButton
    lateinit var bottomOrderNavMenu : BottomNavigationView
    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding!!
        
    

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomOrderNavigationMenu()
        
        imageBackOrderButton = findViewById<ImageButton>(R.id.imageBackOrderButton)
        imageBackOrderButton.setOnClickListener {

            goBackTwo()

        }
    }

    fun goBackTwo(){
        //super.onBackPressed() is deprecated but works
        finish()
        //val intent = Intent(this, MainActivity::class.java)
        //moveToActivity(intent)

    }
    private fun setBottomOrderNavigationMenu(){
        bottomOrderNavMenu = binding.bottomOrderNavMenu
       bottomOrderNavMenu.setOnItemSelectedListener {it: MenuItem ->
           when(it.itemId){
               // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
              R.id.navigateProfile->moveToActivityAndPutOnTop(Intent(this,BuyActivity::class.java))
              R.id.navigateSearch->moveToActivityAndPutOnTop(Intent(this, FavoriteActivity::class.java))

           }
           true
        }


        
       }

}