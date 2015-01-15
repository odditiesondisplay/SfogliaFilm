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
import android.util.Log;
import com.example.fab.android.sfogliafilm.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;


public class JSONApiTmdbSfogliaMovie {
    private static final String LOG_TAG=JSONApiTmdbSfogliaMovie.class.getSimpleName();
    public static final String APIVERSION="3";
    // These are the names of the JSON objects that need to be extracted.
    // Prefix: TMDB_

    public static final String TMDB_MOVIE_ID="id";
    public static final String TMDB_TITLE="title";
    public static final String TMDB_TAGLINE="tagline";
    public static final String TMDB_RUNTIME="runtime";
    public static final String TMDB_BACKDROP_PATH="backdrop_path";
    public static final String TMDB_OVERVIEW="overview";
    public static final String TMDB_POSTER_PATH="poster_path";
    public static final String TMDB_RELEASE_DATE="release_date";
    public static final String TMDB_PROD_COMPANIES="production_companies"; //ARRAY
    public static final String TMDB_PROD_COUNTRIES="production_countries"; //ARRAY
    public static final String TMDB_GENRES="genres"; //ARRAY
    public static final String TMDB_HOMEPAGE="homepage";
    public static final String TMDB_IMDB_ID="imdb_id";
    public static final String TMDB_ORIGINAL_TITLE="original_title";
    public static final String TMDB_STATUS="status";
    public static final String TMDB_SPOKEN_LANGUAGES="spoken_languages"; //ARRAY
    public static final String TMDB_ADULT="adult";

    //subFields to select
    public static final String ISO_COUNTRIES_CODE="iso_3166_1";
    public static final String ISO_LANG_CODE="iso_639_1";
    public static final String MOVIE_API_BASE_URL="https://api.themoviedb.org/3/movie";
    public static final String TMDB_API_URL="http://api.themoviedb.org/3";
    public static final String API_UPCOMING="upcoming";
    public static String TMDBImageURL;
    public static String pSmallPosterWidth;
    public static String pLargePosterWidth;


    public static void getGeneralConfigurationFromJson(String configuratioJsonStr)throws JSONException {
        JSONObject confJson = new JSONObject(configuratioJsonStr);
        JSONObject confImagesJson=confJson.getJSONObject("images");
        TMDBImageURL=confImagesJson.getString("base_url");
        JSONArray pSizes = confImagesJson.getJSONArray("poster_sizes");
        pLargePosterWidth= pSizes.getString(4);
        pSmallPosterWidth= pSizes.getString(0);
        //Log.d("JSON configuration","base "+ TMDBImageURL);
        //Log.d("JSON configuration","poster large "+pLargePosterWidth);
        //Log.d("JSON configuration","poster small "+pSmallPosterWidth);
    }

