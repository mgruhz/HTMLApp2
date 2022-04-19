package com.uvenal.slot.components.view;

import static com.uvenal.slot.activities.SplashActivity.contextS;
import static com.uvenal.slot.utils.Keys.FOU_KEY;
import static com.uvenal.slot.utils.Keys.FOV_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.navigation.Navigation;

import com.uvenal.slot.R;
import com.uvenal.slot.utils.Constants;

public class WV {
    private static String coma = "\"\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>\\u003C/body>\\u003C/html>\"";

    @SuppressLint("SetJavaScriptEnabled")
    public static void setParams(WebView wov, SharedPreferences mPrefs) {
        WebSettings webSettings = wov.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        wov.getSettings().setPluginState(WebSettings.PluginState.ON);
        wov.getSettings().setAllowFileAccess(true);

        wov.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Log.i("TAG", "shouldOverrideUrlLoading Exception:" + e);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (Constants.OPEN == 0) {
                    Constants.ITERATION++;
                    if (Constants.ITERATION == 1) {
                        Constants.OPEN = 1;
                        Constants.FIRST_URL = wov.getUrl();
                        SharedPreferences.Editor edit = mPrefs.edit();
                        edit.putString(FOU_KEY.getCode(), Constants.FIRST_URL);
                        edit.putInt(FOV_KEY.getCode(), Constants.OPEN);
                        edit.apply();
                    }
                }

                view.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        html -> {
                            if (html.equals(coma)) {
                                Navigation.findNavController((Activity) contextS, R.id.fragment_container).navigate(R.id.htmlFragment);
                                Log.i( "APP_CHECK", "[WebView] Empty content on site, opened Activity");
                            }
                        });

            }
        });
    }
}
