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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.fab.android.sfogliafilm.data.LargePosterImageCache;
import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class FetchHelperTmdbTask
        extends AsyncTask<String,Void,Bitmap> {
    private final String LOG_TAG= FetchHelperTmdbTask.class.getSimpleName();

    private final Context mContext;
    private final Uri mUriSingleMovieSignal;
    private final LargePosterImageCache myCache;
    private boolean iAmImageLoader=false;
    public final static String ACTION_CLEAR_ALL="clear all please";
    private String mImageKey;

    public FetchHelperTmdbTask(Context context){
        mContext=context;
        mUriSingleMovieSignal = null;
        myCache = null;
    }

    public FetchHelperTmdbTask(FragmentActivity activity, Uri uriSignal, LargePosterImageCache permanentCache) {
        mContext=activity;
        mUriSingleMovieSignal=uriSignal;
        myCache=permanentCache;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        //final Bitmap ball=null;
        Bitmap ball=null;
        if ((params.length ==0)&&(!Utility.canFetchImages)) {
            if (Utility.setupImagesConfiguration()) {
                Utility.canFetchImages=true;
                mContext.getContentResolver().notifyChange(SfogliaFilmContract.MovieEntry.CONTENT_URI,null);
            }
        }
        if ((params.length ==1)&&(params[0]==ACTION_CLEAR_ALL)) {
            mContext.getContentResolver().delete(
                    SfogliaFilmContract.MovieEntry.CONTENT_URI,null,null);
            Log.v(LOG_TAG, "DELETED all movie records!");
        }
        if ((Utility.canFetchImages)&&(params.length ==2)) {
            iAmImageLoader=true;
            String thePoster=params[0];
            String dimension=params[1];
            if ((thePoster==null)||(thePoster.equals("null"))) {
                Log.i(LOG_TAG, "Skipping fetch null image");
                return ball;
            }
            mImageKey=thePoster;
            String newImage=JSONApiTmdbSfogliaMovie.TMDBImageURL+dimension+thePoster;
            //Log.d(LOG_TAG,"Getting IMAGE "+newImage);
            try {
                URL poster=new URL(newImage);
                HttpURLConnection connection = (HttpURLConnection)poster.openConnection();
                ball=BitmapFactory.decodeStream(connection.getInputStream(),null,null);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "ERROR IMAGE" + e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR READING IMAGE" + e);
            }

        }
        return ball;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if ((iAmImageLoader)&&(result!=null)) {
            myCache.addBitmapToMemoryCache(mImageKey, result);
            if (mUriSingleMovieSignal != null)
                mContext.getContentResolver().notifyChange(mUriSingleMovieSignal,null);
        }

    }





}
