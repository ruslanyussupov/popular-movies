package com.example.ruslanyussupov.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class Utils {

    private final Context appContext;
    private final OkHttpClient okHttpClient;
    private final Request.Builder requestBuilder;

    public Utils(Context appContext, OkHttpClient okHttpClient, Request.Builder requestBuilder) {
        this.appContext = appContext;
        this.okHttpClient = okHttpClient;
        this.requestBuilder = requestBuilder;
    }

    public boolean hasNetworkConnection() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();

        }

        return false;

    }

    // Save bitmap as image file in private external storage and return it's path
    public String saveBitmap(String url, String imageName) {

        Request request = requestBuilder.url(url).build();
        Response response = null;

        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Timber.e(e, "Can't load image: %s", url);
        }

        if (response == null || response.body() == null) {
            return "";
        }

        File imageFile = new File( getPrivateStorageDir()
                + File.separator + imageName
                + ".png");


        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(imageFile);
            BitmapFactory.decodeStream(response.body().byteStream())
                    .compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            Timber.e(e, "File not found");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Timber.e(e, "FileOutputStream close error");
                }
            }
        }

        Timber.d("Bitmap saved: %s", imageFile.getAbsolutePath());

        return imageFile.getAbsolutePath();

    }

    // Return private external storage path
    private File getPrivateStorageDir() {

        File imagesDir = new File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "fav");

        if (!imagesDir.exists()) {
            if (!imagesDir.mkdirs()) {
                Timber.e("Popular movies directory hasn't created");
            }
        }

        Timber.d(imagesDir.getAbsolutePath());

        return imagesDir;

    }

    // Delete file by it's path
    public boolean deleteFile(String path) {

        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.delete();

    }

}
