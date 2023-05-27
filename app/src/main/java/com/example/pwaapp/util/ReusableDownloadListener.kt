package com.example.pwaapp.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Toast
import com.example.pwaapp.script.JavaScriptInterfaceee

class ReusableDownloadListener(private val context: Context,private val webView: WebView) : DownloadListener {
    override fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String,
        contentLength: Long
    ) {
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "Downloading Complete", Toast.LENGTH_SHORT).show()
            }
        }

        if (url.startsWith("blob")) {
            // Handle blob URLs
            webView.loadUrl(JavaScriptInterfaceee.getBase64StringFromBlobUrl(url, mimeType))
        } else {
            // Handle regular download URLs
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.setMimeType(mimeType)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(url, contentDisposition, mimeType)
            )
            manager.enqueue(request)
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }
}
