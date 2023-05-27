package com.example.pwaapp.util

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class ReusableWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        // Handle onPageFinished event if needed
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        val context = view?.context ?: return
        Toast.makeText(context, "Failed loading app!", Toast.LENGTH_SHORT).show()

    }

    // Add other WebViewClient methods as per your requirements
}
