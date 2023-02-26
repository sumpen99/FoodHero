package com.example.foodhero.messaging
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.AuthFailureError
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.foodhero.MainActivity
import com.example.foodhero.global.*
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*
//https://developer.android.com/develop/ui/views/notifications/build-notification
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
        private var myToken = ""
        private lateinit var callbackOnTokenRefresh:((args:String)->Unit)
        private lateinit var callbackOnMessageRecieved:((args:RemoteMessage)->Unit)
        private var instance: FirestoreMessaging? = null
        private fun isCallbackOnTokenRefreshInitialized() = ::callbackOnTokenRefresh.isInitialized
        private fun isCallbackOnMessageRecievedInitialized() = ::callbackOnMessageRecieved.isInitialized

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

        fun setMessagedRecievedCallback(callback:(args:RemoteMessage)->Unit){
            callbackOnMessageRecieved = callback
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

        fun messageReceived(remoteMessage: RemoteMessage){
            if(isCallbackOnMessageRecievedInitialized()){
                callbackOnMessageRecieved(remoteMessage)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setupChannels(notificationManager: NotificationManager){
            val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val adminChannelName: CharSequence = "New notification"
            val adminChannelDescription = "Device to device notification "

            val adminChannel = NotificationChannel(
                ADMIN_CHANNEL_ID,
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            adminChannel.description = adminChannelDescription
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            adminChannel.enableVibration(true)
            adminChannel.setSound(sound,attributes)

            notificationManager.createNotificationChannel(adminChannel)
        }

    }

    override fun onNewToken(token: String) {
        onTokenRefresh(token)
    }

    private fun loadToken():Task<String>{
        return firestoreMessage!!.token
    }

     override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            messageReceived(remoteMessage)

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
 }

    fun buildJsonRespondBody(
        tokenToSpecificDevice:String,):JSONObject?{
        NOTIFICATION_TITLE = "Order Bekräftelse"
        NOTIFICATION_MESSAGE = "Maten är på väg"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        return try {
            notifcationBody.put("title", NOTIFICATION_TITLE)
            notifcationBody.put("message", NOTIFICATION_MESSAGE)
            notification.put("to", tokenToSpecificDevice)
            notification.put("data", notifcationBody)
        } catch (err:Exception) {
            logMessage(err.message.toString())
            null
        }
    }

    fun buildJsonOrderBody(
        tokenToRestaurant:String,
        tokenToRespondTo:String,):JSONObject?{
        NOTIFICATION_TITLE = "Order"
        NOTIFICATION_MESSAGE = "Jag vill ha mat"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        return try {
            notifcationBody.put("title", NOTIFICATION_TITLE)
            notifcationBody.put("message", NOTIFICATION_MESSAGE)
            notifcationBody.put("token", tokenToRespondTo)
            notification.put("to", tokenToRestaurant)
            notification.put("data", notifcationBody)
        } catch (err:Exception) {
            logMessage(err.message.toString())
            null
        }
    }

    fun buildJsonTopicAndDevicesBody():JSONObject?{
        val tokenToSpecificDevice = "en56SlpXTAKOFY5VpgLpSy:APA91bF7e_13aCXnX1XQVEvsdB6kWDtILixh-9pDVzDK4e8afOwLfX_bqJycFELMOp4A1Ub61ZvRdeXOOCYw-V0sT2eDwegm4jYTQ5N36s-Tj0Tp_1UdcFY9znes1abOmYlPjXh2Sapm"
        TOPIC = "/$DELIVERY_TOPIC/${tokenToSpecificDevice}"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "LETS TRY IT"
        NOTIFICATION_MESSAGE = "IF SOMEONE SEES THIS, I GUESS IT WORKED"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        return try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (err:Exception) {
            logMessage(err.message.toString())
            null
        }
    }

    fun sendJsonNotification(context:Context,notification:JSONObject){
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
        Singleton.getInstance(context)?.addToRequestQueue(jsonObjectRequest);
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

    /*
    *
    * /*
        * val snoozeIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
        action = ACTION_SNOOZE
        putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val snoozePendingIntent: PendingIntent =
        PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                    snoozePendingIntent)
        * */
    *
    *
    *
    * @RequiresApi(Build.VERSION_CODES.O)
    private fun replyToNotification(){
        val KEY_TEXT_REPLY = "key_text_reply"
        var replyLabel: String = resources.getString(R.string.reply_label)
        var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(replyLabel)
            build()
        }

        var replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(applicationContext,
                conversation.getConversationId(),
                getMessageReplyIntent(conversation.getConversationId()),
                PendingIntent.FLAG_UPDATE_CURRENT)

        var action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(R.drawable.ic_stat_ic_notification_foreground,
                getString(R.string.reply_label), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val newMessageNotification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(getString(R.string.title))
            .setContentText(getString(R.string.content))
            .addAction(action)
            .build()

            with(NotificationManagerCompat.from(this)) {
                notificationManager.notify(notificationId, newMessageNotification)
            }
    }
    *
    * */


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

