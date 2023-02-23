package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityOrderBinding
import com.example.foodhero.global.*
import com.example.foodhero.struct.PurchasedItem
import com.example.foodhero.struct.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import java.util.UUID

class OrderActivity : AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var imageBackOrderButton: ImageButton
    private var firestoreListener: ListenerRegistration?=null
    lateinit var bottomOrderNavMenu : BottomNavigationView
    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding!!
    private val intentFilter = IntentFilter()
    lateinit var shoppingCartTextView: TextView
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    var currentText = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        setOnBackNavigation()
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setBottomOrderNavigationMenu()
        setCloseAppCallback()

        shoppingCartTextView = findViewById(R.id.shoppingCartTextView)
        shoppingCartTextView.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()
        }

        imageBackOrderButton = findViewById<ImageButton>(R.id.imageBackOrderButton)
        imageBackOrderButton.setOnClickListener {
            goBackTwo()
        }

        getUserShoppingCart()

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


    fun getUserShoppingCart() {
        db = FirebaseFirestore.getInstance()
        val mail = auth.currentUser?.email
        db.collection("Users").document(mail!!).collection("ShoppingCart")
            .get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val docRef = db.collection(USER_COLLECTION).document(mail).collection("ShoppingCart")
                    firestoreListener = docRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(ContentValues.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            for (doc in snapshot!!.documents){
                                val purchasedItem = doc.toObject(PurchasedItem::class.java)
                                shoppingCartTextView.text = purchasedItem!!.foodName.toString()
                                logMessage(doc.toString())

                            }

                        } else {
                            Log.d(ContentValues.TAG, "Current data: null")
                        }
                    }
                }
            }
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

    fun closeListener(){
        if(firestoreListener!=null)firestoreListener!!.remove()
    }

    override fun onDestroy(){
        closeListener()
        super.onDestroy()
    }

}