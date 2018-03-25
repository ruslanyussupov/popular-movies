package com.example.ruslanyussupov.popularmovies.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;

    private MovieDbHelper mMovieDbHelper;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_ID:
                long id = ContentUris.parseId(uri);
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        long rowId;
        Uri retUri = null;

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (rowId > 0) {
                    retUri = MovieContract.MovieEntry.buildMovieUri(rowId);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case MOVIE_ID:
                long id = ContentUris.parseId(uri);
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            case MOVIE_ID:
                long id = ContentUris.parseId(uri);
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#", MOVIE_ID);

        return uriMatcher;

    }

}
