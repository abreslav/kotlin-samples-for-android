package com.example.android.snake

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.MotionEvent
import android.widget.TextView
import com.example.android.snake.static.*
import com.example.android.snake.R.drawable
import android.graphics.drawable.Drawable

/**
* User: Natalia.Ukhorskaya
*/
/**
 * Snake: a simple game that everyone can enjoy.
 *
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 *
 */

class Snake(): Activity() {
    val mScreenWidth: Int
        get() {
            return getWindowManager()?.getDefaultDisplay()?.getWidth().sure()
        }

    val mScreenHeight: Int
        get() {
            return getWindowManager()?.getDefaultDisplay()?.getHeight().sure()
        }

    val mSnakeView: SnakeView
        get() {
            return findViewById(R.id.snake) as SnakeView
        }


    private var ICICLE_KEY: String = "snake-view"

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.snake_layout)
        mSnakeView.setTextView((findViewById(R.id.text) as TextView))
        if (savedInstanceState == null) {
            mSnakeView.setMode(READY)
        }
        else {
            var map: Bundle? = savedInstanceState.getBundle(ICICLE_KEY)
            if (map != null) {
                mSnakeView.restoreState(map.sure())
            }
            else {
                mSnakeView.setMode(PAUSE)
            }
        }
    }

    protected override fun onResume() {
        super<Activity>.onResume()
        mSnakeView.setMode(READY)
    }

    protected override fun onPause(): Unit {
        super<Activity>.onPause()
        mSnakeView.setMode(PAUSE)
    }

    public override fun onSaveInstanceState(outState: Bundle?): Unit {
        outState?.putBundle(ICICLE_KEY, mSnakeView.saveState())
    }


    public override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.getAction() == MotionEvent.ACTION_DOWN) {
            mSnakeView.maybeStart()
            val x = event?.getX().sure()
            val y = event?.getY().sure()
            if (mNextDirection == NORTH || mNextDirection == SOUTH) {
                if (x > mScreenWidth / 2) {
                    mSnakeView.setDirection("RIGHT")
                } else  {
                    mSnakeView.setDirection("LEFT")
                }
            } else if (mNextDirection == EAST || mNextDirection == WEST) {
                if (y > mScreenHeight / 2) {
                    mSnakeView.setDirection("DOWN")
                } else  {
                    mSnakeView.setDirection("UP")
                }
            }

            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(50)
        }
        return super<Activity>.onTouchEvent(event)
    }

}

