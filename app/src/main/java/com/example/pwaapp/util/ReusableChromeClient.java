package com.example.pwaapp.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReusableChromeClient extends WebChromeClient {
        private Activity mActivity;
        private ValueCallback<Uri[]> mUploadMessage;
        private String mCameraPhotoPath;
    private static final int FCR = 1;

        public ReusableChromeClient(Activity activity) {
            mActivity = activity;
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }

            mUploadMessage = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    Log.e("MyWebChromeClient", "Error creating image file: " + ex.getMessage());
                }

                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    Uri imgUrl;
                    if (mActivity.getApplicationInfo().targetSdkVersion > Build.VERSION_CODES.M) {
                        String authority = "com.example.pwaapp.fileprovider";
                        imgUrl = FileProvider.getUriForFile(mActivity, authority, photoFile);
                    } else {
                        imgUrl = Uri.fromFile(photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl);
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
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            mActivity.startActivityForResult(chooserIntent, FCR);

            return true;
        }

        private File createImageFile() throws IOException {
            @SuppressLint("SimpleDateFormat")
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";
            File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == FCR) {
                if (null == mUploadMessage) {
                    return;
                }

                Uri[] results = null;

                if (resultCode == Activity.RESULT_OK) {
                    if (data == null || data.getData() == null) {
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

                mUploadMessage.onReceiveValue(results);
                mUploadMessage = null;
            }
        }
    }

