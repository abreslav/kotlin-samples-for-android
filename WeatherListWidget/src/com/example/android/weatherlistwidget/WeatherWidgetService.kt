package com.example.android.weatherlistwidget

/**
 * User: Natalia.Ukhorskaya
 */

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory

public open class WeatherWidgetService(): RemoteViewsService() {
    public override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory? {
        return StackRemoteViewsFactory(this.getApplicationContext(), intent)
    }
}
open class StackRemoteViewsFactory(val context: Context?, val intent: Intent?): RemoteViewsService.RemoteViewsFactory {
    private var mCursor: Cursor = context?.getContentResolver()?.query(CONTENT_URI, null, null, null, null)!!
    private val mAppWidgetId: Int
        get() {
            return intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)!!
        }

    public override fun onCreate(): Unit {
    }
    public override fun onDestroy(): Unit {
        mCursor.close()
    }
    public override fun getCount(): Int {
        return mCursor.getCount()
    }
    public override fun getViewAt(position: Int): RemoteViews? {
        var city = "Unknown City"
        var temp: Int = 0
        if (mCursor.moveToPosition(position))
        {
            val cityColIndex: Int = mCursor.getColumnIndex(COLUMNS.CITY)
            val tempColIndex: Int = mCursor.getColumnIndex(COLUMNS.TEMPERATURE)
            city = mCursor.getString(cityColIndex)!!
            temp = mCursor.getInt(tempColIndex)
        }
        val formatStr = context?.getResources()?.getString(R.string.item_format_string)!!
        val itemId: Int = if (position % 2 == 0) R.layout.light_widget_item else  R.layout.dark_widget_item

        var rv = RemoteViews(context?.getPackageName(), itemId)
        rv.setTextViewText(R.id.widget_item, java.lang.String.format(formatStr, temp, city))
        val fillInIntent = Intent()
        val extras = Bundle()
        extras.putString(EXTRA_CITY_ID, city)
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)
        return rv
    }
    public override fun getLoadingView(): RemoteViews? {
        return null
    }
    public override fun getViewTypeCount(): Int {
        return 2
    }
    public override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    public override fun hasStableIds(): Boolean {
        return true
    }
    public override fun onDataSetChanged(): Unit {
        mCursor.close()
        mCursor = context?.getContentResolver()?.query(CONTENT_URI, null, null, null, null)!!
    }
}