package com.example.ruslanyussupov.popularmovies.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageUtils {

    private static final String LOG_TAG = StorageUtils.class.getSimpleName();

    // Save bitmap as image file in private external storage and return it's path
    public static String saveBitmap(Context context, Bitmap bitmap, String imageName) {

        File imageFile = new File( getPrivateStorageDir(context)
                + File.separator + imageName
                + ".png");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "FileOutputStream close error: " + e);
                }
            }
        }

        Log.d(LOG_TAG, imageFile.getAbsolutePath());

        return imageFile.getAbsolutePath();

    }

    // Return private external storage path
    private static File getPrivateStorageDir(Context context) {

        File imagesDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "fav");

        if (!imagesDir.exists()) {
            if (!imagesDir.mkdirs()) {
                Log.e(LOG_TAG, "Popular movies directory not created");
            }
        }

        Log.d(LOG_TAG, imagesDir.getAbsolutePath());

        return imagesDir;

    }

    // Delete file by it's path
    public static boolean deleteFile(String path) {

        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.delete();

    }

}
