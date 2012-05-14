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
import com.example.android.snake.SnakeView.RefreshHandler
import java.util.ArrayList
import java.util.Random
import com.example.android.snake.SnakeView.Coordinate

/**
* User: Natalia.Ukhorskaya
*/

//public open class SnakeViewKt(_TAG : String?, _PAUSE : Int, _READY : Int, _RUNNING : Int, _LOSE : Int, _NORTH : Int, _SOUTH : Int, _EAST : Int, _WEST : Int, _RED_STAR : Int, _YELLOW_STAR : Int, _GREEN_STAR : Int, _mLastMove : Long, _mStatusText : TextView?, _RNG : Random?) : TileView() {
//    private var mMode : Int = READY
//    private var mDirection : Int = NORTH
//    private var mNextDirection : Int = NORTH
//    private var mScore : Long = 0
//    private var mMoveDelay : Long = 600
//    private var mLastMove : Long = 0
//    private var mStatusText : TextView? = null
//    private var mSnakeTrail : ArrayList<Coordinate?>? = ArrayList<Coordinate?>()
//    private var mAppleList : ArrayList<Coordinate?>? = ArrayList<Coordinate?>()
//    private var mRedrawHandler : RefreshHandler? = RefreshHandler()
//    open class RefreshHandler() : Handler() {
//        public open fun handleMessage(msg : Message?) : Unit {
//            this@SnakeView.update()
//            this@SnakeView.invalidate()
//        }
//        public open fun sleep(delayMillis : Long) : Unit {
//            this.removeMessages(0)
//            sendMessageDelayed(obtainMessage(0), delayMillis)
//        }
//    }
//    private open fun initSnakeView() : Unit {
//        setFocusable(true)
//        var r : Resources? = this.getContext().getResources()
//        resetTiles(4)
//        loadTile(RED_STAR, r?.getDrawable(R.drawable.redstar))
//        loadTile(YELLOW_STAR, r?.getDrawable(R.drawable.yellowstar))
//        loadTile(GREEN_STAR, r?.getDrawable(R.drawable.greenstar))
//    }
//    private open fun initNewGame() : Unit {
//        mSnakeTrail?.clear()
//        mAppleList?.clear()
//        mSnakeTrail?.add(Coordinate(7, 7))
//        mSnakeTrail?.add(Coordinate(6, 7))
//        mSnakeTrail?.add(Coordinate(5, 7))
//        mSnakeTrail?.add(Coordinate(4, 7))
//        mSnakeTrail?.add(Coordinate(3, 7))
//        mSnakeTrail?.add(Coordinate(2, 7))
//        mNextDirection = NORTH
//        addRandomApple()
//        addRandomApple()
//        mMoveDelay = 600
//        mScore = 0
//    }
//    private open fun coordArrayListToArray(cvec : ArrayList<Coordinate?>?) : IntArray? {
//        var count : Int = cvec?.size().sure()
//        var rawArray : IntArray? = IntArray(count * 2)
//        for (index in 0..count - 1) {
//            var c : Coordinate? = cvec?.get(index).sure()
//            rawArray[2 * index] = c?.x
//            rawArray[2 * index + 1] = c?.y
//        }
//        return rawArray
//    }
//    public open fun saveState() : Bundle? {
//        var map : Bundle? = Bundle()
//        map?.putIntArray("mAppleList", coordArrayListToArray(mAppleList))
//        map?.putInt("mDirection", Integer.valueOf(mDirection))
//        map?.putInt("mNextDirection", Integer.valueOf(mNextDirection))
//        map?.putLong("mMoveDelay", Long.valueOf(mMoveDelay))
//        map?.putLong("mScore", Long.valueOf(mScore))
//        map?.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail))
//        return map
//    }
//    private open fun coordArrayToArrayList(rawArray : IntArray?) : ArrayList<Coordinate?>? {
//        var coordArrayList : ArrayList<Coordinate?>? = ArrayList<Coordinate?>()
//        var coordCount : Int = rawArray?.length.sure()
//            {
//                var index : Int = 0
//                while (index < coordCount)
//                {
//                        {
//                            var c : Coordinate? = Coordinate(rawArray[index], rawArray[index + 1])
//                            coordArrayList?.add(c)
//                        }
//                        {
//                            index += 2
//                        }
//                }
//            }
//        return coordArrayList
//    }
//    public open fun restoreState(icicle : Bundle?) : Unit {
//        setMode(PAUSE)
//        mAppleList = coordArrayToArrayList((icicle?.getIntArray("mAppleList")).sure())
//        mDirection = icicle?.getInt("mDirection")
//        mNextDirection = icicle?.getInt("mNextDirection")
//        mMoveDelay = icicle?.getLong("mMoveDelay")
//        mScore = icicle?.getLong("mScore")
//        mSnakeTrail = coordArrayToArrayList((icicle?.getIntArray("mSnakeTrail")).sure())
//    }
//    public open fun onKeyDown(keyCode : Int, msg : KeyEvent?) : Boolean {
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
//        {
//            if (mMode == READY or mMode == LOSE)
//            {
//                initNewGame()
//                setMode(RUNNING)
//                update()
//                return (true)
//            }
//            if (mMode == PAUSE)
//            {
//                setMode(RUNNING)
//                update()
//                return (true)
//            }
//            if (mDirection != SOUTH)
//            {
//                mNextDirection = NORTH
//            }
//            return (true)
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
//        {
//            if (mDirection != NORTH)
//            {
//                mNextDirection = SOUTH
//            }
//            return (true)
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
//        {
//            if (mDirection != EAST)
//            {
//                mNextDirection = WEST
//            }
//            return (true)
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
//        {
//            if (mDirection != WEST)
//            {
//                mNextDirection = EAST
//            }
//            return (true)
//        }
//        return super.onKeyDown(keyCode, msg)
//    }
//    public open fun setTextView(newView : TextView?) : Unit {
//        mStatusText = newView
//    }
//    public open fun setMode(newMode : Int) : Unit {
//        var oldMode : Int = mMode
//        mMode = newMode
//        if (newMode == RUNNING and oldMode != RUNNING)
//        {
//            mStatusText?.setVisibility(View.INVISIBLE)
//            update()
//            return
//        }
//        var res : Resources? = getContext().getResources()
//        var str : CharSequence? = ""
//        if (newMode == PAUSE)
//        {
//            str = res?.getText(R.string.mode_pause)
//        }
//        if (newMode == READY)
//        {
//            str = res?.getText(R.string.mode_ready)
//        }
//        if (newMode == LOSE)
//        {
//            str = (res?.getString(R.string.mode_lose_prefix)).sure() + mScore + (res?.getString(R.string.mode_lose_suffix)).sure()
//        }
//        mStatusText?.setText(str)
//        mStatusText?.setVisibility(View.VISIBLE)
//    }
//    private open fun addRandomApple() : Unit {
//        var newCoord : Coordinate? = null
//        var found : Boolean = false
//        while (!found)
//        {
//            var newX : Int = 1 + (RNG?.nextInt(mXTileCount - 2)).sure()
//            var newY : Int = 1 + (RNG?.nextInt(mYTileCount - 2)).sure()
//            newCoord = Coordinate(newX, newY)
//            var collision : Boolean = false
//            var snakelength : Int = mSnakeTrail?.size().sure()
//            for (index in 0..snakelength - 1) {
//                if (mSnakeTrail?.get(index).equals(newCoord))
//                {
//                    collision = true
//                }
//            }
//            found = !collision
//        }
//        if (newCoord == null)
//        {
//            Log.e(TAG, "Somehow ended up with a null newCoord!")
//        }
//        mAppleList?.add(newCoord)
//    }
//    public open fun update() : Unit {
//        if (mMode == RUNNING)
//        {
//            var now : Long = System.currentTimeMillis()
//            if (now - mLastMove > mMoveDelay)
//            {
//                clearTiles()
//                updateWalls()
//                updateSnake()
//                updateApples()
//                mLastMove = now
//            }
//            mRedrawHandler?.sleep(mMoveDelay)
//        }
//    }
//    private open fun updateWalls() : Unit {
//        for (x in 0..mXTileCount - 1) {
//            setTile(GREEN_STAR, x, 0)
//            setTile(GREEN_STAR, x, mYTileCount - 1)
//        }
//        for (y in 1..mYTileCount - 1 - 1) {
//            setTile(GREEN_STAR, 0, y)
//            setTile(GREEN_STAR, mXTileCount - 1, y)
//        }
//    }
//    private open fun updateApples() : Unit {
//        for (c : Coordinate? in mAppleList)
//        {
//            setTile(YELLOW_STAR, c?.x, c?.y)
//        }
//    }
//    private open fun updateSnake() : Unit {
//        var growSnake : Boolean = false
//        var head : Coordinate? = mSnakeTrail?.get(0).sure()
//        var newHead : Coordinate? = Coordinate(1, 1)
//        mDirection = mNextDirection
//        when (mDirection) {
//            EAST -> {
//                    {
//                        newHead = Coordinate((head?.x).sure() + 1, (head?.y).sure())
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure() - 1, (head?.y).sure())
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() - 1)
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() + 1)
//                        break
//                    }
//            }
//            WEST -> {
//                    {
//                        newHead = Coordinate((head?.x).sure() - 1, (head?.y).sure())
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() - 1)
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() + 1)
//                        break
//                    }
//            }
//            NORTH -> {
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() - 1)
//                        break
//                    }
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() + 1)
//                        break
//                    }
//            }
//            SOUTH -> {
//                    {
//                        newHead = Coordinate((head?.x).sure(), (head?.y).sure() + 1)
//                        break
//                    }
//            }
//        }
//        if (((newHead?.x).sure() < 1) || ((newHead?.y).sure() < 1) || ((newHead?.x).sure() > mXTileCount - 2) || ((newHead?.y).sure() > mYTileCount - 2))
//        {
//            setMode(LOSE)
//            return
//        }
//        var snakelength : Int = mSnakeTrail?.size().sure()
//        for (snakeindex in 0..snakelength - 1) {
//            var c : Coordinate? = mSnakeTrail?.get(snakeindex).sure()
//            if (c?.equals(newHead).sure())
//            {
//                setMode(LOSE)
//                return
//            }
//        }
//        var applecount : Int = mAppleList?.size().sure()
//        for (appleindex in 0..applecount - 1) {
//            var c : Coordinate? = mAppleList?.get(appleindex).sure()
//            if (c?.equals(newHead).sure())
//            {
//                mAppleList?.remove(c)
//                addRandomApple()
//                mScore++
//                mMoveDelay *= 0.9
//                growSnake = true
//            }
//        }
//        mSnakeTrail?.add(0, newHead)
//        if (!growSnake)
//        {
//            mSnakeTrail?.remove((mSnakeTrail?.size()).sure() - 1)
//        }
//        var index : Int = 0
//        for (c : Coordinate? in mSnakeTrail)
//        {
//            if (index == 0)
//            {
//                setTile(YELLOW_STAR, c?.x, c?.y)
//            }
//            else
//            {
//                setTile(RED_STAR, c?.x, c?.y)
//            }
//            index++
//        }
//    }
//    private open class Coordinate(newX : Int, newY : Int) {
//        public var x : Int = 0
//        public var y : Int = 0
//        public open fun equals(other : Coordinate?) : Boolean {
//            if (x == (other?.x).sure() && y == (other?.y).sure())
//            {
//                return true
//            }
//            return false
//        }
//        public open fun toString() : String? {
//            return "Coordinate: [" + x + "," + y + "]"
//        }
//        {
//            x = newX
//            y = newY
//        }
//    }
//    {
//        TAG = _TAG
//        PAUSE = _PAUSE
//        READY = _READY
//        RUNNING = _RUNNING
//        LOSE = _LOSE
//        NORTH = _NORTH
//        SOUTH = _SOUTH
//        EAST = _EAST
//        WEST = _WEST
//        RED_STAR = _RED_STAR
//        YELLOW_STAR = _YELLOW_STAR
//        GREEN_STAR = _GREEN_STAR
//        mLastMove = _mLastMove
//        mStatusText = _mStatusText
//        RNG = _RNG
//    }
//    class object {
//        public open fun init(context : Context?, attrs : AttributeSet?) : SnakeView {
//            val __ = SnakeView(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null)
//            super(context, attrs)
//            initSnakeView()
//            return __
//        }
//        public open fun init(context : Context?, attrs : AttributeSet?, defStyle : Int) : SnakeView {
//            val __ = SnakeView(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null)
//            super(context, attrs, defStyle)
//            initSnakeView()
//            return __
//        }
//        private val TAG : String? = "SnakeView"
//        public val PAUSE : Int = 0
//        public val READY : Int = 1
//        public val RUNNING : Int = 2
//        public val LOSE : Int = 3
//        private val NORTH : Int = 1
//        private val SOUTH : Int = 2
//        private val EAST : Int = 3
//        private val WEST : Int = 4
//        private val RED_STAR : Int = 1
//        private val YELLOW_STAR : Int = 2
//        private val GREEN_STAR : Int = 3
//        private val RNG : Random? = Random()
//    }
//}

