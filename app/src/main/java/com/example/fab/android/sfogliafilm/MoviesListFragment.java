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


import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fab.android.sfogliafilm.data.SmallPosterImageCache;
import com.example.fab.android.sfogliafilm.data.MovieDbHelper;
import com.example.fab.android.sfogliafilm.data.MoviesListAdapter;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import com.example.fab.android.sfogliafilm.sync.SfogliaFilmSyncAdapter;



public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG=MoviesListFragment.class.getSimpleName();
    private static final int MOVIESLIST_LOADER = 0; //LOADER id per il fragment nel caso avesse + loaders

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private static SmallPosterImageCache ica;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private MoviesListAdapter mMoviesAdapter;
    private ListView mListView;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created. This
        // fragment only uses one loader, so we don't care about checking the id.

        String sortOrder = SfogliaFilmContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC";
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                SfogliaFilmContract.MovieEntry.CONTENT_URI,
                MovieDbHelper.getProjectionListableMovies(),
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mActivatedPosition);
        }
        if ((data!=null)&&(data.getCount() < 3 )){
            //any new films in DB :-(
            updateUpcomingMovie();
            Toast.makeText(getActivity(), getString(R.string.eula_notice), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMoviesAdapter.swapCursor(null);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Loader lifecycle bound to Activity not to Fragment
        getLoaderManager().initLoader(MOVIESLIST_LOADER, null, this);  //initialize LoaderManager CursorLoader
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (ica==null) {
            //Log.d(LOG_TAG, "CACHE size:" + cacheSize);
            ica = new SmallPosterImageCache(new LruCache<String, Bitmap>(10), getActivity());
        }else {
            //Log.d(LOG_TAG, "REUSE CACHE ");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.d(LOG_TAG + ":onOptionsItemSelected", "I got clicked!!  " + item);
        /*int id = item.getItemId();
        boolean executed;
        switch (id) {

            default:
                executed=false;
        }
        if (executed) return true;*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMoviesAdapter.notifyDataSetChanged();
        //updateUpcomingMovie();
        //SfogliaFilmSyncAdapter.syncImmediately(getActivity());
        //Log.d(LOG_TAG,"Lifecycle onStart");
    }



    @Override
    public void onResume() {
        super.onResume();
        //Log.d(LOG_TAG,"Lifecycle onResume");
        FetchHelperTmdbTask configure = new FetchHelperTmdbTask(getActivity());
        configure.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(LOG_TAG,"Lifecycle onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(LOG_TAG,"Lifecycle onStop");
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMoviesAdapter=new MoviesListAdapter(getActivity());

        mMoviesAdapter.setBitmapCache(ica);
        View rootView = inflater.inflate(R.layout.fragment_main_movies_list, container, false);

        mListView= (ListView) rootView.findViewById(R.id.list_movs_content);
        mListView.setAdapter(mMoviesAdapter);
        mListView.setClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor fantasyCursor = mMoviesAdapter.getCursor();
                String attrToStart = "";
                if ((fantasyCursor != null) && (fantasyCursor.moveToPosition(position))) {
                    attrToStart = fantasyCursor.getString(MovieDbHelper.getIndexColumnProjection(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID));
                    //Log.d(LOG_TAG, "Intenting this: " + attrToStart);
                }
                ((Callbacks)getActivity()).onItemSelected(attrToStart);
                mActivatedPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            // The listview probably hasn't even been populated yet. Actually perform the
            // swapout in onLoadFinished.
            mActivatedPosition = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
        }
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }




    private void deleteAllMovies(){
        //Log.d(LOG_TAG + ":deleteAllMovies", "Here! ");
        FetchHelperTmdbTask eraser= new FetchHelperTmdbTask(getActivity());
        eraser.execute(FetchHelperTmdbTask.ACTION_CLEAR_ALL);
    }

    private void updateUpcomingMovie(){
        //deleteAllMovies(); //Debugging purposes
        //Log.d(LOG_TAG + ":updateUpcomingMovie", "Here! ");
        SfogliaFilmSyncAdapter.syncImmediately(getActivity());
    }




}
