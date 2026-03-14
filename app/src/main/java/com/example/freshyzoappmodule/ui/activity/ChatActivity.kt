package com.example.freshyzoappmodule.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.model.ChatMessage
import com.example.freshyzoappmodule.databinding.ActivityChatBinding
import com.example.freshyzoappmodule.ui.adapter.ChatAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ChatActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private var userId = "user1" // Current User
    private var chatId: String? = null

    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get chatId from intent
        chatId = intent.getStringExtra("CHAT_ID")

        // Safety check: If chatId is null or empty, don't proceed
        if (chatId.isNullOrEmpty()) {
            finish()
            return
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
        val id = chatId ?: return
        val currentTime = System.currentTimeMillis()

        val message = hashMapOf(
            "senderId" to userId,
            "message" to text,
            "timestamp" to currentTime,
            "read" to false
        )

        db.collection("chats")
            .document(id)
            .collection("messages")
            .add(message)

        val chatSummary = hashMapOf(
            "chatId" to id,
            "lastMessage" to text,
            "timestamp" to currentTime,
            "otherUserName" to (intent.getStringExtra("OTHER_USER_NAME") ?: "Support"),
            "otherUserId" to userId
        )

        db.collection("chats").document(id)
            .set(chatSummary, SetOptions.merge())
    }

    private fun listenMessages() {
        val id = chatId ?: return

        db.collection("chats")
            .document(id)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == DocumentChange.Type.ADDED) {
                        val message = change.document.toObject(ChatMessage::class.java)
                        if (message != null) {
                            messageList.add(message)
                            adapter.notifyItemInserted(messageList.size - 1)
                            binding.rvChat.scrollToPosition(messageList.size - 1)
                        }
                    }
                }
            }
    }
}