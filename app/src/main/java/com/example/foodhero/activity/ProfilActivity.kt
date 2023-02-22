package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.global.USER_COLLECTION
import com.example.foodhero.global.moveToActivityAndClearTop
import com.example.foodhero.global.moveToActivityAndFinish
import com.example.foodhero.global.moveToActivityAndPutOnTop
import com.example.foodhero.struct.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class ProfilActivity : AppCompatActivity() {
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
        /*val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.foodhero.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }, intentFilter)*/
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



        db.collection("Users").document(mail!!)
            .get().addOnCompleteListener {
                if(it.isSuccessful)
                {


                    val docRef = db.collection(USER_COLLECTION).document(mail)
                    docRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }

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
            }








        }

    fun goBack(){
        val intent = Intent(this, MainActivity::class.java)
        moveToActivityAndPutOnTop(intent)

    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        moveToActivityAndClearTop()
    }

    /*override fun onDestroy(){
        logMessage("on destroy profile")
        super.onDestroy()
    }*/

}