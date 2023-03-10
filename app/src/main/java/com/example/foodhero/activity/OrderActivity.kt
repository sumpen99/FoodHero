package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityOrderBinding
import com.example.foodhero.global.*
import com.example.foodhero.struct.PurchasedItem
import com.example.foodhero.widgets.SalmbergsWidget
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class OrderActivity : AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var imageBackOrderButton: ImageButton
    private var firestoreListener: ListenerRegistration?=null
    lateinit var bottomOrderNavMenu : BottomNavigationView
    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding!!
    private val intentFilter = IntentFilter()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var shoppingCartLayout: LinearLayout
    var sum = 0

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

        shoppingCartLayout = binding.shoppingCartLayout





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
    fun deleteItemFromShoppingCart(widget: Any?) {
        if (widget is SalmbergsWidget){
            val mail = auth.currentUser?.email
            val docRef = db.collection(USER_COLLECTION)
                .document(mail!!)
                .collection("ShoppingCart")
                .document(widget.id)

            docRef.delete()
                .addOnSuccessListener {
                    shoppingCartLayout.removeView(widget)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
        }

    }





    fun getUserShoppingCart() {
        db = FirebaseFirestore.getInstance()
        val mail = auth.currentUser?.email
        val docRef = db.collection(USER_COLLECTION)
            .document(mail!!)
            .collection("ShoppingCart")
        firestoreListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            snapshot?.documentChanges?.forEach { change ->
                when (change.type) {
                    DocumentChange.Type.ADDED -> {
                        val purchasedItem = change.document.toObject(PurchasedItem::class.java)
                        val salmbergsItem = SalmbergsWidget(
                            purchasedItem.foodName!!,
                            purchasedItem.price!!,
                            purchasedItem.itemId!!,
                            ::deleteItemFromShoppingCart,
                            this,
                            null
                        )
                        shoppingCartLayout.addView(salmbergsItem, shoppingCartLayout.childCount)
                        logMessage(change.document.toString())
                    }

                    DocumentChange.Type.MODIFIED -> {
                        // Handle modified document
                    }

                    DocumentChange.Type.REMOVED -> {
                        // Handle removed document

                    }
                }
            }
        }
    }


    fun totalSum():Double{
        var sum = 0.0
        for(Child in shoppingCartLayout.children){
            if (Child is SalmbergsWidget){
                sum += Child.price
            }
       }
        return sum
    }


    private fun setBottomOrderNavigationMenu(){
        bottomOrderNavMenu = binding.bottomOrderNavMenu
        bottomOrderNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                R.id.navigateProfile->{
                    val intent = Intent(this,BuyActivity::class.java)
                    intent.putExtra("Summan",totalSum())
                    moveToActivityAndReOrder(intent)
                }

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