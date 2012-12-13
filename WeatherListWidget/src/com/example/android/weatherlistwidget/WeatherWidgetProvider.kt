package com.example.android.weatherlistwidget

/**
 * User: Natalia.Ukhorskaya
 */

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.content.ContentValues
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.widget.RemoteViews
import android.widget.Toast
import java.util.Random

public var CLICK_ACTION: String = "com.example.android.weatherlistwidget.CLICK"
public var REFRESH_ACTION: String = "com.example.android.weatherlistwidget.REFRESH"
public var EXTRA_CITY_ID: String = "com.example.android.weatherlistwidget.city"

public  val sWorkerThread: HandlerThread
    get() {
        val handlerThread = HandlerThread("WeatherWidgetProvider-worker")
        handlerThread.start()
        return handlerThread
    }

public val sWorkerQueue: Handler
    get() {
        return Handler(sWorkerThread.getLooper())
    }

private var sDataObserver: WeatherDataProviderObserver? = null

class WeatherDataProviderObserver(val mAppWidgetManager: AppWidgetManager?, val mComponentName: ComponentName?, h: Handler?): ContentObserver(h) {
    public override fun onChange(selfChange: Boolean): Unit {
        mAppWidgetManager?.notifyAppWidgetViewDataChanged(mAppWidgetManager?.getAppWidgetIds(mComponentName), R.id.weather_list)
    }
}

public class WeatherWidgetProvider(): AppWidgetProvider() {
    public override fun onEnabled(context: Context?): Unit {
        val r: ContentResolver = context?.getContentResolver()!!
        if (sDataObserver == null) {
            val mgr: AppWidgetManager = AppWidgetManager.getInstance(context)!!
            val cn: ComponentName = ComponentName(context, javaClass<WeatherWidgetProvider>())
            sDataObserver = WeatherDataProviderObserver(mgr, cn, sWorkerQueue)
            r.registerContentObserver(CONTENT_URI, true, sDataObserver)
        }
    }
    public override fun onReceive(ctx: Context?, intent: Intent?): Unit {
        val action = intent?.getAction()!!
        if (action.equals(REFRESH_ACTION))
        {
            val context = ctx!!
            sWorkerQueue.removeMessages(0)
            sWorkerQueue.post(object : Runnable {
                public override fun run(): Unit {
                    val r = context.getContentResolver()!!
                    val c = r.query(CONTENT_URI, null, null, null, null)!!
                    val count: Int = c.getCount()
                    val maxDegrees: Int = 35
                    if (sDataObserver == null) {
                        onEnabled(ctx)
                    }
                    r.unregisterContentObserver(sDataObserver)
                    for (i in 0..count - 1) {
                        val uri: Uri? = ContentUris.withAppendedId(CONTENT_URI, i.toLong())
                        val values = ContentValues()
                        values.put(COLUMNS.TEMPERATURE, Random().nextInt(maxDegrees))
                        r.update(uri, values, null, null)
                    }
                    r.registerContentObserver(CONTENT_URI, true, sDataObserver)
                    val mgr = AppWidgetManager.getInstance(context)!!
                    val cn = ComponentName(context, javaClass<WeatherWidgetProvider>())
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list)
                }
            })
        }
        else
            if (action.equals(CLICK_ACTION)) {
                val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)!!
                val city = intent?.getStringExtra(EXTRA_CITY_ID)!!
                val formatStr = ctx?.getResources()?.getString(R.string.toast_format_string)!!
                Toast.makeText(ctx, java.lang.String.format(formatStr, city), Toast.LENGTH_SHORT)?.show()
            }
        super.onReceive(ctx, intent)
    }
    public override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, val appWidgetIds: IntArray?): Unit {
        if (appWidgetIds == null) {
            return
        }
        for (i in 0..appWidgetIds.size - 1) {
            val intent = Intent(context, javaClass<WeatherWidgetService>())
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)))

            val rv = RemoteViews(context?.getPackageName(), R.layout.widget_layout)
            rv.setRemoteAdapter(appWidgetIds[i], R.id.weather_list, intent)
            rv.setEmptyView(R.id.weather_list, R.id.empty_view)

            val onClickIntent = Intent(context, javaClass<WeatherWidgetProvider>())
            onClickIntent.setAction(CLICK_ACTION)
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)))

            val onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent)

            val refreshIntent = Intent(context, javaClass<WeatherWidgetProvider>())
            refreshIntent.setAction(REFRESH_ACTION)

            val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent)
            appWidgetManager?.updateAppWidget(appWidgetIds[i], rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}