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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import com.example.fab.android.sfogliafilm.data.SmallPosterImageCache;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class FetchPosterThumbTask
        extends AsyncTask<String,Void,Bitmap> {

    private final static String LOG_TAG= FetchPosterThumbTask.class.getSimpleName();
    private final Context mContext;
    private SmallPosterImageCache iCache;
    private String mImageKey;
    private ImageView mimageView;

    public FetchPosterThumbTask(Context context) {
        mContext = context;
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
        if ((Utility.canFetchImages)&&(params.length ==2)) {
            String thePoster=params[0];
            String dimension=params[1];
            if ((thePoster==null)||(thePoster.equals("null"))) {
                Log.i(LOG_TAG, "Skipping fetch null image");
                return ball;
            }
            String newImage= JSONApiTmdbSfogliaMovie.TMDBImageURL+dimension+thePoster;
            //Log.d(LOG_TAG,"Getting IMAGE "+newImage);
            try {
                URL poster=new URL(newImage);
                HttpURLConnection connection = (HttpURLConnection)poster.openConnection();
                ball= BitmapFactory.decodeStream(connection.getInputStream(), null, null);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "ERROR IMAGE" + e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR READING IMAGE" + e);
            }

        }
        return ball;
    }

    public void setImageViewCache(ImageView imageView, String imageName, SmallPosterImageCache memoryCache) {
        iCache=memoryCache;
        mImageKey=imageName;
        mimageView=imageView;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result!=null){
            iCache.addBitmapToMemoryCache(mImageKey, result);
            mimageView.setImageBitmap(result);
        }

    }
}
