package com.example.foodhero.activity
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import com.example.foodhero.R
import com.example.foodhero.global.moveToActivityAndClearTop
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

                    val user = it.result.toObject(User::class.java)
                    EditMailText.hint = user!!.email
                    EditNameText.hint = user!!.name
                    EditPhoneText.hint = user!!.phoneNumber
                    EditPostalCodeText.hint = user!!.postalCode
                    EditCityText.hint = user!!.city
                }else{

                }
            }








        }

    fun goBack(){
        finish()
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