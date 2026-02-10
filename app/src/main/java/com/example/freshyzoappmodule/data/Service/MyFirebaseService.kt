package com.example.freshyzoappmodule.data.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.example.freshyzoappmodule.view.Activity.NotificationActivity

class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token generated: $token")

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOPIC", "Subscribed to all_users topic")
                } else {
                    Log.e("FCM_TOPIC", "Subscription failed")
                }
            }
    }


    // In MyFirebaseService.kt, update the onMessageReceived to handle data-only messages better
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_MESSAGE", "Data: ${message.data}")

        // Priority: Data payload (for data-only messages) -> Notification payload (fallback)
        val title = message.data["title"] ?: message.notification?.title ?: "New Notification"
        val body = message.data["message"] ?: message.data["body"] ?: message.notification?.body ?: ""

        if (body.isNotEmpty()) {
            saveNotification(title, body)
            notifyNotificationScreen()
            showNotification(title, body)
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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = try {
            BitmapFactory.decodeResource(resources, R.drawable.app_icon)
        } catch (e: Exception) {
            null
        }

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
        val oldData = prefs.getString("list", "[]") ?: "[]"
        val jsonArray = JSONArray(oldData)

        val jsonObject = JSONObject().apply {
            put("title", title)
            put("message", message)
            put("time", System.currentTimeMillis())
        }

        jsonArray.put(jsonObject)
        prefs.edit().putString("list", jsonArray.toString()).apply()
        Log.d("FCM_STORAGE", "Notification saved to SharedPreferences")
    }

    private fun notifyNotificationScreen() {
        val intent = Intent("com.example.freshyzoappmodule.NEW_NOTIFICATION")
        // Set package name to ensure it's an explicit broadcast
        intent.setPackage(packageName)
        sendBroadcast(intent)
        Log.d("FCM_BROADCAST", "Broadcast sent for new notification")
    }
}
