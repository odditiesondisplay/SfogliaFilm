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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import com.example.fab.android.sfogliafilm.sync.SfogliaFilmSyncAdapter;



public class MoviesListActivity extends ActionBarActivity
        implements MoviesListFragment.Callbacks, ImageWorkerFragmentCallbacks {



    private final String LOG_TAG=MoviesListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private HeadlessDetailFragment mHeadlessF;
    private VideoDetailFragment vdFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(LOG_TAG,"Lifecycle onCreate");
        setContentView(R.layout.activity_main_movies_list);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        if (findViewById(R.id.video_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            vdFragment =(VideoDetailFragment) getSupportFragmentManager().findFragmentById(R.id.video_detail_container);

            HeadlessDetailFragment hlFragment=null;
            boolean noWorker=false;
            try {
                hlFragment = (HeadlessDetailFragment) getSupportFragmentManager().findFragmentByTag(VideoDetailActivity.WORK_FRAGMENT);
                if (hlFragment==null) {
                    noWorker=true;
                }
            } catch (ClassCastException e) {
                //null
                noWorker=true;
            }
            if (noWorker) {
                hlFragment = HeadlessDetailFragment.getInstance();
                getSupportFragmentManager().beginTransaction().add(hlFragment,VideoDetailActivity.WORK_FRAGMENT).commit();
            }
            mHeadlessF=hlFragment;

        }
        SfogliaFilmSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTwoPane){
            getMenuInflater().inflate(R.menu.menu_share, menu);
            MenuItem mItemS=menu.findItem(R.id.menu_movie_savethedate);
            mItemS.setVisible(false);
        }
       return false;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ((mTwoPane) &&(vdFragment!=null)) {
            menu.findItem(R.id.menu_movie_savethedate).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, MoviesListActivity.class));
                return true;

            case  R.id.menu_movie_savethedate:
                saveThisDate();
                return true;

            default:
               //Nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveThisDate(){
        //Log.d(LOG_TAG, "pinThisDate ShareAction Provider è nullo?");
        startActivity(Utility.createSaveTheDateIntent(vdFragment));
    }


    /**
     * Callback method from {@link MoviesListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        //Log.d(LOG_TAG,"Callback handler FOR "+id);
        if (mTwoPane) {
            //Log.d(LOG_TAG,"Callback handler TWOPANE "+id);
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(com.example.fab.android.sfogliafilm.VideoDetailFragment.ARG_ITEM_ID,
                        Long.parseLong(id));
            vdFragment= new com.example.fab.android.sfogliafilm.VideoDetailFragment();
            vdFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_detail_container, vdFragment)
                    .commit();
           invalidateOptionsMenu(); //to reload menu
        } else {
            //Log.d(LOG_TAG,"Callback handler SINGLE "+id);
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, com.example.fab.android.sfogliafilm.VideoDetailActivity.class);
            detailIntent.putExtra(com.example.fab.android.sfogliafilm.VideoDetailFragment.ARG_ITEM_ID,Long.parseLong(id));
            startActivity(detailIntent);
        }
    }


    @Override
    public void onStart() {
       super.onStart();
       //Log.d(LOG_TAG + ":onStart", "Here! ");
       if (mTwoPane) {
           invalidateOptionsMenu(); //to reload menu
           //Log.d(LOG_TAG,"Lifecycle onStart");
       }

    }

    @Override
    public void asyncUpdateThis(String tmdbID) {
        if (mHeadlessF==null) {
            Log.e(LOG_TAG,"asyncUpdateThis: Headless fragment is null!");
            return;
        }
        mHeadlessF.reloadThisMovie(tmdbID);
    }

    @Override
    public void fillBitmapCache(String imagename, String tmdbMovieID) {
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








}
