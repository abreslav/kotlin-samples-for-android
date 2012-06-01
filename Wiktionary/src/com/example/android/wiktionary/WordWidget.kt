package com.example.android.wiktionary

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.IBinder
import android.text.format.Time
import android.util.Log
import android.widget.RemoteViews

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
class WordWidget(): AppWidgetProvider() {
    public override fun onUpdate(context : Context?, appWidgetManager : AppWidgetManager?, appWidgetIds : IntArray?) {
        context?.startService(Intent(context, javaClass<UpdateService>()))
    }
}

class UpdateService() : Service() {
    public override fun onStart(intent : Intent?, startId : Int) {
        // Build the widget update for today
        val updateViews = buildUpdate(this)

        // Push update for this widget to the home screen
        val thisWidget = ComponentName(this, javaClass<WordWidget>())
        val manager = AppWidgetManager.getInstance(this)
        manager?.updateAppWidget(thisWidget, updateViews)
    }

    public override fun onBind(p0 : Intent?) : IBinder? {
        return null
    }

    public fun buildUpdate(val context: Context): RemoteViews {
        // Pick out month names from resources
        val res = context.getResources()
        val monthNames = res?.getStringArray(R.array.month_names)

        // Find current month and day
        val today = Time()
        today.setToNow()

        // Build the page title for today, such as "March 21"
        val pageName = res?.getString(R.string.template_wotd_title,
                monthNames?.get(today.month), today.monthDay)
        var pageContent: String? = null

        try {
            // Try querying the Wiktionary API for today's word
            SimpleWikiHelper.instance.prepareUserAgent(context)
            pageContent = SimpleWikiHelper.instance.getPageContent(pageName.sure(), false)
        } catch (e: IllegalArgumentException) {
            Log.e("WordWidget", "Couldn't contact API", e)
        }

        var views: RemoteViews?
        val matcher = Pattern.compile("(?s)\\{\\{wotd\\|(.+?)\\|(.+?)\\|([^#\\|]+).*?\\}\\}")?.matcher(pageContent).sure()
        if (matcher.find().sure()) {
            // Build an update that holds the updated widget contents
            views = RemoteViews(context.getPackageName(), R.layout.widget_word)

            val wordTitle = matcher.group(1)
            views?.setTextViewText(R.id.word_title, wordTitle)
            views?.setTextViewText(R.id.word_type, matcher.group(2))
            views?.setTextViewText(R.id.definition, matcher.group(3)?.trim())

            // When user clicks on widget, launch to Wiktionary definition page
            val definePage = String.format("%s://%s/%s", WIKI_AUTHORITY,
                    WIKI_LOOKUP_HOST, wordTitle)
            val defineIntent = Intent(Intent.ACTION_VIEW, Uri.parse(definePage))
            val pendingIntent = PendingIntent.getActivity(context,
                    0 /* no requestCode */, defineIntent, 0 /* no flags */)
            views?.setOnClickPendingIntent(R.id.widget, pendingIntent)

        } else {
            // Didn't find word of day, so show error message
            views = RemoteViews(context.getPackageName(), R.layout.widget_message)
            views?.setTextViewText(R.id.message, context.getString(R.string.widget_error))
        }
        return views.sure()
    }
}
