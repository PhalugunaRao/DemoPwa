package com.example.pwaapp;

import static com.example.pwaapp.AppUtilsKt.whiteStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BrowserScreen extends AppCompatActivity {
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = BrowserScreen.class.getSimpleName();
    Toolbar toolbar;
    Bundle bundle;
    String webURL = "";
    String webURLTitle = "";
    String pagefrom = "";
    private WebView webView;
    private WebSettings webSettings;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
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
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whiteStatus(this);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        initViews();
        bundle = getIntent().getExtras();
        webURL = "https://pwa.ekincare.com";


        WebSettings settings = webView.getSettings();
        //settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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
        webView.setWebViewClient(new Client());
        webView.setWebChromeClient(new ChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.loadUrl(webURL);
        webView.getSettings().setBlockNetworkLoads(false);

       // webView.getSettings().setDefaultTextEncodingName("utf-8");


       /* webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
*/
        settings.setAllowFileAccessFromFileURLs(true);
        // Off by default, deprecated for SDK versions >= 30.
        settings.setAllowUniversalAccessFromFileURLs(true);
        // Keeping these off is less critical but still a good idea, especially if your app is not
        // using file:// or content:// URLs.
       /* settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        JavascriptInterface javascriptInterface = new JavascriptInterface(getApplicationContext());
        webView.addJavascriptInterface(javascriptInterface, "Android");
*/
       /* webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url.startsWith("blob:")) {
                    System.out.println("=====myyyy===="+javascriptInterface.getBase64StringFromBlobUrl(url));
                    webView.evaluateJavascript(javascriptInterface.getBase64StringFromBlobUrl(url), null);
                }
            }
        });*/



        /*webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                webView.loadUrl(JavascriptInterface.getBase64StringFromBlobUrl(url),null);
                webView.evaluateJavascript(new JavascriptInterface(this).getBase64StringFromBlobUrl(url), null)

            }
        });*/

        /*webView.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long timestampTime = timestamp.getTime();
                String downloadFileName = Long.toString(timestampTime);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse("https://pwa.ekincare.com/6830eb63-e541-449a-b9b7-7d78d472caf9"));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();





            }});*/
       /* webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Name of your downloadble file goes here, example: Mathematics II ");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });*/
    }

    private void initViews() {
        webView = findViewById(R.id.webview);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public class ChromeClient extends WebChromeClient {
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
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
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + System.currentTimeMillis()
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    public class Client extends WebViewClient {
        // ProgressDialog progressDialog;
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                // Here we can open new activity
                return true;
            } else {
                System.out.println("====url===$url"+url);
                // Stay within this webview and load url
                view.loadUrl(url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
            // in standard case YourActivity.this
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            try {
                // Close progressDialog
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
