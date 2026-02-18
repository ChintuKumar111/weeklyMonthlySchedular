package com.example.freshyzoappmodule.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.model.Message
import com.example.freshyzoappmodule.databinding.ActivityChatBinding
import com.example.freshyzoappmodule.view.adapter.ChatAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ChatActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val messageList = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter

    private var userId = "user1"
    private var agentId = "agent1"
    private var chatId = "${userId}_${agentId}"

    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get chatId from intent if available
        intent.getStringExtra("CHAT_ID")?.let {
            chatId = it
        }

        adapter = ChatAdapter(messageList, userId)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        listenMessages()

        binding.btnSend.setOnClickListener {
            val text = binding.edtMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.edtMessage.setText("")
            }
        }
    }

    private fun sendMessage(text: String) {
        val currentTime = System.currentTimeMillis()

        val message = hashMapOf(
            "senderId" to userId,
            "message" to text,
            "timestamp" to currentTime,
            "read" to false
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)

        val chatSummary = hashMapOf(
            "chatId" to chatId,
            "lastMessage" to text,
            "timestamp" to currentTime,
            "otherUserName" to "Customer Name", // Replace with actual name if possible
            "otherUserId" to userId
        )

        db.collection("chats").document(chatId)
            .set(chatSummary, SetOptions.merge())
    }

    private fun listenMessages() {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                for (change in snapshots!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val message = change.document.toObject(Message::class.java)
                        messageList.add(message)
                        adapter.notifyDataSetChanged()
                        binding.rvChat.scrollToPosition(messageList.size - 1)
                    }
                }
            }
    }
}
