package com.example.pwaapp;

import static com.example.pwaapp.AppUtilsKt.whiteStatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pwaapp.script.JavaScriptInterfaceee;
import com.example.pwaapp.util.CustomWebChromeClient2;
import com.example.pwaapp.util.ReusableDownloadListener;
import com.example.pwaapp.util.ReusableWebViewClient;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    private CustomWebChromeClient2 webChromeClient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        webChromeClient.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        whiteStatus(this);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        webView = (WebView) findViewById(R.id.webview);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT < 19){
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        }
        WebSettings settings = webView.getSettings();
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);
        webView.clearSslPreferences();

        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new ReusableWebViewClient());
        webView.loadUrl("https://pwa.ekincare.com");
        settings.setAllowFileAccessFromFileURLs(true);
        // Off by default, deprecated for SDK versions >= 30.
        settings.setAllowUniversalAccessFromFileURLs(true);
        // Keeping these off is less critical but still a good idea, especially if your app is not
        // using file:// or content:// URLs.
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        JavaScriptInterfaceee javascriptInterface = new JavaScriptInterfaceee(getApplicationContext());
        webView.addJavascriptInterface(javascriptInterface, "Android");
        webView.setDownloadListener(new ReusableDownloadListener(this,webView));

        webChromeClient = new CustomWebChromeClient2(this);
        webView.setWebChromeClient(webChromeClient);

    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(webView.canGoBack()){
                        webView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}