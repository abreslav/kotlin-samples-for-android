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

class LookupActivity() : Activity(), AnimationListener {

    public var mTitleBar : View? = null
    private var mTitle : TextView? = null
    public var mProgress : ProgressBar? = null
    private var mWebView : WebView? = null

    public var mSlideIn : Animation? = null
    public var mSlideOut : Animation? = null

    /**
    * History stack of previous words browsed in this session. This is
    * referenced when the user taps the "back" key, to possibly intercept and
    * show the last-visited entry, instead of closing the activity.
    */
    private var mHistory : Stack<String> = Stack<String>()

    private var mEntryTitle : String = ""

    /**
     * Keep track of last time user tapped "back" hard key. When pressed more
     * than once within {@link #BACK_THRESHOLD}, we treat let the back key fall
     * through and close the app.
     */
    private var mLastPress : Long = - 1.toLong()

    private val BACK_THRESHOLD : Long = DateUtils.SECOND_IN_MILLIS / 2


    protected override fun onCreate(savedInstanceState : Bundle?) {
        super<Activity>.onCreate(savedInstanceState)


        setContentView(R.layout.lookup)

//        TileView(this.getBaseContext().sure(), AttributeSet())


        // Load animations used to show/hide progress bar
        mSlideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        mSlideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out)

        // Listen for the "in" animation so we make the progress bar visible
        // only after the sliding has finished.
        mSlideIn?.setAnimationListener(this)

        mTitleBar = findViewById(R.id.title_bar)
        mTitle = findViewById(R.id.title) as TextView
        mProgress = findViewById(R.id.progress) as ProgressBar
        mWebView = findViewById(R.id.webview) as WebView

        // Make the view transparent to show background
        mWebView?.setBackgroundColor(0)

        // Prepare User-Agent string for wiki actions
        //       ExtendedWikiHelperKt.prepareUserAgent(this)

        // Handle incoming intents as possible searches or links
        onNewIntent(getIntent())
    }


    public override fun onKeyDown(keyCode : Int, event : KeyEvent?) : Boolean {
        // Handle back key as long we have a history stack
        if (keyCode == KeyEvent.KEYCODE_BACK && !mHistory.empty()) {

            // Compare against last pressed time, and if user hit multiple times
            // in quick succession, we should consider bailing out early.
            val currentPress = SystemClock.uptimeMillis()
            if (currentPress - mLastPress < BACK_THRESHOLD) {
                return super<Activity>.onKeyDown(keyCode, event)
            }
            mLastPress = currentPress

            // Pop last entry off stack and start loading
            val lastEntry = mHistory.pop().sure()
            startNavigating(lastEntry, false)

            return true
        }

        // Otherwise fall through to parent
        return super<Activity>.onKeyDown(keyCode, event)
    }

    private fun startNavigating(word : String?, val pushHistory : Boolean) {
        // Push any current word onto the history stack
        if (!TextUtils.isEmpty(mEntryTitle) && pushHistory) {
            mHistory.add(mEntryTitle)
        }

        displayText(word)
    }


    protected override fun onNewIntent(intent : Intent?) {
        val action = intent?.getAction()
        if (Intent.ACTION_SEARCH.equals(action)) {
            // Start query for incoming search request
            val query = intent?.getStringExtra(SearchManager.QUERY)
            startNavigating(query, true)

        } else if (Intent.ACTION_VIEW.equals(action)) {
            // Treat as internal link only if valid Uri and host matches
            val data = intent?.getData()
            if (data != null && WIKI_LOOKUP_HOST
                    .equals(data.getHost())) {
                val query = data.getPathSegments()?.get(0)
                startNavigating(query, true)
            }

        } else {
            // If not recognized, then start showing random word
            startNavigating(null, true)
        }
    }

    /**
    * {@inheritDoc}
    */
    public override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        val inflater = getMenuInflater().sure()
        inflater.inflate(R.menu.lookup, menu)
        return true
    }

    /**
     * {@inheritDoc}
     */
    public override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
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

    /**
     * Show an about dialog that cites data sources.
     */
    protected fun showAbout() {
        // Inflate the about message contents
        val messageView = getLayoutInflater()?.inflate(R.layout.about, null, false).sure()

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        val textView = messageView.findViewById(R.id.about_credits) as TextView
        val defaultColor = textView.getTextColors()?.getDefaultColor()
        textView.setTextColor(defaultColor.sure())

        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.app_icon)
        builder.setTitle(R.string.app_name)
        builder.setView(messageView)
        builder.create()
        builder.show()
    }

    fun displayText(val word : String?) : String {
        var query = word
        var parsedText : String? = null

        // If query word is null, assume request for random word
        if (query == null) {
            query = ExtendedWikiHelperKt().getRandomWord()
        }

        if (query != null) {
            // Push our requested word to the title bar
            //publishProgress(query)
            println(query)
            val wikiText = ExtendedWikiHelperKt().getPageContent(query.sure(), true)
            parsedText = ExtendedWikiHelperKt().formatWikiText(wikiText)
        }

        if (parsedText == null) {
            parsedText = getString(R.string.empty_result)
        }

        setEntryContent(parsedText.sure())
        return parsedText.sure()
    }

    /**
    * Set the title for the current entry.
    */
    public fun setEntryTitle(entryText : String) {
        mEntryTitle = entryText
        mTitle?.setText(mEntryTitle)
    }

    /**
     * Set the content for the current entry. This will update our
     * {@link WebView} to show the requested content.
     */
    public fun setEntryContent(entryContent : String) {
        mWebView?.loadDataWithBaseURL(WIKI_AUTHORITY, entryContent,
                MIME_TYPE, ENCODING, null)
    }


    public override fun onAnimationStart(p0 : Animation?) {
        // Not interested when the animation starts
    }
    /**
     * Make the {@link ProgressBar} visible when our in-animation finishes.
     */
    public override fun onAnimationEnd(p0 : Animation?) {
        mProgress?.setVisibility(View.INVISIBLE)
    }
    public override fun onAnimationRepeat(p0 : Animation?) {
        // Not interested if the animation repeats
    }

}
