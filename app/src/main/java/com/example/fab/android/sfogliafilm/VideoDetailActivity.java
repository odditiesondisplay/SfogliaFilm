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

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.fab.android.sfogliafilm.data.LargePosterImageCache;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;


public class VideoDetailActivity extends ActionBarActivity implements ImageWorkerFragmentCallbacks {

    private final String LOG_TAG=VideoDetailActivity.class.getSimpleName();
    private HeadlessFragmentToolsCallbacks mHeadlessF;
    public static final  String WORK_FRAGMENT="headless_fragment";
    private VideoDetailFragment vdFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(LOG_TAG + " :onCreateLoader", "Here!");
        setContentView(R.layout.activity_video_detail);
        // Setup worker Fragment
        HeadlessDetailFragment hlFragment=null;
        boolean noWorker=false;
        try {
            hlFragment = (HeadlessDetailFragment) getSupportFragmentManager().findFragmentByTag(WORK_FRAGMENT);
            if (hlFragment==null) {
                noWorker=true;
            }
        } catch (ClassCastException e) {
            //null
            noWorker=true;
        }
        if (noWorker) {
            hlFragment = HeadlessDetailFragment.getInstance();
            getSupportFragmentManager().beginTransaction().add(hlFragment, WORK_FRAGMENT).commit();
            noWorker=false;
        }
        mHeadlessF=hlFragment;
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(VideoDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(VideoDetailFragment.ARG_ITEM_ID,
                            VideoDetailFragment.FOURoFOURmovieID));
            VideoDetailFragment fragment = new VideoDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_detail_container, fragment)
                    .commit();
            vdFragment=fragment;
        } else {
            vdFragment=(VideoDetailFragment) getSupportFragmentManager().findFragmentById(R.id.video_detail_container);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        //MenuItem mItemS=menu.findItem(R.id.menu_movie_savethedate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.d(LOG_TAG + ":onOptionsItemSelected", "I got clicked!!  " + item);
        int id = item.getItemId();
        boolean executed;
        switch (id) {
            case R.id.menu_movie_savethedate:
                executed=true;
                pinThisDate();
                break;
            default:
                executed=false;
        }
        if (executed) return true;

        return super.onOptionsItemSelected(item);
    }

    private void pinThisDate(){
        //Log.d(LOG_TAG, "pinThisDate ShareAction Provider è nullo?");
        startActivity(Utility.createSaveTheDateIntent(vdFragment));
    }


    public void asyncUpdateThis(String movieStringID) {
        if (mHeadlessF==null) {
            Log.e(LOG_TAG,"asyncUpdateThis: Headless fragment is null!");
            return;
        }
        mHeadlessF.reloadThisMovie(movieStringID);
    }

    public void fillBitmapCache(String imagename, String tmdbMovieID){
        if (mHeadlessF==null) {
            Log.e(LOG_TAG,"fillBitmapCache: Headless fragment is null!");
            return;
        }
        try {
            mHeadlessF.loadImageBitmap(imagename, SfogliaFilmContract.MovieEntry.buildMovieChangedUri(Long.parseLong(tmdbMovieID)));
        } catch (NumberFormatException e){
            //Log.d(LOG_TAG,"Converting this "+tmdbMovieID+":",e);
            return;
        }
    }

    @Override
    public Bitmap getBitmapFromCache(String id) {
        if (mHeadlessF==null) {
            Log.e(LOG_TAG,"getBitmapFromCache: Headless fragment is null!");
            return null;
        }
        return mHeadlessF.getPermanentCache().getBitmapFromMemCache(id);
    }


    public static interface HeadlessFragmentToolsCallbacks {
        public void reloadThisMovie(String tmdbID);
        void loadImageBitmap(String imagename, Uri uriSignal);
        LargePosterImageCache getPermanentCache();
    }
}
