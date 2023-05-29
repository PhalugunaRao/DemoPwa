package com.example.pwaapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pwaapp.script.JavaScriptInterfaceee
import com.example.pwaapp.util.ReusableChromeClient
import com.example.pwaapp.util.ReusableDownloadListener
import com.example.pwaapp.util.ReusableWebViewClient

class WebViewActivity : AppCompatActivity() {
    lateinit var webView: WebView
    private var webChromeClient2: ReusableChromeClient? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webChromeClient2?.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetJavaScriptEnabled", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whiteStatus(this)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= 23 && (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA,
                ) != PackageManager.PERMISSION_GRANTED
                )
        ) {
            ActivityCompat.requestPermissions(
                this@WebViewActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1,
            )
        }
        webView = findViewById<View>(R.id.webview) as WebView
        webChromeClient2 = ReusableChromeClient(this)
        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            if (Build.VERSION.SDK_INT >= 21) {
                mixedContentMode = 0
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT >= 19) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT < 19) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            loadsImagesAutomatically = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            domStorageEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }

        webView.apply {
            clearHistory()
            clearFormData()
            clearCache(true)
            clearSslPreferences()
            webViewClient = ReusableWebViewClient()
            loadUrl("https://pwa.ekincare.com")
            addJavascriptInterface(JavaScriptInterfaceee(applicationContext), "Android")
            setDownloadListener(ReusableDownloadListener(this@WebViewActivity, webView))
            webChromeClient = webChromeClient2
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
