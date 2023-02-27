package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.global.*
import com.example.foodhero.struct.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class ProfilActivity : AppCompatActivity() {
    private val intentFilter = IntentFilter()
    private var firestoreListener: ListenerRegistration?=null
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var imageBackButton : ImageButton
    lateinit var imageLogOutButton: ImageButton
    lateinit var EditNameText : EditText
    lateinit var EditMailText : EditText
    lateinit var EditPhoneText : EditText
    lateinit var EditPostalCodeText : EditText
    lateinit var EditCityText : EditText
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    var currentText = ""


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCloseAppCallback()
        setOnBackNavigation()
        setContentView(R.layout.activity_profil)

        auth = Firebase.auth
       getUserData()



        imageBackButton = findViewById<ImageButton>(R.id.imageBackTwoButton)
        imageBackButton.setOnClickListener {
           goBack()
        }

        EditMailText = findViewById(R.id.EditMailText)
        EditMailText.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()


        }
        EditNameText = findViewById(R.id.EditNameText)
        EditNameText.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()

        }
        EditPhoneText = findViewById(R.id.EditPhoneText)
        EditPhoneText.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()

        }
        EditPostalCodeText = findViewById(R.id.EditPostalCodeText)
        EditPostalCodeText.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()


        }
        EditCityText = findViewById(R.id.EditCityText)
        EditCityText.doOnTextChanged { text, start, before, count ->
            currentText = text.toString()


        }


        imageLogOutButton = findViewById<ImageButton>(R.id.ImageProfilButton)

        val imageLogOutButton = findViewById<ImageButton>(R.id.ImageProfilButton)

        imageLogOutButton.setOnClickListener {
            signOut()
        }

        val imageAccountView = findViewById<ImageView>(R.id.imageAccountView)
            imageAccountView.animate().apply {

                duration = 1000
                rotationXBy(360f)

            } .start()


    }


    fun getUserData() {
        db = FirebaseFirestore.getInstance()
        val mail = auth.currentUser?.email
        logMessage("1")

        db.collection("Users").document(mail!!)
            .get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val docRef = db.collection(USER_COLLECTION).document(mail)

                    firestoreListener = docRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        logMessage("2")
                        if (snapshot != null && snapshot.exists()) {
                            val user = snapshot.toObject(User::class.java)
                            EditMailText.hint = user!!.email
                            EditNameText.hint = user!!.name
                            EditPhoneText.hint = user!!.phoneNumber
                            EditPostalCodeText.hint = user!!.postalCode
                            EditCityText.hint = user!!.city
                        } else {
                            Log.d(TAG, "Current data: null")
                        }
                    }
                }
                logMessage("3")
            }
        logMessage("4")
        }

    /*
    *   ##########################################################################
    *               SET NAVIGATION IF USER DRAGS LEFT OR RIGHT SIDE OF SCREEN
    *   ##########################################################################
    */

    private fun setCloseAppCallback(){
        intentFilter.addAction(APP_ACTION_LOG_OUT)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }, intentFilter)
    }

    private fun setOnBackNavigation(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){navigateOnBackPressed()}
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun navigateOnBackPressed(){
        moveToActivityAndReOrder(Intent(this, MainActivity::class.java))
    }

    fun goBack(){
        moveToActivityAndReOrder(Intent(this, MainActivity::class.java))
    }

    fun signOut(){
        closeListener()
        FirebaseAuth.getInstance().signOut()
        moveToActivityAndClearTop()
    }

    fun closeListener(){
        if(firestoreListener!=null)firestoreListener!!.remove()
    }

    override fun onDestroy(){
        closeListener()
        super.onDestroy()
    }

}