    public static void getMovieFromJson(String oneMovieJsonStr,ContentValues amovie)
            throws JSONException {
        JSONObject theFullMovieJson = new JSONObject(oneMovieJsonStr);
        String nsfw= theFullMovieJson.getString(TMDB_ADULT);
        if (nsfw == "true")       return;

        String value= theFullMovieJson.getString(TMDB_BACKDROP_PATH);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_BACKDROP_PATH,value);
        value= theFullMovieJson.getString(TMDB_HOMEPAGE);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_HOMEPAGE,value);
        value= theFullMovieJson.getString(TMDB_IMDB_ID);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID,value);
        value= theFullMovieJson.getString(TMDB_ORIGINAL_TITLE);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_ORIGINAL_TITLE,value);
        value= theFullMovieJson.getString(TMDB_OVERVIEW);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_OVERVIEW,value);
        value= theFullMovieJson.getString(TMDB_POSTER_PATH);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_POSTER_PATH,value);
        value= theFullMovieJson.getString(TMDB_RELEASE_DATE);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_RELEASE_DATE,value);
        value= theFullMovieJson.getString(TMDB_RUNTIME);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_RUNTIME,value);
        value= theFullMovieJson.getString(TMDB_TAGLINE);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_TAGLINE,value);
        value= theFullMovieJson.getString(TMDB_TITLE);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_TITLE,value);
        Long movieID= theFullMovieJson.getLong(TMDB_MOVIE_ID);
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID,movieID);
        amovie.put(SfogliaFilmContract.MovieEntry._ID,movieID);

        JSONArray prodCompaniesA= theFullMovieJson.getJSONArray(TMDB_PROD_COMPANIES);
        value="";
        for (int i = 0; i < prodCompaniesA.length(); i++) {
            JSONObject jo=prodCompaniesA.getJSONObject(i);
            value+=" "+jo.getString("name");
        }
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COMPANIES,value);

        JSONArray prodCountriesA= theFullMovieJson.getJSONArray(TMDB_PROD_COUNTRIES);
        value="";
        for(int i=0; i< prodCountriesA.length(); i++) {
            JSONObject  jo=prodCountriesA.getJSONObject(i);
            value+=" "+jo.getString(ISO_COUNTRIES_CODE);
        }
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_PROD_COUNTRIES,value);

        JSONArray spokenLanguagesA= theFullMovieJson.getJSONArray(TMDB_SPOKEN_LANGUAGES);
        value="";
        for(int i = 0; i < spokenLanguagesA.length(); i++) {
            JSONObject jo= spokenLanguagesA.getJSONObject(i);
            value+=" "+ jo.getString(ISO_LANG_CODE);
        }
        amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_SPOKEN_LANGUAGES,value);
    }


    public static void getUpcomingMoviesVectorFromJson(String upcomingJsonStr,Vector<ContentValues> filling)
            throws JSONException {
        ContentValues amovie;
        JSONObject upcomings= new JSONObject(upcomingJsonStr);
        Integer paging_page=upcomings.getInt("page");
        String paging_page_min=upcomings.getJSONObject("dates").getString("minimum");
        String paging_page_max=upcomings.getJSONObject("dates").getString("maximum");
        Integer paging_total_pages=upcomings.getInt("total_pages");
        Integer paging_total_results=upcomings.getInt("total_results"); // org.json.JSONException: No value for totale_results
        JSONArray resultList=upcomings.getJSONArray("results");
        //Log.d(LOG_TAG,"JSON: "+resultList.length());
        Calendar cal = Calendar.getInstance();
        String justInTime=SfogliaFilmContract.getDbDateTimeString(cal.getTime());
        for (int i = 0; i < resultList.length(); i++) {
            JSONObject jo=resultList.getJSONObject(i);
            String nsfw = jo.getString(TMDB_ADULT);
            if ( nsfw.equals("true")) continue;
            amovie = new ContentValues();
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_BACKDROP_PATH,jo.getString(TMDB_BACKDROP_PATH));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID,jo.getLong(TMDB_MOVIE_ID));
            amovie.put(SfogliaFilmContract.MovieEntry._ID,jo.getLong(TMDB_MOVIE_ID));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_ORIGINAL_TITLE,jo.getString(TMDB_ORIGINAL_TITLE));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_RELEASE_DATE,SfogliaFilmContract.getValidDateString(jo.getString(TMDB_RELEASE_DATE)));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_POSTER_PATH,jo.getString(TMDB_POSTER_PATH));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_TITLE,jo.getString(TMDB_TITLE));
            amovie.put(SfogliaFilmContract.MovieEntry.COLUMN_INSERTTIME,justInTime);
            filling.add(amovie);
        }
    }

    public static String fetchJsonResponse(String stringUrl){
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = "";
        String logTag="fetchJsonResponse";
        try {
            url = new URL(stringUrl);
            Log.v(logTag, "GET / " + url.toString());
            //Connection request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                //pretty for DEBUG
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty
                return "";
            } else {
                Log.v(logTag, "GET / " + buffer.length() + " Chars!");
                jsonResponse = buffer.toString();
                //Log.d(logTag, "CONTENT \n" + jsonResponse + " ");
            }
        } catch (IOException e) {
            Log.e(logTag, "Error ", e);
            return "";
        }finally {
            if (urlConnection != null) { //CLOSE connection
                urlConnection.disconnect();
            }
            if (reader != null) { //CLOSE stream
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(logTag, "Error closing stream", e);
                }
            }
        }
        return jsonResponse;
    }

    public static String getDirectorsFromJson(String jsonMovieCredits) throws JSONException {
        String theList="";
        JSONObject credits= new JSONObject(jsonMovieCredits);
        JSONArray allCrew=credits.getJSONArray("crew");
        String[] dirs= new String[allCrew.length()];
        int d=0;
        for (int i = 0; i < allCrew.length(); i++) {
            JSONObject jo=allCrew.getJSONObject(i);
            if (jo.getString("job").equals("Director")){
                dirs[d]=jo.getString("name");
                //Log.d("JSONX", "Director "+dirs[d]);
                d++;
            }
            //Log.d("JSONX", "NO-director "+jo.getString("job"));
        }
        theList=Utility.strJoin(dirs,", ");
        //Log.d("JSONX", " List  "+theList);
        return theList;
    }

    public static String getCastCrewFromJson(String jsonMovieCredits) throws JSONException {
        String theList="";
        JSONObject credits= new JSONObject(jsonMovieCredits);
        JSONArray allCrew=credits.getJSONArray("cast");
        String[] acts= new String[allCrew.length()];
        for (int i = 0; i < allCrew.length(); i++) {
            JSONObject jo=allCrew.getJSONObject(i);
            acts[i]=jo.getString("name");
        }
        theList=Utility.strJoin(acts,", ");
        return theList;
    }


}
