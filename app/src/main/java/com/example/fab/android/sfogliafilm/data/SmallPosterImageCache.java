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
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.example.fab.android.sfogliafilm.FetchPosterThumbTask;
import com.example.fab.android.sfogliafilm.R;
import com.example.fab.android.sfogliafilm.Utility;


public class SmallPosterImageCache {
    private LruCache<String, Bitmap> bitmapCache;
    private Context mContext;
    private final String LOG_TAG=SmallPosterImageCache.class.getSimpleName();

    public SmallPosterImageCache(LruCache<String, Bitmap> bitmapCache, Context context) {
        this.bitmapCache = bitmapCache;
        this.mContext=context;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        //Log.d(LOG_TAG, "GET from CACHE:" + key);
        return bitmapCache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapCache.put(key, bitmap);
            //Log.d(LOG_TAG,"PUT into CACHE:"+key);
        }
    }

    public void loadBitmap(String imageName, ImageView imageView) {
        String imagesize=JSONApiTmdbSfogliaMovie.pSmallPosterWidth;
        if ((imagesize!=null)&&(imageName!=null)) {
            String imageKey = JSONApiTmdbSfogliaMovie.pSmallPosterWidth + imageName;
            Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //imageView.setImageResource(R.drawable.ic_sm);
                imageView.setImageResource(R.drawable.fourofour);
                if (Utility.canFetchImages) {
                    FetchPosterThumbTask listme = new FetchPosterThumbTask(mContext);
                    listme.setImageViewCache(imageView, imageKey, this);
                    listme.execute(imageName, imagesize);
                }
            }
        }
    }

}
