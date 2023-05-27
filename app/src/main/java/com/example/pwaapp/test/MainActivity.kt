package com.example.pwaapp.test

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pwaapp.R
import com.example.pwaapp.setStatusBarColor
import com.example.pwaapp.whiteStatus

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whiteStatus(this)
        window.setStatusBarColor(resources, R.color.white)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webview)
        webView.apply {
            clearHistory()
            clearFormData()
            clearCache(true)
            clearSslPreferences()
        }
        webView.settings.apply {
            javaScriptEnabled = true
            loadsImagesAutomatically = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            domStorageEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                println("===url===$url")
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        webView.loadUrl("https://pwa.ekincare.com")

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(
                Uri.parse(url.trim()),
            )
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Notify client once download is completed!
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Name of your downloadble file goes here, example: Mathematics II ",
            )
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(
                applicationContext,
                "Downloading File", // To notify the Client that the file is being downloaded
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
