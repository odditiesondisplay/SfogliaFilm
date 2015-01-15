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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.fab.android.sfogliafilm.data.FakeDataUtils;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;


public class TestMCProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestMCProvider.class.getSimpleName();

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        Log.d(LOG_TAG, "inizio setUp()");
        deleteAllRecords();
        Log.d(LOG_TAG, "fine setUp()");
    }

    private void deleteAllRecords() {
        Log.d(LOG_TAG, "using deleteAllRecords()");
        mContext.getContentResolver().delete(
                SfogliaFilmContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                SfogliaFilmContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testInsertReadProvider() {
        Log.d(LOG_TAG, "Starting testReadProvider()");
        ContentValues testValues = FakeDataUtils.createPositionNamesValues();

        Uri locationUri = mContext.getContentResolver().insert(SfogliaFilmContract.MovieEntry.CONTENT_URI, testValues);
        long locationRowId= ContentUris.parseId(locationUri);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
        // the round trip.
        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                SfogliaFilmContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        FakeDataUtils.validateCursor(cursor, testValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                SfogliaFilmContract.MovieEntry.buildMovieUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        FakeDataUtils.validateCursor(cursor, testValues);
        cursor.close();
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }


    public void testUpdateLocation() {
        Log.d(LOG_TAG, "Starting testUpdateLocation()");
        // Create a new map of values, where column names are the keys
        ContentValues values = FakeDataUtils.createPositionNamesValues();
        Uri locationUri = mContext.getContentResolver().
                insert(SfogliaFilmContract.MovieEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(SfogliaFilmContract.MovieEntry._ID, locationRowId);
        updatedValues.put(SfogliaFilmContract.MovieEntry.COLUMN_TITLE, "Santa's Village");
        int count = mContext.getContentResolver().update(
                SfogliaFilmContract.MovieEntry.CONTENT_URI, updatedValues, SfogliaFilmContract.MovieEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);
        Log.d(LOG_TAG, "testUpdateLocation:: "+SfogliaFilmContract.MovieEntry.buildMovieUri(locationRowId));
        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                SfogliaFilmContract.MovieEntry.buildMovieUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        FakeDataUtils.validateCursor(cursor, updatedValues);
    }



    public void testGetType() {
        Log.d(LOG_TAG, "Starting testGetType()");
        // content://com.example.fab.android.sfogliafilmdata.data/movie
        String type = mContext.getContentResolver().getType(SfogliaFilmContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(SfogliaFilmContract.MovieEntry.CONTENT_TYPE, type);
        Log.d(LOG_TAG, "testGetType:: "+type+" >> "+SfogliaFilmContract.MovieEntry.CONTENT_URI);
        // content://com.example.fab.android.sfogliafilmdata.data/movie/1
        type = mContext.getContentResolver().getType(SfogliaFilmContract.MovieEntry.buildMovieUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(SfogliaFilmContract.MovieEntry.CONTENT_ITEM_TYPE, type);
        Log.d(LOG_TAG, "testGetType:: "+type+" >> "+SfogliaFilmContract.MovieEntry.buildMovieUri(1L));
    }

}
