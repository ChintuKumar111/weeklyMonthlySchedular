package com.example.freshyzoappmodule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.Adapter.NotificationAdapter
import com.example.freshyzoappmodule.databinding.ActivityNotificationBinding
import com.example.freshyzoappmodule.model.NotificationModel
import org.json.JSONArray

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshNotifications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(emptyList())
        binding.rvNotifications.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("NEW_NOTIFICATION_RECEIVED")
        // Specify RECEIVER_NOT_EXPORTED for apps targeting API 34+
        ContextCompat.registerReceiver(
            this,
            notificationReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(notificationReceiver)
    }

    override fun onResume() {
        super.onResume()
        refreshNotifications()
    }

    private fun refreshNotifications() {
        val list = loadNotifications().reversed()
        adapter.updateList(list)
    }

    private fun loadNotifications(): List<NotificationModel> {
        val prefs = getSharedPreferences("notifications", MODE_PRIVATE)
        val json = prefs.getString("list", "[]") ?: "[]"
        val jsonArray = JSONArray(json)

        val list = mutableListOf<NotificationModel>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                NotificationModel(
                    obj.getString("title"),
                    obj.getString("message"),
                    obj.getLong("time")
                )
            )
        }
        return list
    }
}
