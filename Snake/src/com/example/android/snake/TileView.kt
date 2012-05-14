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

private var mTileArray : Array<Bitmap?>? = null
private var mTileGrid : Array<IntArray?> = Array<IntArray?>(1){null}
private val mPaint : Paint? = Paint()

public var mTileSize : Int = 0
public var mXTileCount : Int = 0
public var mYTileCount : Int = 0
private var mXOffset : Int = 0
private var mYOffset : Int = 0

public open class TileView(val context : Context, val attrs : AttributeSet, val defStyle : Int) : View(context, attrs, defStyle) {

    public open fun resetTiles(tilecount : Int) : Unit {
        mTileArray = Array<Bitmap?>(tilecount) { null }

    }
    protected override fun onSizeChanged(w : Int, h : Int, oldw : Int, oldh : Int) : Unit {
        mXTileCount = (Math.floor((w / mTileSize).toDouble()) as Int)
        mYTileCount = (Math.floor((h / mTileSize).toDouble()) as Int)
        mXOffset = ((w - (mTileSize * mXTileCount)) / 2)
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2)
        mTileGrid = Array<IntArray?>(mXTileCount, {IntArray(mYTileCount)})
        clearTiles()
    }
    public open fun loadTile(key : Int, tile : Drawable?) : Unit {
        var bitmap : Bitmap? = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888)
        var canvas : Canvas? = Canvas(bitmap)
        tile?.setBounds(0, 0, mTileSize, mTileSize)
        tile?.draw(canvas)
        mTileArray?.set(key, bitmap)
    }
    public open fun clearTiles() : Unit {
        for (x in 0..mXTileCount - 1) {
            for (y in 0..mYTileCount - 1) {
                setTile(0, x, y)
            }
        }
    }
    public open fun setTile(tileindex : Int, x : Int, y : Int) : Unit {
        mTileGrid[x]?.set(y, tileindex)
    }
    public override fun onDraw(canvas : Canvas?) : Unit {
        super<View>.onDraw(canvas);
        for (x in 0..mXTileCount) {
            for (y in 0..mYTileCount) {
                if (mTileGrid.get(x)?.get(y)?.compareTo(0) == 0) {
                    canvas?.drawBitmap(mTileArray?.get(mTileGrid.get(x)?.get(y).sure()).sure(),
                            (mXOffset + x * mTileSize).toFloat(),
                            (mYOffset + y * mTileSize).toFloat(),
                            mPaint);
                }
            }
        }
    }

}