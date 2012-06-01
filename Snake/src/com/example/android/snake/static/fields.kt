package com.example.android.snake.static

import android.widget.TextView
import java.util.ArrayList
import com.example.android.snake.SnakeView.Coordinate
import java.util.Random

/**
* User: Natalia.Ukhorskaya
*/

public val PAUSE : Int = 0
public val READY : Int = 1
public val RUNNING : Int = 2
public val LOSE : Int = 3
public var mMode : Int = READY

public val NORTH : Int = 1
public val SOUTH : Int = 2
public val EAST : Int = 3
public val WEST : Int = 4
public var mNextDirection : Int = NORTH
public var mDirection : Int = NORTH

public val RED_STAR : Int = 1
public val YELLOW_STAR : Int = 2
public val GREEN_STAR : Int = 3

public var mScore : Long = 0
public var mMoveDelay : Long = 300

public var mLastMove : Long = 0

public var mStatusText : TextView? = null

public var mSnakeTrail : ArrayList<Coordinate> = ArrayList<Coordinate>()
public var mAppleList : ArrayList<Coordinate> = ArrayList<Coordinate>()

public val RNG : Random = Random()