private val TAG = "SnakeView";

/**
* Current mode of application: READY to run, RUNNING, or you have already
* lost. static final ints are used instead of an enum for performance
* reasons.
*/
private var mMode = READY;
public val PAUSE : Int = 0;
public val READY : Int = 1;
public val RUNNING : Int = 2;
public val LOSE : Int = 3;

/**
* Current direction the snake is headed.
*/
private var mDirection = NORTH;
private var mNextDirection = NORTH;
private val NORTH = 1;
private val SOUTH = 2;
private val EAST = 3;
private val WEST = 4;

/**
* Labels for the drawables that will be loaded into the TileView class
*/
private val RED_STAR = 1;
private val YELLOW_STAR = 2;
private val GREEN_STAR = 3;

/**
* mScore: used to track the number of apples captured mMoveDelay: number of
* milliseconds between snake movements. This will decrease as apples are
* captured.
*/
private var mScore : Long = 0;
private var mMoveDelay : Long = 600;
/**
* mLastMove: tracks the absolute time when the snake last moved, and is used
* to determine if a move should be made based on mMoveDelay.
*/
private var mLastMove : Long? = null;

/**
* mStatusText: text shows to the user in some run states
*/
private var mStatusText : TextView? = null

/**
* mSnakeTrail: a list of Coordinates that make up the snake's body
* mAppleList: the secret location of the juicy apples the snake craves.
*/
private var mSnakeTrail = ArrayList<Coordinate>();
private var mAppleList = ArrayList<Coordinate>();

