package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yalsaadi on 5/29/2018.
 */

public class FavoriteMovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE = "my_db";
    private static final String TABLE = "favourite";
    private static final String FavouriteMovieName = "name";
    private static final String FavouriteMovieDetails = "details";

    public FavoriteMovieDBHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TABLE
                + " (id integer primary key, "
                + FavouriteMovieName + " text, "
                + FavouriteMovieDetails + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
