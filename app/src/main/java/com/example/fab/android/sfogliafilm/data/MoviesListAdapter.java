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
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.fab.android.sfogliafilm.R;
import com.example.fab.android.sfogliafilm.Utility;
import com.example.fab.android.sfogliafilm.data.SfogliaFilmContract.MovieEntry;


public class MoviesListAdapter extends CursorAdapter {
    private final String LOG_TAG=MoviesListAdapter.class.getSimpleName();
    private final Context myContext;
    private SmallPosterImageCache ica;
    public static final int SMALL_ITEM=42;
    public static final int LARGE_ITEM=56;
    private int itemViewType;

    public MoviesListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        myContext=context;
    }

    public MoviesListAdapter(Context activity) {
       this(activity,null,0);
    }

    protected void setItemListType(int listType){
        if (listType==LARGE_ITEM)
            itemViewType=listType;
        else
            itemViewType=SMALL_ITEM;
    }

    public void setBitmapCache(SmallPosterImageCache smallPosterImageCache) {
        ica= smallPosterImageCache;
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView smallPosterView;
        public final TextView showdateView;
        public final TextView titleView;
        public final TextView summaryView;

        public ViewHolder(View view, MoviesListAdapter caller) {
            smallPosterView = (ImageView) view.findViewById(R.id.smallPoster);
            titleView=(TextView) view.findViewById(R.id.tv_showTitle);
            summaryView=(TextView) view.findViewById(R.id.tv_shortsummary);
            if (summaryView==null) {
                caller.setItemListType(SMALL_ITEM);
            } else {
                caller.setItemListType(LARGE_ITEM);
            }
            showdateView=(TextView) view.findViewById(R.id.tv_showDate);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.list_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, this);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            int viewType = getItemViewType(cursor.getPosition());
            ViewHolder vh = (ViewHolder) view.getTag();
            String temp;
            String debug_message="DEB ";
            vh.titleView.setText(cursor.getString(MovieDbHelper.getIndexColumnProjection(MovieEntry.COLUMN_TITLE)));
            vh.showdateView.setText(cursor.getString(MovieDbHelper.getIndexColumnProjection(MovieEntry.COLUMN_RELEASE_DATE)));
            temp = cursor.getString(MovieDbHelper.getIndexColumnProjection(MovieEntry.COLUMN_POSTER_PATH));
            ica.loadBitmap(temp, vh.smallPosterView);

            if (itemViewType==LARGE_ITEM) {
                temp = cursor.getString(MovieDbHelper.getIndexColumnProjection(MovieEntry.COLUMN_DIRECTORS));
                if(temp==null) {
                    vh.summaryView.setText("");
                    vh.summaryView.setVisibility(View.GONE);
                }else {

                    vh.summaryView.setText(context.getString(R.string.regia)+" "+ temp);
                    vh.summaryView.setVisibility(View.VISIBLE);
                }
            }
            debug_message+=" BITMAP:"+Utility.canFetchImages+temp;
            //Log.d(LOG_TAG, " ==> " + debug_message);
        }
    }




}
