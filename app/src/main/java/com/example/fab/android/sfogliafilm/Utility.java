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


import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    private static final String LOG_TAG=Utility.class.getSimpleName();
    protected static String getDevKey(){
        return "aa1e7261d9475e8fdd21de02ee2f334b";
    }
    public static boolean canFetchImages=false;

    static String setupTMDBConfigurationURL(){
        Uri.Builder bulderBase = Uri.parse(JSONApiTmdbSfogliaMovie.TMDB_API_URL).buildUpon();
        return bulderBase.appendPath("configuration").
                appendQueryParameter("api_key", getDevKey()).
                build().toString();
    }

    static boolean setupImagesConfiguration() {
        String configurationURL = setupTMDBConfigurationURL();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonConfiguration = "";
        try {
            URL url = new URL(configurationURL);
            //Log.d(Utility.LOG_TAG, "GET / " + configurationURL);
            //Connection request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return false;
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
                return false;
            } else {
                //Log.d(Utility.LOG_TAG, "GET / " + buffer.length() + " Chars!");
                jsonConfiguration = buffer.toString();
                //Log.d(Utility.LOG_TAG, "CONTENT \n" + jsonConfiguration + " ");
            }
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG,  e.getMessage(), e);
            return false;
        } finally {
            if (urlConnection != null) { //CLOSE connection
                urlConnection.disconnect();
            }
            if (reader != null) { //CLOSE stream
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(Utility.LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            //Log.d(Utility.LOG_TAG, "getGeneralConfigurationFromJson");
            JSONApiTmdbSfogliaMovie.getGeneralConfigurationFromJson(jsonConfiguration);
        } catch (JSONException e) {
            //error parsing
            Log.e(Utility.LOG_TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }

    static Intent createSaveTheDateIntent(VideoDetailFragment currentVDF) {
        Intent saveTDIntent = new Intent(Intent.ACTION_INSERT);
        saveTDIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); //ritorna nella tua applicazione invece che in quella chiamata
        saveTDIntent.setType("vnd.android.cursor.item/event");
        String title="";
        if (currentVDF!=null) title=currentVDF.getMovieTitle();
        String date="";
        if (currentVDF!=null) date=currentVDF.getMovieDate();
        String tmdbID="";
        if (currentVDF!=null) tmdbID=currentVDF.getMovieId();
        saveTDIntent.putExtra(CalendarContract.Events.TITLE, "Upcoming movie: " + title);
        saveTDIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "on screen");
        saveTDIntent.putExtra(CalendarContract.Events.DESCRIPTION, title + " https://www.themoviedb.org/search?query=" + title.replace(' ', '+'));
        Date alDate ;
        try {
            alDate =new SimpleDateFormat(SfogliaFilmContract.DATE_FORMAT).parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            alDate=new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(alDate);
        saveTDIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        saveTDIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                cal.getTimeInMillis());
        saveTDIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                cal.getTimeInMillis());
        saveTDIntent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        saveTDIntent.putExtra(CalendarContract.Events.HAS_ALARM, 1);

        return saveTDIntent;
    }

    public static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        int c=0;
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (null!= aArr[i]) {
                if (c > 0) {
                    sbStr.append(sSep);
                }
                sbStr.append(aArr[i]);
                c++;
            }
        }
        return sbStr.toString();
    }
}

