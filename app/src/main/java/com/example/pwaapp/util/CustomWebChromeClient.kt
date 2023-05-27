package com.example.pwaapp.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CustomWebChromeClient(private val activity: Activity) : WebChromeClient() {
    companion object {
        private const val FCR = 1
    }

    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private var mCM: String? = null

    override fun onPermissionRequest(request: PermissionRequest) {
        super.onPermissionRequest(request)
        request.grant(request.resources)
    }

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams,
    ): Boolean {
        if (mUMA != null) {
            mUMA!!.onReceiveValue(null)
        }
        mUMA = filePathCallback
        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent?.resolveActivity(activity.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                takePictureIntent.putExtra("PhotoPath", mCM)
            } catch (ex: IOException) {
                Log.e("TAG", "Image file creation failed", ex)
            }
            if (photoFile != null) {
                mCM = "file:" + photoFile.absolutePath
                val imgUrl: Uri
                imgUrl = if (activity.applicationInfo.targetSdkVersion > Build.VERSION_CODES.M) {
                    val authority = "com.example.pwaapp.fileprovider"
                    FileProvider.getUriForFile(activity, authority, photoFile)
                } else {
                    Uri.fromFile(photoFile)
                }
                System.out.println("====imagurl===+" + imgUrl)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl)
            } else {
                takePictureIntent = null
            }
        }
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "*/*"
        val intentArray: Array<Intent?>
        intentArray = if (takePictureIntent != null) {
            arrayOf(takePictureIntent)
        } else {
            arrayOfNulls(0)
        }
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        activity.startActivityForResult(chooserIntent, FCR)
        return true
    }

    // Create an image file
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_${timeStamp}_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
}
