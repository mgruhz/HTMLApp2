package com.uvenal.slot;



import static com.uvenal.slot.utils.Keys.GLOBAL_SHARED;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.uvenal.slot.utils.Constants;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class AFApplication extends Application{
    public static String campaign;
    private SharedPreferences mPrefs;////
    private JSONObject json_log;//
    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = getSharedPreferences(GLOBAL_SHARED.getCode(), MODE_PRIVATE);
        json_log = new JSONObject();
        SharedPreferences.Editor editor = mPrefs.edit();
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                campaign = conversionData.get("campaign").toString();
            }
            @Override public void onConversionDataFail(String errorMessage) {
//                try {
//                    editor.putString("json", json_log.toString()).apply();
//                    json_log.put("appsflyer_error_onconversiondatafail", "error getting conversion data: " + errorMessage);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Log.d("APP_CHECK", "error getting conversion data: " + errorMessage);
            }
            @Override public void onAppOpenAttribution(Map<String, String> attributionData) { for (String attrName : attributionData.keySet()) {
//                try {
//                    json_log.put("appsflyer_error_onappopenattribution", "attribute: " + attrName + " = " + attributionData.get(attrName));
//                    editor.putString("json", json_log.toString()).apply();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Log.d("APP_CHECK", "attribute: " + attrName + " = " + attributionData.get(attrName));
            } }
            @Override public void onAttributionFailure(String errorMessage) {
//                try {
//                    json_log.put("appsflyer_error_onappopenattribution", "error onAttributionFailure : " + errorMessage);
//                    editor.putString("json", json_log.toString()).apply();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Log.d("APP_CHECK", "error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().init(Constants.APPSFLYER, conversionListener, this);
        AppsFlyerLib.getInstance().start(this);
    }
    public void oneSignalNotification(Context context, String KEY) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(context);
        OneSignal.setAppId(KEY);
    }
}

