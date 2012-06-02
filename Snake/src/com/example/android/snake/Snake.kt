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
import android.R
import com.example.android.snake.SnakeView
import com.example.android.snake.static.*
import android.view.GestureDetector
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.view.MotionEvent
import android.gesture.GestureOverlayView.OnGestureListener
import android.gesture.GestureOverlayView
import android.view.GestureDetector.SimpleOnGestureListener
import android.graphics.Point
import android.os.Vibrator

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
    var mSensorManager: SensorManager? = null
    var mAccelerometerSensor: Sensor? = null

    var mScreenWidth = 0
    var mScreenHeight = 0

    protected override fun onResume() {
        super<Activity>.onResume()
    }

    private var mSnakeView: SnakeView? = null;
    private var ICICLE_KEY: String? = "snake-view"

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.snake_layout)
        mSnakeView = (findViewById(R.id.snake) as SnakeView?)
        mSnakeView?.setTextView((findViewById(R.id.text) as TextView))
        if (savedInstanceState == null) {
            mSnakeView?.setMode(READY)
        }
        else {
            var map: Bundle? = savedInstanceState.getBundle(ICICLE_KEY).sure()
            if (map != null)
            {
                mSnakeView?.restoreState(map.sure())
            }
            else
            {
                mSnakeView?.setMode(PAUSE)
            }

        }

        val display = getWindowManager()?.getDefaultDisplay()
        mScreenHeight = display?.getHeight().sure()
        mScreenWidth = display?.getWidth().sure()
    }

    protected override fun onPause(): Unit {
        super<Activity>.onPause()
        mSnakeView?.setMode(PAUSE)
    }

    public override fun onSaveInstanceState(outState: Bundle?): Unit {
        outState?.putBundle(ICICLE_KEY, mSnakeView?.saveState())
    }


    public override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.getAction() == MotionEvent.ACTION_DOWN) {
            val x = event?.getX().sure()
            val y = event?.getY().sure()
            if (mNextDirection == NORTH || mNextDirection == SOUTH) {
                if (x > mScreenWidth / 2) {
                    mSnakeView?.setDirection("RIGHT")
                } else  {
                    mSnakeView?.setDirection("LEFT")
                }
            } else if (mNextDirection == EAST || mNextDirection == WEST) {
                if (y > mScreenHeight / 2) {
                    mSnakeView?.setDirection("DOWN")
                } else  {
                    mSnakeView?.setDirection("UP")
                }
            }

            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(50)
        }
        return super<Activity>.onTouchEvent(event)
    }
}

