package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertMovieTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = InsertMovieTask.class.getSimpleName();

    private static final String POSTER_FILE_PREFIX = "poster-";
    private static final String BACKDROP_FILE_PREFIX = "backdrop-";

    private Context mContext;
    private int mMovieId;

    public InsertMovieTask(Context context, int movieId) {
        mContext = context;
        mMovieId = movieId;
    }

    @Override
    protected Void doInBackground(String... urls) {

        if (urls == null || urls.length == 0) {
            return null;
        }

        String posterPath = urls[0];
        String backdropPath = urls[1];

        Bitmap posterBitmap = downloadBitmap(posterPath);
        Bitmap backdropBitmap = downloadBitmap(backdropPath);

        String posterName = POSTER_FILE_PREFIX + mMovieId;
        String backdropName = BACKDROP_FILE_PREFIX + mMovieId;

        String posterStoragePath = saveBitmap(mContext, posterBitmap, posterName);
        String backdropStoragePath = saveBitmap(mContext, backdropBitmap, backdropName);

        return null;
    }

    private Bitmap downloadBitmap(String path) {

        Bitmap bitmap = null;

        if (!TextUtils.isEmpty(path)) {

            HttpURLConnection connection = null;
            URL url = NetworkUtils.makeUrl(path);

            if (url == null) {
                return null;
            }

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(connection.getInputStream());

                bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            } catch (IOException e) {
                Log.e(LOG_TAG, "", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        return bitmap;

    }

    private static String saveBitmap(Context context, Bitmap bitmap, String imageName) {

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


}