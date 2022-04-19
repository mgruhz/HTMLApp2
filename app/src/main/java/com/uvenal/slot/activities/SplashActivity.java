package com.uvenal.slot.activities;

import static com.uvenal.slot.utils.Keys.FOU_KEY;
import static com.uvenal.slot.utils.Keys.FOV_KEY;
import static com.uvenal.slot.utils.Keys.GLOBAL_SHARED;
import static com.uvenal.slot.utils.Keys.LOU_KEY;
import static com.uvenal.slot.utils.Keys.WCK_KEY;
import static com.uvenal.slot.utils.Keys.p_timeSplash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uvenal.slot.AFApplication;
import com.uvenal.slot.FBDeepLink;
import com.uvenal.slot.R;
import com.uvenal.slot.components.someUtils.AppsflyerChecker;
import com.uvenal.slot.components.someUtils.InstallData;
import com.uvenal.slot.components.someUtils.ParamUtils;
import com.uvenal.slot.components.view.ImageLoader;
import com.uvenal.slot.components.view.WV;
import com.uvenal.slot.databinding.FragmentWebviewBinding;
import com.uvenal.slot.notification.FirebaseService;
import com.uvenal.slot.notification.PushService;
import com.uvenal.slot.screens.WebViewFragment;
import com.uvenal.slot.utils.Constants;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private static final String app_check = "APP_CHECK";
    private boolean run = false;
    private double pastTense = 0.0;
    private int a = 0;

    private JSONObject json_log;

    SharedPreferences mPrefs;
    private WebView wov;
    private FirebaseFirestore db;
    private static int OC = 0;
    private static final int IC = 1, FR = 1;
    private static final String TAG = SplashActivity.class.getSimpleName();
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private String firebase, last_url, result_url;
    public static Context contextS;


    private FragmentContainerView col;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        contextS = this;
        FirebaseApp.initializeApp(this);
        AFApplication application = new AFApplication();
        application.oneSignalNotification(getApplicationContext(), Constants.ONE_SIGNAL_ID);
        init();
        Constants.DOMAIN = getResources().getString(R.string.DOMAIN);

//        try {
//            json_log = new JSONObject(mPrefs.getString("json", ""));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        Constants.OPEN = mPrefs.getInt(FOV_KEY.getCode(), 0);
        last_url = mPrefs.getString(LOU_KEY.getCode(), WCK_KEY.getCode());
        splashLoader();
    }
    @SuppressLint("CutPasteId")
    private void init() {
        col = findViewById(R.id.fragment_container);

        mPrefs = getSharedPreferences(GLOBAL_SHARED.getCode(), MODE_PRIVATE);


        CookieSyncManager.createInstance(getApplicationContext());
        wov = new WebView(this);
        wov.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));

        db = FirebaseFirestore.getInstance();

        new PushService().getToken(mPrefs);
        FirebaseService fb = new FirebaseService();


        getPackageManager();
        wvSettings();
    }

    private void getOrganicData() throws JSONException {
        result_url = Constants.URL;
        ParamUtils.paramChecker(mPrefs);
        result_url = ParamUtils.replace_param(result_url, mPrefs);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        if(AFApplication.campaign != null){
//            json_log.put("type_naming", "campaign");
//        } else {
//            json_log.put("type_naming", "deeplink");
//        }
//        json_log.put("result_url", result_url);
//        json_log.put("type_open", "first");
        Bundle bundle = new Bundle();
        bundle.putBoolean("non-organic", true);
        bundle.putString("result_url", result_url);
        Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.htmlFragment, bundle);
        Log.i(app_check, "[RESULT URL] " + result_url);
//        SharedPreferences.Editor edit = mPrefs.edit();
//        edit.putString("json", json_log.toString()).apply();
        new InstallData(getApplicationContext(), mPrefs).Process();
    }

    private int getTimeLoading(){ return Constants.LOADING_TIME*10; }

    private void splashLoader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Log.i(app_check, "[Loading Time]: " + getTimeLoading()/10 + " sec. (" + Constants.LOADING_TIME*10 + " ms.)");
                    SharedPreferences.Editor editor = mPrefs.edit();
                    while (!run) {
                        if(hasConnection(getApplicationContext())){
                            if(!last_url.equals(WCK_KEY.getCode())){
                                //json_log.put("second_join", (!hasConnection(getApplicationContext())?"Нету интернета открытие WebView Game":"Last URL: " + last_url));
                                Log.i(app_check, "[Second Join] " + (!hasConnection(getApplicationContext())?"Нету интернета открытие WebView Game":"Last URL: " + last_url));
                                run = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        secondUiChanger();
                                    }
                                });
                                break;
                            }
                            if(AFApplication.campaign != null && !AFApplication.campaign.equals("None") || FBDeepLink.campaign != null) {
                                if(a >= 10){
                                    Constants.DOCUMENT = AppsflyerChecker.getDocument();
                                    //json_log.put("type_join", "non-organic");
                                    run = true;
                                    editor.putString(p_timeSplash.getCode(), pastTense/1000.0+"").apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startUIChange();
                                        }
                                    });
                                    break;
                                }
                            }
                            if(a >= 100){
                                run = true;
                                Constants.DOCUMENT = AppsflyerChecker.getDocument();
                                Log.i(app_check, "[DOCUMENT] " + Constants.DOCUMENT);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new InstallData(getApplicationContext(), mPrefs).Process();
                                        Navigation.findNavController(SplashActivity.this, R.id.fragment_container).navigate(R.id.htmlFragment);
                                    }
                                });
                                break;
                            }
                            a++;
                            pastTense += getTimeLoading();
                            Thread.sleep(getTimeLoading());
                        } else {
                            run = true;
                            Log.i("APP_CHECK", "[INTERNET] Интернета нету");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new InstallData(getApplicationContext(), mPrefs).Process();
                                    Navigation.findNavController(SplashActivity.this, R.id.fragment_container).navigate(R.id.htmlFragment);
                                }
                            });
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void startUIChange() {
        Log.i(app_check, "[DOCUMENT] " + Constants.DOCUMENT);
        if (last_url.equals(WCK_KEY.getCode())) {
            try {
                getOrganicData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            secondUiChanger();
        }
    }
    //
    private void secondUiChanger() {
        runOnUiThread(() -> {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            if(hasConnection(getApplicationContext())){
                Bundle bundle = new Bundle();
                bundle.putBoolean("non-organic", true);
                Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.htmlFragment, bundle);
//                try {
//                    json_log.put("type_open", "second_join");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                SharedPreferences.Editor edit = mPrefs.edit();
//                edit.putString("json", json_log.toString()).apply();
                new InstallData(getApplicationContext(), mPrefs).Process();
            } else {
                Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.htmlFragment);

            }

        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> { if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) { decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); } });
    }
    //
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
    //
    @SuppressLint("SetJavaScriptEnabled")
    private void wvSettings() {
        WebSettings webSettings = wov.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        wov.getSettings().setPluginState(WebSettings.PluginState.ON);
        wov.getSettings().setAllowFileAccess(true);

        WV.setParams(wov, mPrefs);
        wov.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = ImageLoader.createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
//                        try {
//                            json_log.put("webview_image", "Unable to create Image File");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, IC);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }
                File file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg");
                mCapturedImageURI = Uri.fromFile(file);
                final Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[]{captureIntent});
                startActivityForResult(chooserIntent, FR);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType,
                                        String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != IC || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FR || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FR) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }
}