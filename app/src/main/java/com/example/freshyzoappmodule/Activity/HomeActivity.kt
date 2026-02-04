package com.example.freshyzoappmodule.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.freshyzoappmodule.databinding.ActivityHomeBinding

class HomeActivity : BaseActivityy() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendButton.setOnClickListener {
            logButtonClick("sendButton")

            Toast.makeText(this,"send button clicked ",Toast.LENGTH_SHORT).show()
        }

        binding.receiveButton.setOnClickListener {
            logButtonClick("receiveButton")
            Toast.makeText(this,"receive button clicked ",Toast.LENGTH_SHORT).show()

        }

    }
}