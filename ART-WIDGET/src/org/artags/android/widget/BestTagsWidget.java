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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 *
 * @author Pierre Levy
 */
public class BestTagsWidget extends TagsWidgetProvider
{
    private static AppWidgetManager mAppWidgetManager;
    private static int[] mAppWidgetIds;
    private static Context mContext;

    private static Tag mCurrentTag;

    @Override
    void setStatics(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        mContext = context;
        mAppWidgetManager = appWidgetManager;
        mAppWidgetIds = appWidgetIds;
    }

    @Override
    Tag getCurrentTag()
    {
        return mCurrentTag;
    }

    @Override
    Class getIntentServiceClass()
    {
        return BestTagsIntentService.class;
    }

    static void updateTag( Tag tag )
    {
        mCurrentTag = tag;
        updateWidget();
    }
    
    private static void updateWidget()
    {
        RemoteViews remoteViews = new RemoteViews( mContext.getPackageName(), R.layout.main);
        remoteViews.setImageViewBitmap(R.id.thumbnail, mCurrentTag.getBitmap());
        remoteViews.setTextViewText(R.id.text, mCurrentTag.getText());
        Intent active = new Intent(mContext, BestTagsWidget.class);
        active.setAction(ACTION_SHOW_TAG);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(mContext, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.thumbnail, actionPendingIntent);
        mAppWidgetManager.updateAppWidget(mAppWidgetIds, remoteViews);
        Log.d("ARTags Widget", "Widget updated");
    }


}
