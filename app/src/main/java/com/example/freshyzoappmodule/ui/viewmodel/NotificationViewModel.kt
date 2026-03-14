package com.example.freshyzoappmodule.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.freshyzoappmodule.data.model.NotificationItem
import org.json.JSONArray

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableLiveData<List<NotificationItem>>()
    val notifications: LiveData<List<NotificationItem>> = _notifications

    init {
        refreshNotifications()
    }

    fun refreshNotifications() {
        val list = loadNotifications().reversed()
        _notifications.value = list
    }

    private fun loadNotifications(): List<NotificationItem> {
        val prefs = getApplication<Application>().getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val json = prefs.getString("list", "[]") ?: "[]"
        val jsonArray = JSONArray(json)

        val list = mutableListOf<NotificationItem>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                NotificationItem(
                    obj.getString("title"),
                    obj.getString("message"),
                    obj.getLong("time")
                )
            )
        }
        return list
    }
}
