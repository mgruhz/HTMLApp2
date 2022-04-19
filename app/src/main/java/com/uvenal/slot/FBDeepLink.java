package com.uvenal.slot;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;

public class FBDeepLink extends Application {
    public static String campaign;
    @Override
    public void onCreate(){
        super.onCreate();
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        campaign = appLinkData.toString();
                    }
                }
        );
    }
}