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

package com.example.fab.android.sfogliafilm;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import org.json.JSONException;
import java.io.BufferedReader;
import java.net.HttpURLConnection;

public class FetchMovieDetailTask extends AsyncTask<String,Void,Void> {
    private final String LOG_TAG= FetchMovieDetailTask.class.getSimpleName();
    private final Context mContext;
    Uri.Builder movieBase=null;
    private final String MOVIE_API_BASE_URL="http://api.themoviedb.org/3/movie";


    public FetchMovieDetailTask(Context context){
        mContext=context;
    }

    private void setupGeneralQuery(){
        if (movieBase==null)
            movieBase = Uri.parse(MOVIE_API_BASE_URL).buildUpon();
    }

    private Uri.Builder setupMovieDetailQuery(String movieID){
        setupGeneralQuery();
        Uri.Builder uu=movieBase.appendPath(movieID);
        uu.appendQueryParameter("api_key", com.example.fab.android.sfogliafilm.Utility.getDevKey());
        return uu;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length != 1)          return null;
        String movieID=params[0];
        Uri.Builder cloud = setupMovieDetailQuery(movieID);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonMovieDetail = "";

        jsonMovieDetail=JSONApiTmdbSfogliaMovie.fetchJsonResponse(cloud.build().toString());
        //JSON parsing and DB inserts
        try {
            //Log.d(LOG_TAG, "getUpcomingMoviesVectorFromJson");
            ContentValues movieDetails= new ContentValues();
            JSONApiTmdbSfogliaMovie.getMovieFromJson(jsonMovieDetail,movieDetails);
            //Log.d(LOG_TAG, "Content "+movieDetails.size());
            Long newMovie = movieDetails.getAsLong(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID);
            int rowsInvolved = mContext.getContentResolver()
                    .update(SfogliaFilmContract.MovieEntry.buildMovieChangedUri(newMovie), movieDetails,
                            SfogliaFilmContract.MovieEntry._ID + " = ?",
                            new String[]{newMovie.toString()});
            if (rowsInvolved==0) {
                Log.v(LOG_TAG, "Error inserting movies data ");
                return null;
            }
            Log.v(LOG_TAG, "Inserted movies data "+newMovie.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
