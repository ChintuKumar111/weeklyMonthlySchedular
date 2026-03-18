package com.example.freshyzoappmodule.helper

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.util.*
class AppHashGenerator(context: Context) : ContextWrapper(context) {
    companion object {
        private const val TAG = "AppSignatureHelper"
    }

    fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()

        val packageName = packageName
        val packageManager = packageManager

        val packageInfo = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        val signatures = packageInfo?.signatures

        if (signatures != null) {
            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    Log.d(TAG, "App Hash: $hash")
                    appCodes.add(hash)
                }
            }
        }
        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"

        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(appInfo.toByteArray())
        val hashSignature = messageDigest.digest()
        val truncatedHash = Arrays.copyOfRange(hashSignature, 0, 9)

        val base64Hash = Base64.encodeToString(
            truncatedHash,
            Base64.NO_PADDING or Base64.NO_WRAP
        )
        return base64Hash.substring(0, 11)
    }
}
