package com.example.ruslanyussupov.popularmovies.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_POSTER_LOCAL_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_BACKDROP_LOCAL_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT);";

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }



}
