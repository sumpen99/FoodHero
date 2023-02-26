package com.example.foodhero.activity
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.example.foodhero.R
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityRestAdminBinding
import com.example.foodhero.global.*
import com.example.foodhero.messaging.FirestoreMessaging
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.messaging.RemoteMessage
import java.util.*

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
            updateRestaurantNotificationReciever(token,myRestaurants).addOnCompleteListener {
                if(it.isSuccessful){
                    val jsonMessage = firestoreMessaging.buildJsonOrderBody(
                        token,
                        "")
                    jsonMessage?: return@addOnCompleteListener
                    firestoreMessaging.sendJsonNotification(applicationContext,jsonMessage)
                }
            }
        }

        FirestoreMessaging.setMessagedRecievedCallback {
            handleMessageReceived(it)
        }

        FirestoreMessaging.initFirebaseMessaging()
        FirestoreMessaging.refreshToken()
  }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun handleMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent(this, OrderActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FirestoreMessaging.setupChannels(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.applogowhite)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.applogoblack)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data.get("title"))
            .setContentText(remoteMessage.data.get("message"))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)
        notificationBuilder.color = resources.getColor(R.color.background_medium_dark)
        notificationManager.notify(notificationID, notificationBuilder.build())
        respondToOrder(remoteMessage.data.get("token"))
    }

    private fun respondToOrder(respondToToken:String?){
        respondToToken?:return
        val jsonMessage = firestoreMessaging.buildJsonRespondBody(
            respondToToken)
        jsonMessage?:return
        firestoreMessaging.sendJsonNotification(applicationContext,jsonMessage)
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