/**
* Everyone needs a little randomness in their life
*/
private val RNG = Random();

/**
* Create a simple handler that we can use to cause animation to happen.  We
* set ourselves as a target and we can use the sleep()
* function to cause an update/invalidate to occur at a later date.
*/



class SnakeView(val myContext : Context, val myAttrs : AttributeSet) : TileView(myContext, myAttrs, 0) {

    private val mRedrawHandler = RefreshHandler();

    {
        initSnakeView();
    }


    public class RefreshHandler() : Handler() {

        override fun handleMessage(val msg : Message?) {
            /*this@SnakeView.update();
            this@SnakeView.invalidate();*/
        }

        public fun sleep(val delayMillis : Long) {
//            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }


    private fun initSnakeView() {
        setFocusable(true);

        val r = this.getContext()?.getResources().sure();

        resetTiles(4);
        loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
        loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
        loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));

    }


    private fun initNewGame() {
        mSnakeTrail.clear();
        mAppleList.clear();

        // For now we're just going to load up a short default eastbound snake
        // that's just turned north


        mSnakeTrail.add(Coordinate(7, 7));
        mSnakeTrail.add(Coordinate(6, 7));
        mSnakeTrail.add(Coordinate(5, 7));
        mSnakeTrail.add(Coordinate(4, 7));
        mSnakeTrail.add(Coordinate(3, 7));
        mSnakeTrail.add(Coordinate(2, 7));
        mNextDirection = NORTH;

        // Two apples to start with
        addRandomApple();
        addRandomApple();

        mMoveDelay = 600;
        mScore = 0;
    }


