package com.uvenal.slot.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.uvenal.slot.AFApplication
import com.uvenal.slot.R
import com.uvenal.slot.components.view.JSBridge
import com.uvenal.slot.components.view.WV
import com.uvenal.slot.data.api.ApiService
import com.uvenal.slot.databinding.FragmentWebviewBinding
import com.uvenal.slot.utils.Constants
import com.uvenal.slot.utils.Keys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.Key


class WebViewFragment : Fragment() {
    private val mPrefs: SharedPreferences by lazy { activity!!.getSharedPreferences(Keys.GLOBAL_SHARED.code, Context.MODE_PRIVATE) }
    private lateinit var webView: WebView
    private var OC = 0
    //
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)
        webView = view.findViewById(R.id.webview)

        if(arguments?.getBoolean("non-organic", false) == true){
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webView.settings.pluginState = WebSettings.PluginState.ON
            webView.settings.allowFileAccess = true
            WV.setParams(webView, mPrefs)
            if(arguments!!.getString("result_url") != null){
                webView.loadUrl(arguments!!.getString("result_url").toString())
                webView.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action === MotionEvent.ACTION_UP && webView.canGoBack()) {
                            webView.goBack()
                            return true
                        } else {
                            OC++;
                            if (OC == 2) {
                                OC = 0;
                                Constants.FIRST_URL =
                                    mPrefs.getString(Keys.FOU_KEY.code, "Error").toString();
                                webView.loadUrl(Constants.FIRST_URL)
                            }
                            return false
                        }
                    }
                })
            } else {
                webView.loadUrl(mPrefs.getString(Keys.LOU_KEY.code, Keys.WCK_KEY.code)
                    .toString())
                webView.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action === MotionEvent.ACTION_UP && webView.canGoBack()) {
                            webView.goBack()
                            return true
                        } else {
                            OC++;
                            if (OC == 2) {
                                OC = 0;
                                Constants.FIRST_URL =
                                    mPrefs.getString(Keys.FOU_KEY.code, "Error").toString();
                                webView.loadUrl(Constants.FIRST_URL)
                            }
                            return false
                        }
                    }
                })
            }
        } else {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            WindowInsetsControllerCompat(requireActivity().window, webView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            loadApplication()
        }
        return view
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadApplication(){
        val u = "https://appassets.androidplatform.net/assets/index.html"
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.addJavascriptInterface(JSBridge(requireContext()), "JSBridge")
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireContext()))
            .build()
        webView.webViewClient = LocalContentWebViewClient(assetLoader)
        webView.loadUrl(u)
        webView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event?.action === MotionEvent.ACTION_UP && webView.canGoBack()) {
                    webView.goBack()
                    return true
                } else {
                    OC++;
                    if (OC == 2) {
                        OC = 0;
                        Constants.FIRST_URL =
                            mPrefs.getString(Keys.FOU_KEY.code, "Error").toString();
                        webView.loadUrl(Constants.FIRST_URL)
                    }
                    return false
                }
            }
        })
    }
    private class LocalContentWebViewClient(private val assetLoader: WebViewAssetLoader) : WebViewClientCompat() {
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }
    }
    override fun onPause() {
        super.onPause()
        webView.evaluateJavascript("javascript:window.ApplicationSoundOff()", null)
        if(arguments?.getBoolean("non-organic", false) == true){
            val edit = mPrefs.edit()
            edit.putString(Keys.LOU_KEY.code, webView.url).putInt(Keys.FOV_KEY.code, Constants.OPEN).apply()
        }
    }
    override fun onResume() {
        super.onResume()
        webView.evaluateJavascript("javascript:window.ApplicationSoundOn()", null)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, webView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}