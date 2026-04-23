package com.shyamdairyfarm.user.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * BroadcastReceiver to wait for SMS messages. This receiver will be registered
 * in the AndroidManifest.xml and will wait for the SMS_RETRIEVED_ACTION.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras: Bundle? = intent.extras
            val status: Status? = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String
                    Log.d("SmsReceiver", "Retrieved message: $message")
                    
                    message?.let {
                        // Extract OTP from message (e.g., 6 digits)
                        val otpRegex = Regex("(\\d{6})")
                        val match = otpRegex.find(it)
                        
                        match?.let { result ->
                            val otp = result.value
                            Log.d("SmsReceiver", "OTP found: $otp")
                            
                            // Send OTP to OtpFragment using a local broadcast
                            val otpIntent = Intent("OTP_RECEIVED")
                            otpIntent.putExtra("otp", otp)
                            otpIntent.setPackage(context?.packageName)
                            context?.sendBroadcast(otpIntent)
                        }
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    // Waiting for SMS timed out (5 minutes)
                    Log.d("SmsReceiver", "SMS Retriever timed out")
                }
            }
        }
    }
}
