package com.example.freshyzoappmodule.data.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.freshyzoappmodule.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject
import com.example.freshyzoappmodule.ui.Activity.NotificationActivity

class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token generated: $token")

        // Automatically subscribe this device to the "all_users" topic
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOPIC", "Subscribed to all_users topic")
                } else {
                    Log.e("FCM_TOPIC", "Subscription failed")
                }
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        Log.d("FCM_MESSAGE", "From: ${message.from}")

        val title = message.notification?.title ?: "New Notification"
        val body = message.notification?.body ?: ""
        saveNotification(title, body)

        notifyNotificationScreen() // 🔥 THIS LINE

        // Handle Notification Payload
        message.notification?.let {
            Log.d("FCM_MESSAGE", "Message Body: ${it.body}")
            showNotification(it.title ?: "Notification", it.body ?: "")
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, NotificationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.app_icon) // full color logo



        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun saveNotification(title: String, message: String) {
        val prefs = getSharedPreferences("notifications", MODE_PRIVATE)

        val oldData = prefs.getString("list", "[]")
        val jsonArray = JSONArray(oldData)

        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("message", message)
        jsonObject.put("time", System.currentTimeMillis())

        jsonArray.put(jsonObject)

        prefs.edit().putString("list", jsonArray.toString()).apply()
    }

    private fun notifyNotificationScreen() {
        val intent = Intent("NEW_NOTIFICATION_RECEIVED")
        sendBroadcast(intent)
    }

}
