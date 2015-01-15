
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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.LargePosterImageCache;


public class HeadlessDetailFragment extends Fragment implements VideoDetailActivity.HeadlessFragmentToolsCallbacks {
    private final static String LOG_TAG=HeadlessDetailFragment.class.getSimpleName();
    private Activity mCurrActivity;

    public LargePosterImageCache getPermanentCache() {
        return permanentCache;
    }

    private static LargePosterImageCache permanentCache;
    private static int CACHESIZE=3;

    private static HeadlessDetailFragment single=null;
    private FetchMovieDetailTask myMovie;
    private FetchHelperTmdbTask myPosterLoader;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment HeadlessDetailFragment.
     */
    public static HeadlessDetailFragment getInstance() {
        //Log.d(LOG_TAG + " :theInstance", "Here!");
        if (single==null) {
            single = new HeadlessDetailFragment();
            Bundle args = new Bundle();
            single.setArguments(args);
        }
        return single;
    }

    public HeadlessDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(LOG_TAG + " :onCreate", "Here!");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(LOG_TAG + " :onCreateView", "Here!");
        return null;
    }



    @Override
    public void onAttach(Activity activity) {
        //Log.d(LOG_TAG + " :onAttach", "Here!");
        super.onAttach(activity);
        if (permanentCache==null) {
            //Log.d(LOG_TAG, "CACHE size:" + CACHESIZE);
            permanentCache = new LargePosterImageCache(new LruCache<String, Bitmap>(CACHESIZE));
        }else {
            //Log.d(LOG_TAG, "REUSE CACHE ");
        }
        mCurrActivity=activity;
    }

    @Override
    public void onDetach() {
        //Log.d(LOG_TAG + " :onDetach", "Here!");
        super.onDetach();
        if (myMovie!=null) myMovie.cancel(true);
        myMovie=null;
        mCurrActivity=null;
        //parent killato per davvero
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //parent pronto
        //Log.d(LOG_TAG + " :onActivityCreated", "Here!");
    }


    public void reloadThisMovie(String movieStringID) {
        //Log.d(LOG_TAG + " :reloadThisMovie", "Here!");
        myMovie=new FetchMovieDetailTask(mCurrActivity);
        myMovie.execute(movieStringID);
    }

    public void loadImageBitmap(String imagename, Uri uriSignal){
        myPosterLoader= new FetchHelperTmdbTask(getActivity(),uriSignal,permanentCache);
        myPosterLoader.execute(imagename, JSONApiTmdbSfogliaMovie.pLargePosterWidth);
    }

}
