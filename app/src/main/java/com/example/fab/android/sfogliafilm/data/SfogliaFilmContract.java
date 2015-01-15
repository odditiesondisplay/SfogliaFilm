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
import android.net.Uri;
import android.provider.BaseColumns;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines table and column names for the SfogliaFilm database.
 */
public class SfogliaFilmContract {
    // name of the content provider
    public static final String CONTENT_AUTHORITY = SfogliaFilmContract.class.getPackage().getName();
    //URI content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = MovieEntry.TABLE_NAME;
    public static final String PATH_MOVIES = MovieEntry.TABLE_NAME+"s";

    // Format used for storing dates in the database. ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Provide an usable data string if uncorrect values are provided.
     * @param maybe the input string (may be null)
     * @return the date provided or "2001-10-02".
     */
    public static String getValidDateString(String maybe){
        String defaultDate="2001-10-02";
        if ((maybe ==null)||(maybe.length()!=10))
            return defaultDate;
        else return maybe;
    }


    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly TEXT representation of the timestamp, format: yyyyMMddHHmmss.
     */
    public static String getDbDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }



    /* Inner class that defines the table contents of the Movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME ="movie";

        public static final String COLUMN_MOVIE_ID="id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_TAGLINE="tagline";
        public static final String COLUMN_RUNTIME="runtime";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_POSTER_PATH="poster_path";
        public static final String COLUMN_BACKDROP_PATH="backdrop_path";
        public static final String COLUMN_OVERVIEW="overview";
        //Just don't care of production companies detailed info
        public static final String COLUMN_PROD_COMPANIES="production_companies";
        public static final String COLUMN_PROD_COUNTRIES="production_countries";
        //Just don't care of genres indexes for now
        public static final String COLUMN_GENRES="genres";
        public static final String COLUMN_HOMEPAGE="homepage";
        public static final String COLUMN_IMDB_ID="imdb_id";
        public static final String COLUMN_ORIGINAL_TITLE="original_title";
        public static final String COLUMN_STATUS="status";
        public static final String COLUMN_SPOKEN_LANGUAGES="spoken_languages";
        public static final String COLUMN_INSERTTIME="timestamp_text";
        //Just don't care of people indexes for now
        public static final String COLUMN_DIRECTORS="directors";
        public static final String COLUMN_ACTORS="actors";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(SfogliaFilmContract.PATH_MOVIES).build();

        public static final Uri CONTENT_ITEM_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(SfogliaFilmContract.PATH_MOVIE).build();

        public static final String CONTENT_TYPE ="vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_ITEM_URI, id);
        }

        public static Uri buildMovieChangedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_ITEM_URI.buildUpon().appendPath("SET").build(), id);
        }
    }

}
