package com.uvenal.slot.notification

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uvenal.slot.R
import org.json.JSONObject

class FirebaseService : FirebaseMessagingService() {
    private val logTag = "FCM"
    private val fcmNotificationID = 999

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(logTag, "received FCM message : ${remoteMessage.data}")

        when (remoteMessage.data["type"].toString()) {
            "inapp" -> {
                val pushData = JSONObject(remoteMessage.data["inapp"].toString())
                PushNotification(this).inAppNotification(
                    pushData.getString("title"),
                    pushData.getString("body"),
                    pushData.getString("image"),
                )
            }
            "push" -> {
                val pushData = JSONObject(remoteMessage.data["push"].toString())

                val modelClass = DataModel(
                    pushData.getString("title"),
                    pushData.getString("body"),
                    pushData.getString("image"),
                    pushData.getString("url")
                )

                val builder = NotificationCompat.Builder(this, applicationContext.resources.getString(
                    R.string.default_notification_channel_id
                ))

                val loader = ImageLoader(this)
                val req = ImageRequest.Builder(this)
                    .data(modelClass.image)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        PushNotification(this).startNotification(
                            modelClass,
                            builder,
                            bitmap,
                            fcmNotificationID
                        )
                    }
                    .build()

                loader.enqueue(req)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(logTag, "Refreshed token: $token")

        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener { task ->
            Log.d(logTag, "Firebase subscribe topic: all")
            if (task.isSuccessful) {
                Log.d(logTag, "Firebase subscribe all OK")
            } else {
                Log.e(logTag, "Firebase subscribe all failed")
            }
        }

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val country = telephonyManager.networkCountryIso

        FirebaseMessaging.getInstance().subscribeToTopic(country).addOnCompleteListener { task ->
            Log.d(logTag, "Firebase subscribe topic: $country")
            if (task.isSuccessful) {
                Log.d(logTag, "Firebase subscribe $country OK")
            } else {
                Log.e(logTag, "Firebase subscribe $country failed")
            }
        }
    }
}