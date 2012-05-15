package com.example.android.snake

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import java.util.ArrayList
import java.util.Random
import android.R
//import com.example.android.snake.SnakeView.Coordinate

/**
* User: Natalia.Ukhorskaya
*/

private val TAG = "SnakeView";

public val PAUSE : Int = 0;
public val READY : Int = 1;
public val RUNNING : Int = 2;
public val LOSE : Int = 3;
private var mMode = READY;

public val NORTH: Int = 1;
private val SOUTH: Int = 2;
private val EAST: Int = 3;
private val WEST: Int = 4;
private var mNextDirection: Int = NORTH;
private var mDirection: Int = NORTH;

private val RED_STAR = 1;
private val YELLOW_STAR = 2;
private val GREEN_STAR = 3;

private var mScore : Long = 0;
private var mMoveDelay : Long = 600;

private var mLastMove : Long? = null;

private var mStatusText : TextView? = null

//private var mSnakeTrail = ArrayList<Coordinate>();
//private var mAppleList = ArrayList<Coordinate>();

private val RNG = Random();

//public class SnakeView(val myContext : Context, val myAttrs : AttributeSet) : TileView(myContext, myAttrs) {
//
//    private val mRedrawHandler = RefreshHandler();
//
//    {
//        initSnakeView();
//    }
//
//
//    public class RefreshHandler() : Handler() {
//
//        override fun handleMessage(val msg : Message?) {
//            this@SnakeView.update();
//            this@SnakeView.invalidate();
//        }
//
//        public fun sleep(val delayMillis : Long) {
////            this.removeMessages(0);
//            sendMessageDelayed(obtainMessage(0), delayMillis);
//        }
//    }
//
//
//    private fun initSnakeView() {
//        setFocusable(true);
//
//        val r = this.getContext()?.getResources().sure();
//
//        resetTiles(4);
//        loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
//        loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
//        loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));
//
//    }
//
//
//    private fun initNewGame() {
//        mSnakeTrail.clear();
//        mAppleList.clear();
//
//        mSnakeTrail.add(Coordinate(7, 7));
//        mSnakeTrail.add(Coordinate(6, 7));
//        mSnakeTrail.add(Coordinate(5, 7));
//        mSnakeTrail.add(Coordinate(4, 7));
//        mSnakeTrail.add(Coordinate(3, 7));
//        mSnakeTrail.add(Coordinate(2, 7));
//        mNextDirection = NORTH;
//
//        addRandomApple();
//        addRandomApple();
//
//        mMoveDelay = 600;
//        mScore = 0;
//    }
//
//    private  fun coordArrayListToArray(val cvec : ArrayList<Coordinate>) : IntArray? {
//        var count = cvec.size();
//        val rawArray = IntArray(count * 2)
//        for (var index in 0..count) {
//            val c : Coordinate = cvec.get(index);
//            rawArray[2 * index] = c.x;
//            rawArray[2 * index + 1] = c.y;
//            index++
//        }
//        return rawArray;
//    }
//
//    public fun saveState() : Bundle {
//        val map = Bundle();
//
//        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
//        map.putInt("mDirection", Integer.valueOf(mDirection).sure());
//        map.putInt("mNextDirection", Integer.valueOf(mNextDirection).sure());
//        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay).sure());
//        map.putLong("mScore", Long.valueOf(mScore).sure());
//        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));
//
//        return map;
//    }
//
//    private fun coordArrayToArrayList(val rawArray : IntArray) : ArrayList<Coordinate> {
//        val coordArrayList = ArrayList<Coordinate>();
//
//        val coordCount = rawArray.size;
//        for (var index in 0..coordCount) {
//            val  c = Coordinate(rawArray[index].sure(), rawArray[index + 1].sure());
//            coordArrayList.add(c);
//            index += 2
//        }
//        return coordArrayList;
//    }
//
//    public fun restoreState(val icicle : Bundle) {
//        setMode(PAUSE);
//
//        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList").sure());
//        mDirection = icicle.getInt("mDirection");
//        mNextDirection = icicle.getInt("mNextDirection");
//        mMoveDelay = icicle.getLong("mMoveDelay");
//        mScore = icicle.getLong("mScore");
//        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail").sure());
//    }
//
//    public override fun onKeyDown(val keyCode : Int, msg : KeyEvent?) : Boolean {
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            if ((mMode == READY).or(mMode == LOSE)) {
//                initNewGame();
//                setMode(RUNNING);
//                update();
//                return (true);
//            }
//
//            if (mMode == PAUSE) {
//                setMode(RUNNING);
//                update();
//                return (true);
//            }
//
//            if (mDirection != SOUTH) {
//                mNextDirection = NORTH;
//            }
//            return (true);
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            if (mDirection != NORTH) {
//                mNextDirection = SOUTH;
//            }
//            return (true);
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            if (mDirection != EAST) {
//                mNextDirection = WEST;
//            }
//            return (true);
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            if (mDirection != WEST) {
//                mNextDirection = EAST;
//            }
//            return (true);
//        }
//
//        return super.onKeyDown(keyCode, msg);
//    }
//
//    public fun setTextView(val newView : TextView) {
//        mStatusText = newView;
//    }
//
//    public fun setMode(val newMode : Int) {
//        val oldMode = mMode;
//        mMode = newMode;
//
//        if ((newMode == RUNNING).and(oldMode != RUNNING)) {
//            mStatusText?.setVisibility(View.INVISIBLE);
//            update();
//            return;
//        }
//
//        val res = getContext()?.getResources().sure();
//        var str : CharSequence = "";
//        if (newMode == PAUSE) {
//            str = res.getText(R.string.mode_pause).sure();
//        }
//        if (newMode == READY) {
//            str = res.getText(R.string.mode_ready).sure();
//        }
//        if (newMode == LOSE) {
//            str = res.getString(R.string.mode_lose_prefix) + mScore + res.getString(R.string.mode_lose_suffix);
//        }
//
//        mStatusText?.setText(str);
//        mStatusText?.setVisibility(View.VISIBLE);
//    }
//
//    private fun addRandomApple() {
//        var newCoord : Coordinate? = null;
//        var found : Boolean = false;
//        while (!found) {
//            // Choose a new location for our apple
//            var newX = 1 + RNG.nextInt(mXTileCount - 2);
//            var newY = 1 + RNG.nextInt(mYTileCount - 2);
//            newCoord = Coordinate(newX, newY);
//
//            // Make sure it's not already under the snake
//            var collision = false;
//            val snakelength = mSnakeTrail.size();
//            for (var index in 0..snakelength) {
//                if (mSnakeTrail.get(index).equals(newCoord)) {
//                    collision = true;
//                }
//                index++
//            }
//            found = !collision;
//        }
//        if (newCoord == null) {
//            Log.e(TAG, "Somehow ended up with a null newCoord!");
//        }
//        mAppleList.add(newCoord.sure());
//    }
//
//    public fun update() {
//        if (mMode == RUNNING) {
//            val  now : Long = System.currentTimeMillis();
//
//            if (now.minus(mLastMove.sure()) > mMoveDelay) {
//                clearTiles();
//                updateWalls();
//                updateSnake();
//                updateApples();
//                mLastMove = now;
//            }
//            mRedrawHandler.sleep(mMoveDelay);
//        }
//
//    }
//
//    private fun updateWalls() {
//        for (var x in 0..mXTileCount) {
//            setTile(GREEN_STAR, x, 0);
//            setTile(GREEN_STAR, x, mYTileCount - 1);
//            x++
//        }
//        for (var y in 1..mYTileCount - 1) {
//            setTile(GREEN_STAR, 0, y);
//            setTile(GREEN_STAR, mXTileCount - 1, y);
//            y++
//        }
//    }
//
//    private fun updateApples() {
//        for (c in mAppleList) {
//            setTile(YELLOW_STAR, c.x, c.y);
//        }
//    }
//
//    private fun updateSnake() {
//        var growSnake = false;
//
//        // grab the snake by the head
//        val head = mSnakeTrail.get(0);
//        var newHead = Coordinate(1, 1);
//
//        mDirection = mNextDirection;
//
//        when (mDirection) {
//            EAST -> {
//                newHead = Coordinate(head.x + 1, head.y);
//            }
//            WEST -> {
//                newHead = Coordinate(head.x - 1, head.y);
//            }
//            NORTH -> {
//                newHead = Coordinate(head.x, head.y - 1);
//            }
//            SOUTH-> {
//                newHead = Coordinate(head.x, head.y + 1);
//            }
//            else -> {
//
//            }
//        }
//
//        if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
//        || (newHead.y > mYTileCount - 2)) {
//            setMode(LOSE);
//            return;
//        }
//
//        val snakelength = mSnakeTrail.size();
//        for (var snakeindex in 0..snakelength) {
//            val c = mSnakeTrail.get(snakeindex);
//            if (c.equals(newHead)) {
//                setMode(LOSE);
//                return;
//            }
//        }
//
//        // Look for apples
//        val applecount = mAppleList.size();
//        for (var appleindex in  0..applecount) {
//            val c = mAppleList.get(appleindex);
//            if (c.equals(newHead)) {
//                mAppleList.remove(c);
//                addRandomApple();
//
//                mScore++;
//                mMoveDelay *= 0.9;
//
//                growSnake = true;
//            }
//        }
//
//        // push a new head onto the ArrayList and pull off the tail
//        mSnakeTrail.add(0, newHead);
//        // except if we want the snake to grow
//        if (!growSnake) {
//            mSnakeTrail.remove(mSnakeTrail.size() - 1);
//        }
//
//        var index = 0;
//        for (c in mSnakeTrail) {
//            if (index == 0) {
//                setTile(YELLOW_STAR, c.x, c.y);
//            } else {
//                setTile(RED_STAR, c.x, c.y);
//            }
//            index++;
//        }
//
//    }
//
//    class Coordinate(val x : Int, val y : Int) {
//
//        public fun equals(val other : Coordinate) : Boolean {
//            if (x == other.x && y == other.y) {
//                return true;
//            }
//            return false;
//        }
//
//        public fun toString() : String {
//            return "Coordinate: [" + x + "," + y + "]";
//        }
//    }
//
//
//
//
//   /* public fun setTextView(val newView : TextView) {
//    }
//
//    public fun setMode(val newMode : Int) {
//
//    }
//
//    public fun restoreState(val icicle : Bundle) {
//
//    }
//    public fun saveState(): Bundle {
//       return Bundle()
//    }*/
//}
public class SnakeView(val myContext : Context, val myAttrs : AttributeSet): TileView(myContext, myAttrs, 0){
    public fun setTextView(val newView : TextView) {
            }

            public fun setMode(val newMode : Int) {

            }

            public fun restoreState(val icicle : Bundle) {

            }
            public fun saveState(): Bundle {
               return Bundle()
            }
}