    /**
    * Given a ArrayList of coordinates, we need to flatten them into an array of
    * ints before we can stuff them into a map for flattening and storage.
    *
    * @param cvec : a ArrayList of Coordinate objects
    * @return : a simple array containing the x/y values of the coordinates
    * as [x1,y1,x2,y2,x3,y3...]
    */
    private  fun coordArrayListToArray(val cvec : ArrayList<Coordinate>) : IntArray? {
        var count = cvec.size();
        val rawArray = IntArray(count * 2)
        for (var index in 0..count) {
            val c : Coordinate = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
            index++
        }
        return rawArray;
    }

    /**
    * Save game state so that the user does not lose anything
    * if the game process is killed while we are in the
    * background.
    *
    * @return a Bundle with this view's state
    */
    public fun saveState() : Bundle {
        val map = Bundle();

        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
        map.putInt("mDirection", Integer.valueOf(mDirection).sure());
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection).sure());
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay).sure());
        map.putLong("mScore", Long.valueOf(mScore).sure());
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));

        return map;
    }

    /**
    * Given a flattened array of ordinate pairs, we reconstitute them into a
    * ArrayList of Coordinate objects
    *
    * @param rawArray : [x1,y1,x2,y2,...]
    * @return a ArrayList of Coordinates
    */
    private fun coordArrayToArrayList(val rawArray : IntArray) : ArrayList<Coordinate> {
        val coordArrayList = ArrayList<Coordinate>();

        val coordCount = rawArray.size;
        for (var index in 0..coordCount) {
            val  c = Coordinate(rawArray[index].sure(), rawArray[index + 1].sure());
            coordArrayList.add(c);
            index += 2
        }
        return coordArrayList;
    }

    /**
    * Restore game state if our process is being relaunched
    *
    * @param icicle a Bundle containing the game state
    */
    public fun restoreState(val icicle : Bundle) {
        setMode(PAUSE);

        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList").sure());
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail").sure());
    }

    /*
    * handles key events in the game. Update the direction our snake is traveling
    * based on the DPAD. Ignore events that would cause the snake to immediately
    * turn back on itself.
    *
    * (non-Javadoc)
    *
    * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
    */

    public override fun onKeyDown(val keyCode : Int, msg : KeyEvent?) : Boolean {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if ((mMode == READY).or(mMode == LOSE)) {
                /*
                * At the beginning of the game, or the end of a previous one,
                * we should start a new game.
                */
                initNewGame();
                setMode(RUNNING);
                update();
                return (true);
            }

            if (mMode == PAUSE) {
                /*
                * If the game is merely paused, we should just continue where
                * we left off.
                */
                setMode(RUNNING);
                update();
                return (true);
            }

            if (mDirection != SOUTH) {
                mNextDirection = NORTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mDirection != NORTH) {
                mNextDirection = SOUTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mDirection != EAST) {
                mNextDirection = WEST;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mDirection != WEST) {
                mNextDirection = EAST;
            }
            return (true);
        }

        return super.onKeyDown(keyCode, msg);
    }

    /**
    * Sets the TextView that will be used to give information (such as "Game
    * Over" to the user.
    *
    * @param newView
    */
    public fun setTextView(val newView : TextView) {
        mStatusText = newView;
    }

    /**
    * Updates the current mode of the application (RUNNING or PAUSED or the like)
    * as well as sets the visibility of textview for notification
    *
    * @param newMode
    */
    public fun setMode(val newMode : Int) {
        val oldMode = mMode;
        mMode = newMode;

        if ((newMode == RUNNING).and(oldMode != RUNNING)) {
            mStatusText?.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        val res = getContext()?.getResources().sure();
        var str : CharSequence = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause).sure();
        }
        if (newMode == READY) {
            str = res.getText(R.string.mode_ready).sure();
        }
        if (newMode == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + mScore + res.getString(R.string.mode_lose_suffix);
        }

        mStatusText?.setText(str);
        mStatusText?.setVisibility(View.VISIBLE);
    }

    /**
    * Selects a random location within the garden that is not currently covered
    * by the snake. Currently _could_ go into an infinite loop if the snake
    * currently fills the garden, but we'll leave discovery of this prize to a
    * truly excellent snake-player.
    *
    */
    private fun addRandomApple() {
        var newCoord : Coordinate? = null;
        var found : Boolean = false;
        while (!found) {
            // Choose a new location for our apple
            var newX = 1 + RNG.nextInt(mXTileCount - 2);
            var newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = Coordinate(newX, newY);

            // Make sure it's not already under the snake
            var collision = false;
            val snakelength = mSnakeTrail.size();
            for (var index in 0..snakelength) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                    collision = true;
                }
                index++
            }
            // if we're here and there's been no collision, then we have
            // a good location for an apple. Otherwise, we'll circle back
            // and try again
            found = !collision;
        }
        if (newCoord == null) {
            Log.e(TAG, "Somehow ended up with a null newCoord!");
        }
        mAppleList.add(newCoord.sure());
    }


    /**
    * Handles the basic update loop, checking to see if we are in the running
    * state, determining if a move should be made, updating the snake's location.
    */
    public fun update() {
        if (mMode == RUNNING) {
            val  now : Long = System.currentTimeMillis();

            if (now.minus(mLastMove.sure()) > mMoveDelay) {
                clearTiles();
                updateWalls();
                updateSnake();
                updateApples();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }

    }

    /**
    * Draws some walls.
    *
    */
    private fun updateWalls() {
        for (var x in 0..mXTileCount) {
            setTile(GREEN_STAR, x, 0);
            setTile(GREEN_STAR, x, mYTileCount - 1);
            x++
        }
        for (var y in 1..mYTileCount - 1) {
            setTile(GREEN_STAR, 0, y);
            setTile(GREEN_STAR, mXTileCount - 1, y);
            y++
        }
    }

    /**
    * Draws some apples.
    *
    */
    private fun updateApples() {
        for (c in mAppleList) {
            setTile(YELLOW_STAR, c.x, c.y);
        }
    }

    /**
    * Figure out which way the snake is going, see if he's run into anything (the
    * walls, himself, or an apple). If he's not going to die, we then add to the
    * front and subtract from the rear in order to simulate motion. If we want to
    * grow him, we don't subtract from the rear.
    *
    */
    private fun updateSnake() {
        var growSnake = false;

        // grab the snake by the head
        val head = mSnakeTrail.get(0);
        var newHead = Coordinate(1, 1);

        mDirection = mNextDirection;

        when (mDirection) {
            EAST -> {
                newHead = Coordinate(head.x + 1, head.y);
            }
            WEST -> {
                newHead = Coordinate(head.x - 1, head.y);
            }
            NORTH -> {
                newHead = Coordinate(head.x, head.y - 1);
            }
            SOUTH-> {
                newHead = Coordinate(head.x, head.y + 1);
            }
            else -> {

            }
        }

        // Collision detection
        // For now we have a 1-square wall around the entire arena
        if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
        || (newHead.y > mYTileCount - 2)) {
            setMode(LOSE);
            return;

        }

        // Look for collisions with itself
        val snakelength = mSnakeTrail.size();
        for (var snakeindex in 0..snakelength) {
            val c = mSnakeTrail.get(snakeindex);
            if (c.equals(newHead)) {
                setMode(LOSE);
                return;
            }
        }

        // Look for apples
        val applecount = mAppleList.size();
        for (var appleindex in  0..applecount) {
            val c = mAppleList.get(appleindex);
            if (c.equals(newHead)) {
                mAppleList.remove(c);
                addRandomApple();

                mScore++;
                mMoveDelay *= 0.9;

                growSnake = true;
            }
        }

        // push a new head onto the ArrayList and pull off the tail
        mSnakeTrail.add(0, newHead);
        // except if we want the snake to grow
        if (!growSnake) {
            mSnakeTrail.remove(mSnakeTrail.size() - 1);
        }

        var index = 0;
        for (c in mSnakeTrail) {
            if (index == 0) {
                setTile(YELLOW_STAR, c.x, c.y);
            } else {
                setTile(RED_STAR, c.x, c.y);
            }
            index++;
        }

    }

    /**
    * Simple class containing two integer values and a comparison function.
    * There's probably something I should use instead, but this was quick and
    * easy to build.
    *
    */
    class Coordinate(val x : Int, val y : Int) {

        public fun equals(val other : Coordinate) : Boolean {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        public fun toString() : String {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

}
