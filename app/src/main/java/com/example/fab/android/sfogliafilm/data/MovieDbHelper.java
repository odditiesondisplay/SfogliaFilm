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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract.MovieEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4; // If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "sfogliafilm.db";
    private static final String[] PROJECTION_LISTABLE_MOVIES = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_TAGLINE,
            MovieEntry.COLUMN_RUNTIME,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_SPOKEN_LANGUAGES,
            MovieEntry.COLUMN_DIRECTORS,
            MovieEntry.COLUMN_ACTORS,
    };

    private static final Map<String,Integer> projectionIndex ;
    static {
        Map <String,Integer> sand=new HashMap<String,Integer>(10);
        sand.put(MovieEntry._ID,0);
        sand.put(MovieEntry.COLUMN_MOVIE_ID,1);
        sand.put(MovieEntry.COLUMN_TITLE,2);
        sand.put(MovieEntry.COLUMN_TAGLINE,3);
        sand.put(MovieEntry.COLUMN_RUNTIME,4);
        sand.put(MovieEntry.COLUMN_RELEASE_DATE,5);
        sand.put(MovieEntry.COLUMN_POSTER_PATH,6);
        sand.put(MovieEntry.COLUMN_OVERVIEW,7);
        sand.put(MovieEntry.COLUMN_SPOKEN_LANGUAGES,8);
        sand.put(MovieEntry.COLUMN_DIRECTORS,9);
        sand.put(MovieEntry.COLUMN_ACTORS,10);
        projectionIndex= Collections.unmodifiableMap(sand);
    };


    public static String[] getProjectionListableMovies() {
        return MovieDbHelper.PROJECTION_LISTABLE_MOVIES;
    }

    public static int getIndexColumnProjection(String columnTitle) {
        int avalue=0;
        if (projectionIndex.containsKey(columnTitle))
            avalue= projectionIndex.get(columnTitle).intValue();
        return avalue;
    }

    public MovieDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_MOVIE_TABLE="CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                 MovieEntry._ID + " INTEGER PRIMARY KEY," +
                 MovieEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                 MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                 MovieEntry.COLUMN_TAGLINE+ " TEXT, "+
                 MovieEntry.COLUMN_RUNTIME+ " TEXT, "+
                 MovieEntry.COLUMN_BACKDROP_PATH+ " TEXT, "+
                 MovieEntry.COLUMN_OVERVIEW+ " TEXT, "+
                 MovieEntry.COLUMN_POSTER_PATH+ " TEXT, "+
                 MovieEntry.COLUMN_RELEASE_DATE+ " TEXT, "+
                 MovieEntry.COLUMN_PROD_COMPANIES+ " TEXT, "+
                 MovieEntry.COLUMN_PROD_COUNTRIES+ " TEXT, "+
                 MovieEntry.COLUMN_GENRES+ " TEXT, "+
                 MovieEntry.COLUMN_HOMEPAGE+ " TEXT, "+
                 MovieEntry.COLUMN_IMDB_ID+ " TEXT, "+
                 MovieEntry.COLUMN_DIRECTORS+ " TEXT, "+
                 MovieEntry.COLUMN_ACTORS+ " TEXT, "+
                 MovieEntry.COLUMN_ORIGINAL_TITLE+ " TEXT, "+
                 MovieEntry.COLUMN_STATUS+ " TEXT, "+
                 MovieEntry.COLUMN_SPOKEN_LANGUAGES+ " TEXT, "+
                 MovieEntry.COLUMN_INSERTTIME+ "  );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //buttiamo via tutto e richreiamo le tabelle della versione giusta
        db.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
