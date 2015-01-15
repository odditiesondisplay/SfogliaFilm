/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Fabrizio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.fab.android.sfogliafilm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class MovieContentProvider extends ContentProvider {

    private final String LOG_TAG=MovieContentProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //UriMatcher ids
    private static final int MOVIES=200;
    private static final int MOVIE_ID=202;
    private static final int MOVIE_CHANGE_ID=203;
    private MovieDbHelper mdbHelper;

    public static String selectionThatMovie= SfogliaFilmContract.MovieEntry.TABLE_NAME+
            "."+ SfogliaFilmContract.MovieEntry._ID+ " = ? ";


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                //Log.d(LOG_TAG, "query.MOVIE");
                retCursor = mdbHelper.getReadableDatabase().query(
                        SfogliaFilmContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_ID:
                //Log.d(LOG_TAG,"query.MOVIE_ID");
                retCursor = mdbHelper.getReadableDatabase().query(
                        SfogliaFilmContract.MovieEntry.TABLE_NAME,
                        projection,
                        SfogliaFilmContract.MovieEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_CHANGE_ID:
                //Log.d(LOG_TAG,"query.MOVIE_CHANGE_ID");
                retCursor = mdbHelper.getReadableDatabase().query(
                        SfogliaFilmContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                //Log.d(LOG_TAG,"query.UnsupportedOperation");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri); //notifyChange
        return retCursor;
    }



    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        //Log.d(LOG_TAG, "getType uri:"+uri);
        switch (match) {
            case MOVIES:
                return SfogliaFilmContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return SfogliaFilmContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        SfogliaFilmContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //db.close();
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri=uri;
        //Log.d(LOG_TAG, "insert into "+uri);
        switch (match) {
            case MOVIE_CHANGE_ID: {
                //long _id = db.insert(SfogliaFilmContract.MovieEntry.TABLE_NAME, null, values);
                long _id = db.insertWithOnConflict(SfogliaFilmContract.MovieEntry.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    //returnUri = SfogliaFilmContract.MovieEntry.buildMovieUri(_id);
                    returnUri = SfogliaFilmContract.MovieEntry.buildMovieChangedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //db.close();
        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIE_CHANGE_ID:
                rowsUpdated = db.update(SfogliaFilmContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //db.close();
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //long _id = db.insert(SfogliaFilmContract.MovieEntry.TABLE_NAME, null, value);
                        long _id = db.insertWithOnConflict(SfogliaFilmContract.MovieEntry.TABLE_NAME,
                                null,value,SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    private static UriMatcher buildUriMatcher() {
        // URI. It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = SfogliaFilmContract.CONTENT_AUTHORITY;
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, SfogliaFilmContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, SfogliaFilmContract.PATH_MOVIE + "/#", MOVIE_ID);
        matcher.addURI(authority, SfogliaFilmContract.PATH_MOVIE + "/SET/#", MOVIE_CHANGE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mdbHelper = new MovieDbHelper(getContext(),"hardcoded",null,0);
        return false; //content provider creates succesfully
    }
}
