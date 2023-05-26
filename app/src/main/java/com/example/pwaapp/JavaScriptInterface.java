/*
package com.example.pwaapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class JavaScriptInterface {
    private Context context;
    private NotificationManager nm;
    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data) throws IOException {
        convertBase64StringToPdfAndStoreIt(base64Data);
    }
    public static String getBase64StringFromBlobUrl(String blobUrl){
        if(blobUrl.startsWith("blob")){

            return "javascript: var xhr=new XMLHttpRequest();" +
                    "xhr.open('GET', '"+blobUrl+"', true);" +
                    "xhr.setRequestHeader('Content-type','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8');" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobPdf = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsDataURL(blobPdf);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            Android.getBase64FromBlobData(base64data);" +
                    "        }" +
                    "    }" +
                    "};" +
                    "xhr.send();";


        }
        return "javascript: console.log('It is not a Blob URL');";
    }
    private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {

        Log.e("base64PDf",base64PDf);
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        Calendar calendar=Calendar.getInstance();
        ;
        final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/Report" +   calendar.getTimeInMillis() + "_.xlsx");
        byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,", ""), 0);
        Log.e("bytearrya",""+pdfAsBytes);
        FileOutputStream os;
        os = new FileOutputStream(dwldsPath, false);
        os.write(pdfAsBytes);
        os.flush();
        os.close();

        if(dwldsPath.exists()) {
           // sendNotification();


            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/Report" +
                    calendar.getTimeInMillis() + "_.xlsx");
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            String path=   dir.getAbsolutePath();

            Uri uri;
            if (Build.VERSION.SDK_INT < 24) {
                uri = Uri.fromFile(dir);
            } else {
                File file = new File(path);
                uri = FileProvider.getUriForFile((MainActivity)this.context,
                        this.context.getApplicationContext().getPackageName() + ".provider", file);
//                    uri = Uri.parse("file://" + dir);
            }
            sendIntent.setDataAndType(uri, "application/vnd.ms-excel");
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try{
                context.startActivity(sendIntent);

            }catch (Exception e){
                Toast.makeText(context, "Np app found to view file", Toast.LENGTH_SHORT).show();
            }

        }

    }
}*/
