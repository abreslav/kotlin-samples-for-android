package com.example.android.wiktionary

import java.util.regex.Pattern
import java.util.ArrayList
import org.json.JSONObject
import org.json.JSONException
import java.text.ParseException
import java.util.HashSet
import android.text.TextUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import com.example.android.wiktionary.SimpleWikiHelper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.Format
import kotlin.dom.get

/**
 * Created with IntelliJ IDEA.
 * User: Natalia.Ukhorskaya
 * Date: 5/5/12
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */


/**
    * HTML style sheet to include with any {@link #formatWikiText(String)} HTML
    * results. It formats nicely for a mobile screen, and hides some content
    * boxes to keep things tidy.
    */
val STYLE_SHEET = "<style>h2 {font-size:1.2em;font-weight:normal;} " +
"a {color:#6688cc;} ol {padding-left:1.5em;} blockquote {margin-left:0em;} " +
".interProject, .noprint {display:none;} " +
"li, blockquote {margin-top:0.5em;margin-bottom:0.5em;}</style>";

/**
* Pattern of section titles we're interested in showing. This trims out
* extra sections that can clutter things up on a mobile screen.
*/
val sValidSections =
Pattern.compile("(verb|noun|adjective|pronoun|interjection)", Pattern.CASE_INSENSITIVE);

/**
* Pattern that can be used to split a returned wiki page into its various
* sections. Doesn't treat children sections differently.
*/
val sSectionSplit =
Pattern.compile("^=+(.+?)=+.+?(?=^=)", (Pattern.MULTILINE.xor(Pattern.DOTALL)));
//        Pattern.compile("^=+(.+?)=+.+?(?=^=)", Pattern.MULTILINE | Pattern.DOTALL);

/**
* When picking random words in {@link #getRandomWord()}, we sometimes
* encounter special articles or templates. This pattern ignores any words
* like those, usually because they have ":" or other punctuation.
*/
val sInvalidWord = Pattern.compile("[^A-Za-z0-9 ]");

/**
* {@link Uri} authority to use when creating internal links.
*/
val WIKI_AUTHORITY = "wiktionary";

/**
* {@link Uri} host to use when creating internal links.
*/
val WIKI_LOOKUP_HOST = "lookup";

/**
* Mime-type to use when showing parsed results in a {@link WebView}.
*/
val MIME_TYPE = "text/html";

/**
* Encoding to use when showing parsed results in a {@link WebView}.
*/
val ENCODING = "utf-8";

/**
* {@link Uri} to use when requesting a random page.
*/
val WIKTIONARY_RANDOM =
"http://en.wiktionary.org/w/api.php?action=query&list=random&format=json";

/**
* Fake section to insert at the bottom of a wiki response before parsing.
* This ensures that {@link #sSectionSplit} will always catch the last
* section, as it uses section headers in its searching.
*/
val STUB_SECTION = "\n=Stub section=";

/**
* Number of times to try finding a random word in {@link #getRandomWord()}.
* These failures are usually when the found word fails the
* {@link #sInvalidWord} test, or when a network error happens.
*/
val RANDOM_TRIES = 3;

val sFormatRules : ArrayList<FormatRule> = init()

