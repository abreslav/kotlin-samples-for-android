package com.example.android.weatherlistwidget

/**
 * User: Natalia.Ukhorskaya
 */

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import java.util.ArrayList

open class WeatherDataPoint(val city: String, var degrees: Int) {
    public fun setDegree(i: Int) {
        degrees = i
    }
}

val sData: ArrayList<WeatherDataPoint> = ArrayList<WeatherDataPoint>()

public val COLUMNS: Columns = Columns()
public val CONTENT_URI: Uri = Uri.parse("content://com.example.android.weatherlistwidget.provider")!!

public class Columns() {
        public val ID: String = "_id"
        public val CITY: String = "city"
        public val TEMPERATURE: String = "temperature"
}

public open class WeatherDataProvider(): ContentProvider() {
    public override fun onCreate(): Boolean {
        sData.add(WeatherDataPoint("San Francisco", 13))
        sData.add(WeatherDataPoint("New York", 1))
        sData.add(WeatherDataPoint("Seattle", 7))
        sData.add(WeatherDataPoint("Boston", 4))
        sData.add(WeatherDataPoint("Miami", 22))
        sData.add(WeatherDataPoint("Toronto", -10))
        sData.add(WeatherDataPoint("Calgary", -13))
        sData.add(WeatherDataPoint("Tokyo", 8))
        sData.add(WeatherDataPoint("Kyoto", 11))
        sData.add(WeatherDataPoint("London", -1))
        sData.add(WeatherDataPoint("Nomanisan", 27))
        return true
    }
    public override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        println(uri.toString())
        assert {(uri?.getPathSegments()?.isEmpty()!!)}
        val c = MatrixCursor(array(COLUMNS.ID, COLUMNS.CITY, COLUMNS.TEMPERATURE))
        for (i in 0..sData.size() - 1) {
            val data = sData.get(i)
            c.addRow(array(Integer(i), data.city, Integer((data.degrees))))
        }
        return c
    }
    public override fun getType(uri: Uri?): String? {
        return "vnd.android.cursor.dir/vnd.weatherlistwidget.citytemperature"
    }
    public override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }
    public override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
    public override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        if (uri == null) {
            throw RuntimeException()
        }
        assert {((uri.getPathSegments()?.size())!! == 1)}
        val index = Integer.parseInt((uri.getPathSegments()?.get(0))!!)
        val c = MatrixCursor(array(COLUMNS.ID, COLUMNS.CITY, COLUMNS.TEMPERATURE))
        assert {(0 <= index && index < (sData.size()))}
        var data = sData.get(index)
        data.setDegree(values?.getAsInteger(COLUMNS.TEMPERATURE)!!)
        getContext()?.getContentResolver()?.notifyChange(uri, null)
        return 1
    }

    fun assert(f: () -> Boolean) {
        if (!f()) {
            throw AssertionError()
        }
    }
}


