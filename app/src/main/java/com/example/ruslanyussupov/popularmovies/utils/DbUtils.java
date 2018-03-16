package com.example.ruslanyussupov.popularmovies.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DbUtils {

    private static final String LOG_TAG = DbUtils.class.getSimpleName();

    private static final String POSTER_FILE_PREFIX = "poster-";
    private static final String BACKDROP_FILE_PREFIX = "backdrop-";

    public static Uri insertMovieIntoDb(final Context context, final Movie movie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        final Uri dbRowUri = context.getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        Picasso.with(context).load(NetworkUtils.buildMoviePosterUrlPath(movie.getPosterPath())).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ContentValues bitmapCv = new ContentValues();
                String posterName = POSTER_FILE_PREFIX + movie.getId();
                String posterPath = saveBitmap(context, bitmap, posterName);
                bitmapCv.put(MovieContract.MovieEntry.COLUMN_POSTER, posterPath);
                context.getContentResolver().update(dbRowUri, bitmapCv, null, null);
                Log.d(LOG_TAG, "Poster put into cv");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        Picasso.with(context).load(NetworkUtils.buildMovieBackdropUrlPath(movie.getBackdropPath())).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ContentValues bitmapCv = new ContentValues();
                String backdropName = BACKDROP_FILE_PREFIX + movie.getId();
                String backdropPath = saveBitmap(context, bitmap, backdropName);
                bitmapCv.put(MovieContract.MovieEntry.COLUMN_BACKDROP, backdropPath);
                context.getContentResolver().update(dbRowUri, bitmapCv, null, null);
                Log.d(LOG_TAG, "Backdrop put into cv");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        return dbRowUri;

    }

    public static ArrayList<Movie> getMoviesFromCursor(Cursor cursor) {

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<>(cursor.getCount());

        cursor.moveToFirst();

        do {

            int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            double voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String backdropPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));

            Movie movie = new Movie(movieId,
                    title,
                    overview,
                    voteAverage,
                    releaseDate);

            movie.setPosterDbPath(posterPath);
            movie.setBackdropDbPath(backdropPath);

            movies.add(movie);

        } while (cursor.moveToNext());

        return movies;

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
