package com.example.android.wiktionary

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import java.util.Stack
import android.util.AttributeSet
import android.os.AsyncTask

class LookupActivity(): Activity(), AnimationListener {

    public val mTitleBar: View
        get() {
            return findViewById(R.id.title_bar)!!
        }

    public val mProgress: ProgressBar
        get() {
            return findViewById(R.id.progress) as ProgressBar
        }

    public val mSlideIn: Animation
        get() {
            return AnimationUtils.loadAnimation(this, R.anim.slide_in)!!
        }

    public val mSlideOut: Animation
        get() {
            return AnimationUtils.loadAnimation(this, R.anim.slide_out)!!
        }

    val mWebView: WebView
        get() {
            return findViewById(R.id.webview) as WebView
        }

    val mTitle: TextView
        get() {
            return findViewById(R.id.title) as TextView
        }

    /**
    * History stack of previous words browsed in this session. This is
    * referenced when the user taps the "back" key, to possibly intercept and
    * show the last-visited entry, instead of closing the activity.
    */
    private val mHistory: Stack<String> = Stack<String>()

    private var mEntryTitle: String = ""

    /**
    * Keep track of last time user tapped "back" hard key. When pressed more
    * than once within {@link #BACK_THRESHOLD}, we treat let the back key fall
    * through and close the app.
    */
    private var mLastPress: Long = -1.toLong()

    private val BACK_THRESHOLD: Long = DateUtils.SECOND_IN_MILLIS / 2


    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.lookup)
        mWebView.setBackgroundColor(0)

        onNewIntent(getIntent())
    }


    public override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mHistory.empty()) {
            val currentPress = SystemClock.uptimeMillis()
            if (currentPress - mLastPress < BACK_THRESHOLD) {
                return super<Activity>.onKeyDown(keyCode, event)
            }
            mLastPress = currentPress

            val lastEntry: String = mHistory.pop()!!
            startNavigating(lastEntry, false)

            return true
        }

        return super<Activity>.onKeyDown(keyCode, event)
    }

    fun startNavigating(word: String?, val pushHistory: Boolean) {
        if (!TextUtils.isEmpty(mEntryTitle) && pushHistory) {
            mHistory.add(mEntryTitle)
        }

        displayText(word)
    }

    protected override fun onNewIntent(intent: Intent?) {
        val action = intent?.getAction()
        if (Intent.ACTION_SEARCH.equals(action)) {
            val query = intent?.getStringExtra(SearchManager.QUERY)
            startNavigating(query, true)

        } else if (Intent.ACTION_VIEW.equals(action)) {
            val data = intent?.getData()
            if (data != null && WIKI_LOOKUP_HOST.equals(data.getHost())) {
                val query = data.getPathSegments()?.get(0)
                startNavigating(query, true)
            }

        } else {
            startNavigating(null, true)
        }
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getMenuInflater()!!
        inflater.inflate(R.menu.lookup, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.lookup_search -> {
                onSearchRequested()
                return true
            }
            R.id.lookup_random -> {
                startNavigating(null, true)
                return true
            }
            R.id.lookup_about -> {
                showAbout()
                return true
            }
            else -> {
                return false
            }
        }
    }

    protected fun showAbout() {
        val messageView = getLayoutInflater()?.inflate(R.layout.about, null, false)!!

        val textView = messageView.findViewById(R.id.about_credits) as TextView
        val defaultColor = textView.getTextColors()?.getDefaultColor()!!
        textView.setTextColor(defaultColor)

        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.app_icon)
        builder.setTitle(R.string.app_name)
        builder.setView(messageView)
        builder.create()
        builder.show()
    }

    fun displayText(val word: String?): String {
        val query: String = word ?: ExtendedWikiHelper().getRandomWord()
        println(query)
        val wikiText = ExtendedWikiHelper().getPageContent(query, true)
        val parsedText = ExtendedWikiHelper().formatWikiText(wikiText) ?: getString(R.string.empty_result)!!  + " (word: " + query + ")"

        setEntryContent(parsedText)
        return parsedText
    }

    /**
    * Set the title for the current entry.
    */
    public fun setEntryTitle(entryText: String) {
        mEntryTitle = entryText
        mTitle.setText(mEntryTitle)
    }

    public fun setEntryContent(entryContent: String) {
        mWebView.loadDataWithBaseURL(WIKI_AUTHORITY, entryContent, MIME_TYPE, ENCODING, null)
    }

    public override fun onAnimationStart(p0: Animation?) {
        // Not interested when the animation starts
    }

    public override fun onAnimationEnd(p0: Animation?) {
        mProgress.setVisibility(View.INVISIBLE)
    }
    public override fun onAnimationRepeat(p0: Animation?) {
        // Not interested if the animation repeats
    }
}
