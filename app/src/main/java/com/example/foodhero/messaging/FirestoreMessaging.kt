package com.example.foodhero.messaging
import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.AuthFailureError
import com.android.volley.Request.Method.POST
import com.example.foodhero.global.SENDER_ID
import com.google.firebase.messaging.FirebaseMessaging
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.global.*
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*

class FirestoreMessaging: FirebaseMessagingService() {
     //When app is in foreground the onMessageReceived always calls.
     //When app is in background the onMessageReceived will be called
     // when the payload only has the data property (it doesn't exist the notification property).
    private var firestoreMessage:FirebaseMessaging? = null
    var NOTIFICATION_TITLE: String? = null
    var NOTIFICATION_MESSAGE: String? = null
    var TOPIC: String? = null

    companion object {
        private var messageId = 0
        private lateinit var callbackOnTokenRefresh:((args:String)->Unit)
        private lateinit var messageResponder:MessageResponder
        private var instance: FirestoreMessaging? = null
        private fun isCallbackOnTokenRefreshInitialized() = ::callbackOnTokenRefresh.isInitialized

        @Synchronized
        fun getInstance(): FirestoreMessaging {
            if(instance == null) {
                instance = FirestoreMessaging()
            }
            return instance!!
        }

        fun initFirebaseMessaging(){
            instance?.firestoreMessage = FirebaseMessaging.getInstance()
        }

        fun setTokenRefreshCallback(callback:(args:String)->Unit){
            callbackOnTokenRefresh = callback
        }

        fun refreshToken(){
            instance?.loadToken()?.addOnCompleteListener {
                if(it.isSuccessful){
                    val token = it.result
                    onTokenRefresh(token)
                }
            }
        }

        fun onTokenRefresh(token:String){
            if(isCallbackOnTokenRefreshInitialized()){
                callbackOnTokenRefresh(token)
            }
        }
    }

    override fun onNewToken(token: String) {
        onTokenRefresh(token)
    }

    private fun loadToken():Task<String>{
        return firestoreMessage!!.token
    }

     override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logMessage("From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            logMessage("Message data payload: ${remoteMessage.data}")
            handleNow(remoteMessage)

            //if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob()
            //} else {
                // Handle message within 10 seconds
            //}
        }
        remoteMessage.notification?.let {
            logMessage("Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun handleNow(remoteMessage:RemoteMessage) {
        val intent = Intent(this,MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or FLAG_IMMUTABLE)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.applogowhite);
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.applogoblack)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data.get("title"))
            .setContentText(remoteMessage.data.get("message"))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent);

        notificationBuilder.color = resources.getColor(R.color.background_medium_dark);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager:NotificationManager?){
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to device notification "

        val adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)

        notificationManager?.createNotificationChannel(adminChannel)
    }

    fun sendNotificationToDevice(){
        val tokenToSpecificDevice = "eClCIq20RhOQ4auNuXFwH7:APA91bElu0_Z57eUjLmSEEBcOvUr0ZIwrwoSGJBsdfGP3N1b1HJIjw0nfX3UXp6sHCH5ghXuUf7PIVEQ5LodIo6B9pLYLGMe9iwlkBCVI0ZomLgsk-aTMPATgMlElwturpKqd9yCcpIt"
        NOTIFICATION_TITLE = "LETS TRY IT"
        NOTIFICATION_MESSAGE = "IF SOMEONE SEES THIS, I GUESS IT WORKED"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", tokenToSpecificDevice);
            notification.put("data", notifcationBody);
        } catch (err:Exception) {
            logMessage(err.message.toString())
        }
        sendNotificationJson(notification);
    }

    fun sendNotificationToSubscribedDevices(){
        val tokenToSpecificDevice = "en56SlpXTAKOFY5VpgLpSy:APA91bF7e_13aCXnX1XQVEvsdB6kWDtILixh-9pDVzDK4e8afOwLfX_bqJycFELMOp4A1Ub61ZvRdeXOOCYw-V0sT2eDwegm4jYTQ5N36s-Tj0Tp_1UdcFY9znes1abOmYlPjXh2Sapm"
        TOPIC = "/$DELIVERY_TOPIC/${tokenToSpecificDevice}"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "LETS TRY IT"
        NOTIFICATION_MESSAGE = "IF SOMEONE SEES THIS, I GUESS IT WORKED"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (err:Exception) {
            logMessage(err.message.toString())
        }
        sendNotificationJson(notification);
    }

    private fun sendNotificationJson(notification:JSONObject){
        val jsonObjectRequest:JsonObjectRequest = object : JsonObjectRequest(
            POST,
            FCM_API,
            notification,
            Response.Listener<JSONObject>{ response->
                    logMessage("onResponse: $response")
            },
            Response.ErrorListener{ error ->
                logMessage("onErrorResponse: Didn't work")
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = FIREBASE_SERVER_KEY
                params["Content-Type"] = CONTENT_TYPE;
                return params
            }
        }
        //if this doesnt work change back to activity
        Singleton.getInstance(applicationContext)?.addToRequestQueue(jsonObjectRequest);
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance(this)
            .beginWith(work)
            .enqueue()
    }

    private fun appNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    /*fun token(): Task<String> {
        return firestoreMessage.token
    }

    fun refresh(){
        //return firestoreDB.collection(RESTAURANT_OWNER_COLLECTION).document(restaurant.restaurantId!!).
    }

    fun subscribe(topic:String): Task<Void> {
        return firestoreMessage.subscribeToTopic(topic)
    }

    fun send(msg: RemoteMessage){
        firestoreMessage.send(msg)


        suspend fun getRegistrationToken():String{
        return try {
            firebaseRepository.token().await()
        }
        catch (e: Exception) {
            return ""
        }
    }

    suspend fun subscribeToTopic(topic:String){
        firebaseRepository.subscribe(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                logMessage(msg)
            }.await()
    }

    fun sendMessage(){
        val msg = RemoteMessage.Builder("$SENDER_ID@fcm.googleapis.com")
            .setMessageId(messageId.toString())
            .addData("New Message","Hello From The Other Side")
            .addData("My Action","Say Hey")
            .build()
        firebaseRepository.send(msg)
        messageId+=1
    }

    fun refreshToken(token:String){
        val updatedToken: MutableMap<String, String> = mutableMapOf(
            "token" to token,
        )
        firebaseRepository.refresh()
    }

    /*
    *
    * fun runtimeEnableAutoInit() {
        Firebase.messaging.isAutoInitEnabled = true
    }

    fun deviceGroupUpstream() {
        val to = "a_unique_key" // the notification key
        val msgId = AtomicInteger()
        Firebase.messaging.send(remoteMessage(to) {
            setMessageId(msgId.get().toString())
            addData("hello", "world")
        })
    }
    *
    * */
    }*/

    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            // TODO(developer): add long running task here.
            return Result.success()
        }
    }
}

