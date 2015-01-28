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

package com.example.fab.android.sfogliafilm.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.example.fab.android.sfogliafilm.R;
import com.example.fab.android.sfogliafilm.Utility;
import com.example.fab.android.sfogliafilm.data.JSONApiTmdbSfogliaMovie;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract;
import org.json.JSONException;
import java.util.Calendar;
import java.util.Vector;



/**
 * Real business logic implemetation
 */
public class SfogliaFilmSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL_SECONDS = 1800;
    private final String LOG_TAG= SfogliaFilmSyncAdapter.class.getSimpleName();
    private final ContentResolver mContentResolver;

    public SfogliaFilmSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    public SfogliaFilmSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    public static void syncImmediately(Context context){
        Bundle bundle=new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }



    protected static String getDevKey(){
        return "aa1e7261d9475e8fdd21de02ee2f334b";
    }


    private Uri.Builder setupUpcomingQuery(){
        Uri.Builder upcomingBulder = Uri.parse(JSONApiTmdbSfogliaMovie.MOVIE_API_BASE_URL).buildUpon();
        upcomingBulder.appendPath(JSONApiTmdbSfogliaMovie.API_UPCOMING);
        upcomingBulder.appendQueryParameter("api_key", getDevKey());
        return upcomingBulder;
    }

    private Uri.Builder setupCreditsQuery(Long movieid){
        Uri.Builder creditsBulder= Uri.parse(JSONApiTmdbSfogliaMovie.MOVIE_API_BASE_URL).buildUpon();
        creditsBulder.appendPath(movieid.toString());
        creditsBulder.appendPath("credits");
        creditsBulder.appendQueryParameter("api_key", getDevKey());
        return creditsBulder;
    }



    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //Log.d(LOG_TAG, "onPerformSync START");

        FetchMovieInfo();
        Utility.canFetchImages=true;
        //mMoviesAdapter.notifyDataSetChanged();
        //Log.d(LOG_TAG, "onPerformSync END");
    }

    private boolean FetchMovieInfo() {
        Uri.Builder cloud = setupUpcomingQuery();
        String jsonUpcomingMovies = "";
        ContentValues[] newContentsArray = new ContentValues[0];
        //JSON fetch data
        jsonUpcomingMovies= JSONApiTmdbSfogliaMovie.fetchJsonResponse(cloud.build().toString());
        //JSON parsing and DB inserts
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -1);
            String dateTimeLimit = SfogliaFilmContract.getDbDateTimeString(cal.getTime());
            int rowsInvolved = mContentResolver.delete(SfogliaFilmContract.MovieEntry.CONTENT_URI,
                    SfogliaFilmContract.MovieEntry.COLUMN_INSERTTIME + " <= ?",
                    new String[]{dateTimeLimit});
            //Log.d(LOG_TAG, "DELETED OLDer "+dateTimeLimit+" rows: " + rowsInvolved + " rows of data");
            //Log.d(LOG_TAG, "getUpcomingMoviesVectorFromJson");
            Vector<ContentValues> moviesPage= new Vector<ContentValues>(10);
            JSONApiTmdbSfogliaMovie.getUpcomingMoviesVectorFromJson(jsonUpcomingMovies, moviesPage);
            //Log.d(LOG_TAG, "Content "+moviesPage.size());
            // Bulk Insert per array of contents
            newContentsArray = new ContentValues[moviesPage.size()];
            moviesPage.toArray(newContentsArray);
            rowsInvolved = mContentResolver
                    .bulkInsert(SfogliaFilmContract.MovieEntry.CONTENT_URI, newContentsArray);
            //Log.d(LOG_TAG, "Bulk inserted " + rowsInvolved + " rows of new movies data");
        } catch (JSONException e) {
            //error parsing
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        for (ContentValues value : newContentsArray) {
            Long tmdbid = value.getAsLong(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID);
            Uri.Builder cred=setupCreditsQuery(tmdbid);
            String jsonMovieCredits = JSONApiTmdbSfogliaMovie.fetchJsonResponse(cred.build().toString());
            //JSON parsing and DB inserts
            try {
                String directedBy=JSONApiTmdbSfogliaMovie.getDirectorsFromJson(jsonMovieCredits);
                String actors=JSONApiTmdbSfogliaMovie.getCastCrewFromJson(jsonMovieCredits);
                ContentValues update=new ContentValues();
                update.put(SfogliaFilmContract.MovieEntry.COLUMN_MOVIE_ID,tmdbid);
                update.put(SfogliaFilmContract.MovieEntry.COLUMN_DIRECTORS, directedBy);
                update.put(SfogliaFilmContract.MovieEntry.COLUMN_ACTORS, actors);
                int rowsInvolved = mContentResolver
                        .update(SfogliaFilmContract.MovieEntry.buildMovieChangedUri(tmdbid), update,
                                SfogliaFilmContract.MovieEntry._ID + " = ?",
                                new String[]{tmdbid.toString()});
                //Log.d(LOG_TAG, "Updated " + rowsInvolved + " rows:"+tmdbid.toString());
            } catch (JSONException e) {
                //error parsing
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Get/Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account getSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, context.getString(R.string.sync_account_type));
        AccountManager accountManager = AccountManager.get(context);
        accountManager.addAccountExplicitly(newAccount, null, null);

        return newAccount;
    }


    public static void configurePeriodicSync(Context context, int syncInterval) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.addPeriodicSync(account,
                authority, new Bundle(), syncInterval);
        ContentResolver.setSyncAutomatically(account, authority, true);
    }

    public static void configureNetworkAvailabilitySync(Context context){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.setIsSyncable(account,  authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);
    }

    // The account name
    public static final String ACCOUNT = "TMDB sync";


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
        configurePeriodicSync(context, SYNC_INTERVAL_SECONDS);
    }
}