fun init() : ArrayList<FormatRule> {
    // Format header blocks and wrap outside content in ordered list
    val result = ArrayList<FormatRule>()
    result.add(FormatRule("^=+(.+?)=+", "</ol><h2>$1</h2><ol>",
            Pattern.MULTILINE));

    // Indent quoted blocks, handle ordered and bullet lists
    result.add(FormatRule("^#+\\*?:(.+?)$", "<blockquote>$1</blockquote>",
            Pattern.MULTILINE));
    result.add(FormatRule("^#+:?\\*(.+?)$", "<ul><li>$1</li></ul>",
            Pattern.MULTILINE));
    result.add(FormatRule("^#+(.+?)$", "<li>$1</li>",
            Pattern.MULTILINE));

    // Add internal links
    result.add(FormatRule("\\[\\[([^:\\|\\]]+)\\]\\]",
            String.format("<a href=\"%s://%s/$1\">$1</a>", WIKI_AUTHORITY, WIKI_LOOKUP_HOST).sure()));
    result.add(FormatRule("\\[\\[([^:\\|\\]]+)\\|([^\\]]+)\\]\\]",
            String.format("<a href=\"%s://%s/$1\">$2</a>", WIKI_AUTHORITY, WIKI_LOOKUP_HOST).sure()));

    // Add bold and italic formatting
    result.add(FormatRule("'''(.+?)'''", "<b>$1</b>"));
    result.add(FormatRule("([^'])''([^'].*?[^'])''([^'])", "$1<i>$2</i>$3"));

    // Remove odd category links and convert remaining links into flat text
    result.add(FormatRule("(\\{+.+?\\}+|\\[\\[[^:]+:[^\\\\|\\]]+\\]\\]|" +
    "\\[http.+?\\]|\\[\\[Category:.+?\\]\\])", "", Pattern.MULTILINE));
    //            "\\[http.+?\\]|\\[\\[Category:.+?\\]\\])", "", Pattern.MULTILINE | Pattern.DOTALL));
    result.add(FormatRule("\\[\\[([^\\|\\]]+\\|)?(.+?)\\]\\]", "$2",
            Pattern.MULTILINE));

    return result
}

class ExtendedWikiHelperKt() : SimpleWikiHelper() {
    /**
    * Query the Wiktionary API to pick a random dictionary word. Will try
    * multiple times to find a valid word before giving up.
    *
    * @return Random dictionary word, or null if no valid word was found.
    * @throws ApiException If any connection or server error occurs.
    * @throws ParseException If there are problems parsing the response.
    */
    public fun getRandomWord() : String? {
        // Keep trying a few times until we find a valid word
        var tries = 0;
        while (tries++ < RANDOM_TRIES) {
            // Query the API for a random word
            val content = getUrlContent(WIKTIONARY_RANDOM);
            try {
                // Drill into the JSON response to find the returned word
                val response = JSONObject(content);
                val query = response.getJSONObject("query");
                val random = query?.getJSONArray("random");
                val word = random?.getJSONObject(0);
                val foundWord = word?.getString("title");

                // If we found an actual word, and it wasn't rejected by our invalid
                // filter, then accept and return it.
                if (foundWord != null &&
                !sInvalidWord?.matcher(foundWord)?.find().sure()) {
                    return foundWord;
                }
            } catch (e : JSONException) {
                throw IllegalArgumentException("Problem parsing API response", e);
            }
        }

        // No valid word found in number of tries, so return null
        return null;
    }

    /**
    * Format the given wiki-style text into formatted HTML content. This will
    * create headers, lists, internal links, and style formatting for any wiki
    * markup found.
    *
    * @param wikiText The raw text to format, with wiki-markup included.
    * @return HTML formatted content, ready for display in {@link WebView}.
    */
    public fun formatWikiText(var wikiText : String?) : String? {
        if (wikiText == null) {
            return null;
        }

        // Insert a fake last section into the document so our section splitter
        // can correctly catch the last section.
        wikiText = wikiText?.concat(STUB_SECTION);

        // Read through all sections, keeping only those matching our filter,
        // and only including the first entry for each title.
        val foundSections = HashSet<String>();
        val builder = StringBuilder();

        val sectionMatcher = sSectionSplit?.matcher(wikiText);
        while (sectionMatcher?.find().sure()) {
            val title = sectionMatcher?.group(1);
            if (!foundSections.contains(title) &&
            sValidSections?.matcher(title)?.matches().sure()) {
                val sectionContent = sectionMatcher?.group();
                foundSections.add(title.sure());
                builder.append(sectionContent);
            }
        }

        // Our new wiki text is the selected sections only
        wikiText = builder.toString();

        // Apply all formatting rules, in order, to the wiki text
        for (rule in sFormatRules) {
            wikiText = rule.apply(wikiText.sure());
        }

        // Return the resulting HTML with style sheet, if we have content left
        if (!TextUtils.isEmpty(wikiText)) {
            return STYLE_SHEET + wikiText;
        } else {
            return null;
        }
    }

}