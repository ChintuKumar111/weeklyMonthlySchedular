package com.example.freshyzoappmodule.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // TOAST FOR DEBUGGING - This will prove if the receiver is alive
        Toast.makeText(context, "SmsReceiver triggered!", Toast.LENGTH_SHORT).show()
        Log.d("SmsReceiver", "Action: ${intent?.action}")
        
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val messageBody = sms.displayMessageBody
                Log.d("SmsReceiver", "Body: $messageBody")
                
                val otpRegex = Regex("(\\d{6})")
                val match = otpRegex.find(messageBody)
                
                match?.let {
                    val otp = it.value
                    Log.d("SmsReceiver", "OTP found: $otp")
                    
                    // Trigger Internal Broadcast
                    val otpIntent = Intent("OTP_RECEIVED")
                    otpIntent.putExtra("otp", otp)
                    otpIntent.setPackage(context?.packageName)
                    context?.sendBroadcast(otpIntent)
                    
                    Toast.makeText(context, "OTP Detected: $otp", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}