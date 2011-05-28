/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.artags.android.widget;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Pierre Levy
 */
public abstract class AbstractTagsIntentService extends IntentService
{

    private long mStartTime;
    private int mRefreshDelay = 7;
    private Tag mCurrentTag;
    private List<Tag> mTagList;
    private int mCurrentTagIndex;
    private boolean mRunning;

    abstract String getTagListUrl();

    abstract void updateTag(Tag tag);

    public AbstractTagsIntentService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d( Constants.LOG_TAG, "onHandleIntent - Starting Fetch Task");

        mFetchingTask.execute();

    }
    
    private AsyncTask<Void, Void, Void> mFetchingTask = new AsyncTask<Void, Void, Void>()
    {

        @Override
        protected Void doInBackground(Void... args)
        {
            mTagList = getTagList();
            for (Tag tag : mTagList)
            {
                tag.setBitmap(HttpUtils.loadBitmap(tag.getThumbnailUrl()));
            }
            return null;
        }

        @Override
        protected void onPostExecute( Void result )
        {
            Log.d(Constants.LOG_TAG, "Asynchronous Fetch Task completed");
            super.onPostExecute( result );
            mCurrentTagIndex = 0;
            mHandler.removeCallbacks(mUpdateTimeTask);

            if (!mRunning)
            {
                mUpdateTimeTask.run();
                mRunning = true;
            }
        }

    };
    
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable()
    {

        @Override
        public void run()
        {
            Log.d(Constants.LOG_TAG, "Run update thread");

            final long start = mStartTime;
            final long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            final int minutes = seconds / 60;
            seconds = seconds % 60;
            mCurrentTag = mTagList.get(mCurrentTagIndex);
            updateTag(mCurrentTag);
            mCurrentTagIndex++;
            if (mCurrentTagIndex >= mTagList.size())
            {
                mCurrentTagIndex = 0;
            }
            mHandler.postAtTime(this, start + (((minutes * 60) + seconds + mRefreshDelay) * 1000));
        }

    };

    private List<Tag> getTagList()
    {
        List<Tag> list = new ArrayList<Tag>();
        String jsonflow = HttpUtils.getUrl(getTagListUrl());

        try
        {

            JSONTokener tokener = new JSONTokener(jsonflow);
            JSONObject json = (JSONObject) tokener.nextValue();
            JSONArray jsonTags = json.getJSONArray("tags");

            int max = (jsonTags.length() < Constants.MAX_TAGS) ? jsonTags.length() : Constants.MAX_TAGS;
            for (int i = 0; i < max; i++)
            {
                JSONObject jsonTag = jsonTags.getJSONObject(i);
                Tag tag = new Tag();
                tag.setId(jsonTag.getString("id"));
                tag.setText(jsonTag.getString("title"));
                tag.setThumbnailUrl(jsonTag.getString("imageUrl"));
                tag.setRating(jsonTag.getString("rating"));
                list.add(tag);
            }
        } catch (JSONException e)
        {
            throw new RuntimeException("JSON Parsing Error : " + e.getMessage(), e);
        }
        return list;
    }
}