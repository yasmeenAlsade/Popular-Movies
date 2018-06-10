package com.example.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by yalsaadi on 5/29/2018.
 */

public class FavouriteContentProvider extends ContentProvider {

    private FavoriteMovieDBHelper favoriteMovieDBHelper;

    public static final int NAMES = 100;
    public static final int NAMES_WITH_ID = 101;
    public static final int DETAILS_WITH_ID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVORITE, NAMES);
        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVORITE + "/#", NAMES_WITH_ID);
        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVORITE + "/#", DETAILS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoriteMovieDBHelper = new FavoriteMovieDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor = null;

        switch (match){

            case NAMES:
                retCursor = db.query(FavouriteContract.FavouriteEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match){
            case NAMES:
                long id = db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    //success
                    returnUri = ContentUris.withAppendedId(FavouriteContract.FavouriteEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("Faild to insert row into " + uri);
                }
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        int tasksDeleted = 0;

        switch (match){

            case NAMES:
                tasksDeleted = db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME,s, strings);
        }

        if(tasksDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
