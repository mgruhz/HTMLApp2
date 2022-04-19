package com.uvenal.slot.components.someUtils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.squareup.okhttp.RequestBody
import com.uvenal.slot.BuildConfig
import com.uvenal.slot.R
import com.uvenal.slot.data.api.ApiService
import com.uvenal.slot.utils.Constants
import com.uvenal.slot.utils.Keys
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

class InstallData(private val context: Context, private val mPrefs: SharedPreferences) {
    fun Process() {
        val editor = mPrefs.edit()

        val tM = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if(mPrefs.getString(Keys.p_installID.code, null) == null) editor.putString(Keys.p_installID.code, UUID.randomUUID().toString()).apply()
        var job: Job = CoroutineScope(Dispatchers.IO).launch {
//            ApiService.retrofit.sendInfoInstall(
//                mPrefs.getString(Keys.p_installID.code, "none").toString(),
//                tM.simOperatorName,
//                tM.networkOperator,
//                tM.networkCountryIso,
//                tM.simOperatorName,
//                Build.MANUFACTURER,
//                Build.MODEL,
//                Locale.getDefault().language,
//                Build.VERSION.RELEASE,
//                TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT),
//                TimeZone.getDefault().id,
//                context.packageName,
//                BuildConfig.VERSION_CODE.toString()
//            )
//            ApiService.retrofit.sendFirebaseToken(
//                mPrefs.getString(Keys.p_installID.code, "none").toString(),
//                mPrefs.getString(Keys.p_token.code, "none").toString()
//            )
//            ApiService.retrofit.sendDeepLink(
//                mPrefs.getString(Keys.p_installID.code, "none").toString(),
//                mPrefs.getString(Keys.p_deepLink.code, "none").toString()
//            )
//            if(mPrefs.getString("json", null) != null) {
//                val jsonObject = JSONObject(mPrefs.getString("json", null))
//                val jsonObjectString = jsonObject.toString()
//                val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
//                ApiService.retrofit.sendLog(
//                    context.resources.getString(R.string.log_token),
//                    requestBody
//                )
//                Log.i("APP_CHECK", "Process: $jsonObject")
//            }

            withContext(coroutineContext){
                Log.i("APP_CHECK", "[API]: Данные были отправлены")
            }
        }
    }
}