package com.example.android.snake

/**
* User: Natalia.Ukhorskaya
*/

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.view.animation.Animation.AnimationListener
import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Snake: a simple game that everyone can enjoy.
 *
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 *
 */

class Snake() : Activity() {
    private var mSnakeView : SnakeView? = null;
    private var ICICLE_KEY : String? = "snake-view"


    protected override fun onCreate(savedInstanceState : Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.snake_layout)
        mSnakeView = (findViewById(R.id.snake) as SnakeView?)
        mSnakeView?.setTextView((findViewById(R.id.text) as TextView))
        if (savedInstanceState == null)
        {
            mSnakeView?.setMode(READY)
        }
        else
        {
            var map : Bundle? = savedInstanceState.getBundle(ICICLE_KEY).sure()
            if (map != null)
            {
                mSnakeView?.restoreState(map.sure())
            }
            else
            {
                mSnakeView?.setMode(PAUSE)
            }
        }
    }
    protected override fun onPause() : Unit {
        super<Activity>.onPause()
        mSnakeView?.setMode(PAUSE)
    }
    public override fun onSaveInstanceState(outState : Bundle?) : Unit {
        outState?.putBundle(ICICLE_KEY, mSnakeView?.saveState())
    }
}
