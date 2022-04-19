package com.uvenal.slot.components.someUtils;


import static com.uvenal.slot.utils.Keys.p_deepLink;

import android.content.Context;
import android.content.SharedPreferences;

import com.uvenal.slot.AFApplication;
import com.uvenal.slot.FBDeepLink;
import com.uvenal.slot.R;
import com.uvenal.slot.utils.Constants;
import com.uvenal.slot.utils.Keys;

public class ParamUtils {
    public static String replace_param(String url, SharedPreferences mPrefs) {
        if (url.contains("domain_url")) url = url.replace("domain_url", Constants.DOMAIN);
        if (url.contains("param_install_id")) url = url.replace("param_install_id", mPrefs.getString(Keys.p_installID.getCode(), "proeb"));
        if (url.contains("param_deeplink")) url = url.replace("param_deeplink", mPrefs.getString(p_deepLink.getCode(), "proeb"));
        return url;
    }
    public static void paramChecker(SharedPreferences mPrefs){
        SharedPreferences.Editor editor = mPrefs.edit();
        if(FBDeepLink.campaign != null){ editor.putString(p_deepLink.getCode(), FBDeepLink.campaign).apply(); }
        if(AFApplication.campaign != null) { editor.putString(p_deepLink.getCode(), AFApplication.campaign).apply(); }
    }
}
