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

import android.content.ContentValues;
import android.database.Cursor;

import junit.framework.Assert;

import java.util.Map;
import java.util.Set;


public class FakeDataUtils {

    public static ContentValues createPositionNamesValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(SfogliaFilmContract.MovieEntry._ID, "100");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID, "100");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_TITLE, "title");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_TAGLINE, "tagline");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_RUNTIME, "runtime");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_BACKDROP_PATH, "backdrop_path");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_OVERVIEW, "overview");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_POSTER_PATH, "poster_path");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_RELEASE_DATE, "release_date");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COMPANIES, "production_companies");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COUNTRIES, "production_countries");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_GENRES, "genres");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_HOMEPAGE, "homepage");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_IMDB_ID, "imdb_id");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "original_title");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_STATUS, "status");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_SPOKEN_LANGUAGES, "spoken_languages");

        return testValues;
    }


    public static ContentValues createFilmUNOValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(SfogliaFilmContract.MovieEntry._ID, "131631");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID, "131631");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_TITLE, "The Hunger Games: Mockingjay - Part 1");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_TAGLINE, "Fire burns brighter in the darkness");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_RUNTIME, "123");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_BACKDROP_PATH, "/83nHcz2KcnEpPXY50Ky2VldewJJ.jpg");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_OVERVIEW, "Katniss Everdeen reluctantly becomes the symbol of a mass rebellion against the autocratic Capitol.");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_POSTER_PATH, "/cWERd8rgbw7bCMZlwP207HUXxym.jpg");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_RELEASE_DATE, "2014-11-21");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COMPANIES, "Color Force, Lionsgate");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COUNTRIES, "US");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_GENRES, "Adventure, Science Fiction, Thriller");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_HOMEPAGE, "");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_IMDB_ID, "tt1951265");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The Hunger Games: Mockingjay - Part 1");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_STATUS, "Released");
        testValues.put(SfogliaFilmContract.MovieEntry.COLUMN_SPOKEN_LANGUAGES, "en");

        return testValues;
    }

    public static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        Assert.assertTrue(valueCursor.moveToLast());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            Assert.assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            Assert.assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
