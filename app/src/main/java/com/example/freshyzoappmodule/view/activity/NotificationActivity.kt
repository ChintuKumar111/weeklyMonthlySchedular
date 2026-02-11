package com.example.freshyzoappmodule.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.freshyzoappmodule.databinding.ActivityNotificationBinding
import com.example.freshyzoappmodule.view.adapter.NotificationAdapter
import com.example.freshyzoappmodule.viewmodel.NotificationViewModel

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("NOTIFICATION_UI", "Broadcast received, refreshing list")
            viewModel.refreshNotifications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(emptyList())
        binding.rvNotifications.adapter = adapter
        
       // binding.btnBack.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.notifications.observe(this) { list ->
            adapter.updateList(list)
        }
    }

    override fun onStart() {
        super.onStart()
        // Action must match MyFirebaseService precisely
        val filter = IntentFilter("com.example.freshyzoappmodule.NEW_NOTIFICATION")
        ContextCompat.registerReceiver(
            this,
            notificationReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED // Changed to EXPORTED for cross-process app communication
        )
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(notificationReceiver)
        } catch (e: Exception) {
            Log.e("NOTIFICATION_UI", "Error unregistering receiver", e)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshNotifications()
    }
}
