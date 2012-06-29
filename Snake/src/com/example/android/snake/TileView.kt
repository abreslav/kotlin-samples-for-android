package com.example.android.snake

/**
* User: Natalia.Ukhorskaya
*/

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import java.util.ArrayList


open class TileView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var mTileSize: Int = 1

    {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TileView).sure()
        mTileSize = a.getInt(R.styleable.TileView_tileSize, 12)
        a.recycle()
    }


    private var mTileArray: Array<Bitmap?> = Array<Bitmap?>(0) {null}
    private var mTileGrid: ArrayList<ArrayList<Integer>> = ArrayList<ArrayList<Integer>>()
    private val mPaint: Paint = Paint()

    var mXTileCount: Int = 0
    var mYTileCount: Int = 0
    private var mXOffset: Int = 0
    private var mYOffset: Int = 0


    public open fun resetTiles(tilecount: Int): Unit {
        mTileArray = Array<Bitmap?>(tilecount) { null }
//        for (i in 0..tilecount) {
//            mTileArray.add(null)
//        }
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit {
        mXTileCount = Math.floor((w / mTileSize).toDouble()).toInt()
        mYTileCount = Math.floor((h / mTileSize).toDouble()).toInt()
        mXOffset = ((w - (mTileSize * mXTileCount)) / 2)
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2)
        for (i in 0..mXTileCount) {
            val innerArray = ArrayList<Integer>()
            for (j in 0..mYTileCount) {
                innerArray.add(Integer(0))
            }
            mTileGrid.add(innerArray)
        }
        clearTiles()
    }

    public open fun loadTile(key: Int, tile: Drawable): Unit {
        var bitmap: Bitmap? = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888)
        var canvas: Canvas? = Canvas(bitmap)
        tile.setBounds(0, 0, mTileSize, mTileSize)
        tile.draw(canvas)
        mTileArray.set(key, bitmap)
    }

    public open fun clearTiles(): Unit {
        for (x in 0..mXTileCount ) {
            for (y in 0..mYTileCount) {
                setTile(0, x, y)
            }
        }
    }
    public open fun setTile(tileindex: Int, x: Int, y: Int): Unit {
        if (mTileGrid.size() > x && mTileGrid.get(x).size() > y) {
            mTileGrid.get(x).set(y, Integer(tileindex))
        } else {
            println("INDEX OUT OF BOUND " + x + " " + y + " " + tileindex)
        }
    }
    public override fun onDraw(canvas: Canvas?): Unit {
        super<View>.onDraw(canvas)
        for (x in 0..mXTileCount) {
            for (y in 0..mYTileCount) {
                if (mTileGrid.get(x).get(y) > 0) {
                    val intValue = mTileGrid.get(x).get(y).intValue()
                    val bitmap = mTileArray.get(intValue)
                    if (bitmap == null) {
                        return
                    }
                    canvas?.drawBitmap(bitmap,
                            (mXOffset + x * mTileSize).toFloat(),
                            (mYOffset + y * mTileSize).toFloat(),
                            mPaint)
                }
            }
        }
    }

}