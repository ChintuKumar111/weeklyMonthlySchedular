package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.model.chatListItem
import com.example.freshyzoappmodule.databinding.ActivityChatListBinding
import com.example.freshyzoappmodule.ui.adapter.ChatListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var adapter: ChatListAdapter
    private val db = FirebaseFirestore.getInstance()
    private val chatList = mutableListOf<chatListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        listenForChats()
    }

    private fun setupRecyclerView() {
        adapter = ChatListAdapter(chatList) { chatItem ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("CHAT_ID", chatItem.chatId)
            intent.putExtra("OTHER_USER_NAME", chatItem.otherUserName)
            startActivity(intent)
        }
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = adapter
    }

    private fun listenForChats() {
        db.collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val newList = mutableListOf<chatListItem>()
                    for (doc in snapshots.documents) {
                        val chatItem = doc.toObject(chatListItem::class.java)?.apply {
                            chatId = doc.id
                        }
                        if (chatItem != null) {
                            newList.add(chatItem)
                        }
                    }
                    adapter.updateList(newList)
                }
            }
    }
}
