package com.example.android.wiktionary

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.InputStream
import android.util.Log

open class SimpleWikiHelper {

    class object {
        var instance = SimpleWikiHelper()
    }

    val TAG = "SimpleWikiHelper"

    val WIKTIONARY_PAGE =
    "http://en.wiktionary.org/w/api.php?action=query&prop=revisions&titles=%s&" +
    "rvprop=content&format=json%s"

    val WIKTIONARY_EXPAND_TEMPLATES = "&rvexpandtemplates=true"

    val HTTP_STATUS_OK = 200

    val sBuffer = ByteArray(512)

    var sUserAgent : String? = null

    public fun prepareUserAgent(val context : Context) {
        try {
            // Read package name and version number from manifest
            val manager = context.getPackageManager()
            val info = manager?.getPackageInfo(context.getPackageName(), 0)
            sUserAgent = java.lang.String.format(context.getString(0x7f060003)!!,
                    info?.packageName, info?.versionName)

        } catch(e : NameNotFoundException) {
            Log.e(TAG, "Couldn't find package information in PackageManager", e)
        }
    }

    public fun getPageContent(val title : String, val expandTemplates : Boolean) : String? {
        // Encode page title and expand templates if requested
        val encodedTitle = Uri.encode(title)
        val expandClause = if (expandTemplates) WIKTIONARY_EXPAND_TEMPLATES else ""

        // Query the API for content
        val content = getUrlContent(java.lang.String.format(WIKTIONARY_PAGE,
                encodedTitle, expandClause))
        try {
            // Drill into the JSON response to find the content body
            val response = JSONObject(content)
            val query = response.getJSONObject("query")
            val pages = query?.getJSONObject("pages")
            val page = pages?.getJSONObject(pages?.keys()?.next() as String)
            val revisions = page?.getJSONArray("revisions")
            val revision = revisions?.getJSONObject(0)
            return revision?.getString("*")!!
        } catch (e : JSONException) {
            println(content)
            return null
        }
    }

    /**
    * Pull the raw text content of the given URL. This call blocks until the
    * operation has completed, and is synchronized because it uses a shared
    * buffer {@link #sBuffer}.
    *
    * @param url The exact URL to request.
    * @return The raw content returned by the server.
    * @throws ApiException If any connection or server error occurs.
    */
    public fun getUrlContent(val url : String) : String? {
        // Create client and set our specific user-agent string
        val  client = DefaultHttpClient()
        val request = HttpGet(url)
        request.setHeader("User-Agent", sUserAgent)

        try {
            val response = client.execute(request)!!

            // Check if server response is valid
            val status = response.getStatusLine()
            if (status?.getStatusCode() != 200) {
                Log.e("Invalid response from server: " + status.toString(), "")
                return null
            }

            // Pull content stream from response
            val entity = response.getEntity()
            val inputStream = entity?.getContent()

            val content = ByteArrayOutputStream()

            // Read response into a buffered stream
            var readBytes = inputStream?.read(sBuffer)!!
            while (readBytes != - 1) {
                content.write(sBuffer, 0, readBytes)
                readBytes = inputStream?.read(sBuffer)!!
            }

            // Return result from buffered stream
            return content.toString()

        } catch (e : IOException ) {
            Log.e("Problem communicating with API", "", e)
            return null
        }
    }
}