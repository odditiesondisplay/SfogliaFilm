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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.fab.android.sfogliafilm.data.*;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract.MovieEntry;

public class VideoDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String SAVED_KEY = "string_movieid";
    static final long FOURoFOURmovieID=290237L;
    private final String LOG_TAG=VideoDetailFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "movie_entry_id";
    private String mSavedMovieID;
    private static final int MOVIEDETAIL_LOADER=1; //x LoadManager
    private TextView mMproducers;
    private TextView mMtagline;
    private TextView mMLang;
    private TextView mMDate;
    private TextView mMTitle;
    private TextView mMoverview;
    private TextView mDirectors;
    private TextView mActors;
    private ImageView mMposter;
    private ImageWorkerFragmentCallbacks mListener;
    private String mTheTitle="";
    private String mTheDate;

    public String getMovieTitle(){ return mTheTitle;}
    public String getMovieDate(){ return mTheDate;}
    public String getMovieId(){ return mSavedMovieID;}

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VideoDetailFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d(LOG_TAG + " :onActivityCreated", "Here!");
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_ITEM_ID)) {
            //arguments.getLong(ARG_ITEM_ID);
            getLoaderManager().initLoader(MOVIEDETAIL_LOADER, null, this);
            //initialize LoaderManager CursorLoader
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d(LOG_TAG + " :onSaveInstanceState", "Here!");
        outState.putString(SAVED_KEY, mSavedMovieID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(LOG_TAG + " :onCreateView", "Here!");
        Bundle arguments = getArguments();
        if ((arguments != null)&&(getArguments().containsKey(ARG_ITEM_ID))) {
            long kiave=getArguments().getLong(ARG_ITEM_ID);
            mSavedMovieID=Long.toString(kiave);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_KEY)) {
            mSavedMovieID = savedInstanceState.getString(SAVED_KEY);
        }
        View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);
        mMposter= (ImageView) rootView.findViewById(R.id.smallPoster);
        mMTitle= (TextView) rootView.findViewById(R.id.tv_showTitle);
        mMDate= (TextView) rootView.findViewById(R.id.tv_showDate);
        mMLang= (TextView) rootView.findViewById(R.id.tv_languages);
        mMtagline= (TextView) rootView.findViewById(R.id.tv_tagline);
        mMoverview= (TextView) rootView.findViewById(R.id.tv_overview);
        mMproducers= (TextView) rootView.findViewById(R.id.tv_producers);
        mDirectors=(TextView) rootView.findViewById(R.id.tv_directors);
        mActors=(TextView) rootView.findViewById(R.id.tv_actors);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Leggi i dati dal DB
        //Log.d(LOG_TAG + " :onCreateLoader", "Here!");
        String[] argums={mSavedMovieID};
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        Uri uriListen =null;
        try {
             uriListen = MovieEntry.buildMovieChangedUri(Long.parseLong(mSavedMovieID));
        }catch (NumberFormatException e) {
            uriListen =MovieEntry.buildMovieChangedUri(0L);
        }
        return new CursorLoader(
                getActivity(),
                uriListen,
                null,
                MovieContentProvider.selectionThatMovie,
                argums,
                null
        );
    }

    private void displayTextView(String value, String label, TextView subject) {
        if ((value!=null)&&(value.length()>1)) {
            subject.setText(label+" "+value);
            subject.setVisibility(View.VISIBLE);
        }else {
            subject.setText("");
            subject.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Log.d(LOG_TAG," onLoadFinished");
        if ((data==null)||(data.getCount() ==0 )){
            Log.e(LOG_TAG," NODATA found!");
        } else {
            if (data.moveToFirst()) {
                if (data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_PROD_COUNTRIES)) == null){
                    //Log.d(LOG_TAG, " PARTDATA found:" + data.getCount());
                    //Se non ci sono Lancia un FETCH
                    mListener.asyncUpdateThis(mSavedMovieID);
                }
                Context cCurrent=getActivity();
                //Log.d(LOG_TAG, " BIGDATA found:" + data.getCount());
                String temp = "";
                temp = data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_TITLE));
                mTheTitle=temp;
                mMTitle.setText(temp);
                temp = data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                mMDate.setText(temp);
                mTheDate=temp;
                temp = data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_SPOKEN_LANGUAGES));
                displayTextView(temp,cCurrent.getString(R.string.lingua), mMLang);
                temp=data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                displayTextView(temp,cCurrent.getString(R.string.storia), mMoverview);
                temp=data.getString(data.getColumnIndex(MovieEntry.COLUMN_DIRECTORS));
                displayTextView(temp,cCurrent.getString(R.string.regia), mDirectors);
                temp=data.getString(data.getColumnIndex(MovieEntry.COLUMN_ACTORS));
                displayTextView(temp,cCurrent.getString(R.string.attori), mActors);
                temp = data.getString(data.getColumnIndex(MovieEntry.COLUMN_TAGLINE));
                displayTextView(temp,cCurrent.getString(R.string.motto), mMtagline);
                temp = data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_PROD_COMPANIES));
                displayTextView(temp,cCurrent.getString(R.string.produzione), mMproducers);

                temp = data.getString(
                        data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
                Bitmap large=mListener.getBitmapFromCache(temp);
                if (large!=null) mMposter.setImageBitmap(large);
                else {
                    mListener.fillBitmapCache(temp,mSavedMovieID);
                }

            }
            else {
                //Log.d(LOG_TAG," NODATA found:"+data.getCount());
                //Se non ci sono Lancia un FETCH
                mListener.asyncUpdateThis(mSavedMovieID);
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
             mListener = (ImageWorkerFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


}
