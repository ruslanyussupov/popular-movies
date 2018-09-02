package com.example.ruslanyussupov.popularmovies.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class DbUtils {

    private static final String LOG_TAG = DbUtils.class.getSimpleName();

    private static final String POSTER_FILE_PREFIX = "poster-";
    private static final String BACKDROP_FILE_PREFIX = "backdrop-";

    // Insert movie into DB and save images in external storage
    public static Uri addMovieToFavourite(final Context context, final Movie movie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        Uri dbRowUri = context.getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (dbRowUri != null) {

            Picasso.get().load(NetworkUtils.buildMoviePosterUrlPath(movie.getPosterPath()))
                    .into(new DbTarget(context,
                            POSTER_FILE_PREFIX + movie.getId(),
                            MovieContract.MovieEntry.COLUMN_POSTER_LOCAL_PATH,
                            dbRowUri));

            Picasso.get().load(NetworkUtils.buildMovieBackdropUrlPath(movie.getBackdropPath()))
                    .into(new DbTarget(context,
                            BACKDROP_FILE_PREFIX + movie.getId(),
                            MovieContract.MovieEntry.COLUMN_BACKDROP_LOCAL_PATH,
                            dbRowUri));

        }

        return dbRowUri;

    }

    // Read movies from a Cursor and returns ArrayList
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
            String posterLocalPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_LOCAL_PATH));
            String backdropLocalPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_LOCAL_PATH));
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            String backdropPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));

            Movie movie = new Movie(movieId,
                    title,
                    posterPath,
                    overview,
                    voteAverage,
                    releaseDate,
                    backdropPath,
                    posterLocalPath,
                    backdropLocalPath);

            movies.add(movie);

        } while (cursor.moveToNext());

        cursor.close();

        return movies;

    }

    // Get movie from DB by the movie id
    public static Cursor getMovieFromDb(Context context, int movieId) {

        return context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry.COLUMN_POSTER_LOCAL_PATH,
                            MovieContract.MovieEntry.COLUMN_BACKDROP_LOCAL_PATH},
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(movieId)},
                    null);

    }

    // Delete movie from DB and related images in external storage
    public static int deleteMovieFromFavourite(Context context, Movie movie) {

        StorageUtils.deleteFile(movie.getPosterLocalPath());
        StorageUtils.deleteFile(movie.getBackdropLocalPath());

        return context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movie.getId())});

    }

    // Save image in external storage and insert path into DB
    private static class DbTarget implements Target {

        private final Context context;
        private final String cvKey;
        private final String name;
        private final Uri uri;

        DbTarget(Context context, String name, String cvKey, Uri uri) {
            this.context = context;
            this.cvKey = cvKey;
            this.name = name;
            this.uri = uri;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ContentValues bitmapCv = new ContentValues();
            String localPath = StorageUtils.saveBitmap(context, bitmap, name);
            bitmapCv.put(cvKey, localPath);
            context.getContentResolver().update(uri, bitmapCv, null, null);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }



}
