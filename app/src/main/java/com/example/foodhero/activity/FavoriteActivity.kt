package com.example.foodhero.activity
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityFavoriteBinding
import com.example.foodhero.global.*
import com.example.foodhero.struct.FavoriteItem
import com.example.foodhero.widgets.AlexWidget
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase


class FavoriteActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var imageBackTwoButton: ImageButton
    private val intentFilter = IntentFilter()
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val binding get() = _binding!!
    private var _binding: ActivityFavoriteBinding? = null
    private var firestoreListener: ListenerRegistration? = null
    lateinit var favoritLayout: LinearLayout
    lateinit var db: FirebaseFirestore
    lateinit var bottomFavoritNavMenu: BottomNavigationView
    var currentText = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setCloseAppCallback()
        setOnBackNavigation()
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        // find the TextViews in the layout
        //  favoriteDishesTextView = findViewById(R.id.favorite_dishes_textview)
        // lastOrdersTextView = findViewById(R.id.last_orders_textview)

        // retrieve the user's favorite dishes and last orders from the database
        //retrieveFavoriteDishes()
        //retrieveLastOrders()
        favoritLayout = binding.favoritLayout




        setBottomOrderNavigationMenu()
        getUserFavorit()



        imageBackTwoButton = findViewById<ImageButton>(R.id.imageBackTwoButton)
        imageBackTwoButton.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //moveToActivityAndPutOnTop(intent)
            moveToActivityAndReOrder(Intent(this, MainActivity::class.java))

        }

    }

    private fun setOnBackNavigation() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateOnBackPressed()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun navigateOnBackPressed() {
        moveToActivityAndReOrder(Intent(this, MainActivity::class.java))
    }

    private fun setCloseAppCallback() {
        intentFilter.addAction(APP_ACTION_LOG_OUT)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }, intentFilter)
    }

    fun favoritPressed() {
        // När nav knappen "navigateSearch" är tryckt ska den gå til getUserFavorit som hämtar de favoriter
        //man har.
        //setBottomOrderNavigationMenu().navigateSearch.isPressed
        //  ->
        //   getUserFavorit()
    }

    fun deleteItemFromFavorites(widget: Any?) {
        if (widget is AlexWidget) {
            val mail = auth.currentUser?.email
            val docRef = db.collection(USER_COLLECTION)
                .document(mail!!)
                .collection("Favorites")
                .document(widget.id)

            docRef.delete()
                .addOnSuccessListener {
                    favoritLayout.removeView(widget)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error deleting document", e)
                }
        }

    }

    fun getUserFavorit() {
        db = FirebaseFirestore.getInstance()
        val mail = auth.currentUser?.email
        //Behövs en ny specifik collection för "Favorties" istället för "shoppingCart" .

        val docRef = db.collection(USER_COLLECTION).document(mail!!).collection("Favorites")
        firestoreListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            snapshot?.documentChanges?.forEach { change ->
                when (change.type) {
                    DocumentChange.Type.ADDED -> {
                        val favoriteItem = change.document.toObject(FavoriteItem::class.java)
                        val alexItem = AlexWidget(
                            favoriteItem.foodName!!,
                            favoriteItem.restaurantName!!,
                            favoriteItem.itemId!!,
                            ::deleteItemFromFavorites,
                            this,
                            null
                        )
                        favoritLayout.addView(alexItem, favoritLayout.childCount)
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

    private fun setBottomOrderNavigationMenu() {
        bottomFavoritNavMenu = binding.bottomFavoritNavMenu
        bottomFavoritNavMenu.setOnItemSelectedListener { it: MenuItem ->
            when (it.itemId) {
                // R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                R.id.navigateSearch -> {
                    moveToActivityAndPutOnTop(Intent(this, MainActivity::class.java))
                }
                //R.id.navigateSearch->(Intent(this, FavoriteActivity::class.java))
            }
            true
        }
    }
}


/* private fun retrieveFavoriteDishes() {
     // make a database query to retrieve the user's favorite dishes
     val mail = auth.currentUser?.email

     db.collection(USER_COLLECTION)
         .document(mail!!)
         .collection("ShoppingCart")
         .get()
         .addOnSuccessListener { documents ->

             val favoriteDishes = documents.map { it.getString("foodName") ?: "" }
             //favoriteDishesTextView.text = favoriteDishes.joinToString("\n")
         }
         .addOnFailureListener { e ->
             Log.w("FavoriteActivity", "Error retrieving favorite dishes", e)
         }
 }

 private fun retrieveLastOrders() {
     // make a database query to retrieve the user's last orders
     val mail = auth.currentUser?.email

     db.collection(USER_COLLECTION)
         .document(mail!!)
         .collection("ShoppingCart")
         .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
         .limit(5)
         .get()
         .addOnSuccessListener { documents ->
             val lastOrders = documents.map { it.getString("description") ?: "" }
            // lastOrdersTextView.text = lastOrders.joinToString("\n")
         }
         .addOnFailureListener { e ->
             Log.w("FavoriteActivity", "Error retrieving last orders", e)
         }
 }
 }*/
