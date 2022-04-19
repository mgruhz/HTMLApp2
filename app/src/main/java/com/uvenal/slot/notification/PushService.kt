package com.uvenal.slot.notification

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.uvenal.slot.utils.Keys

class PushService : FirebaseMessagingService() {

    fun getToken(mPrefs: SharedPreferences){
        val editor = mPrefs.edit()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            val token = task.result
            Log.i("APP_CHECK", "getToken: $token")
            editor.putString(Keys.p_token.code, token).apply()
        }
    }
}