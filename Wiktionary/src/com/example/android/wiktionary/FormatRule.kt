package com.example.android.wiktionary

import java.util.regex.Pattern

class FormatRule(var pattern: String, var mReplaceWith: String, var flags: Int = 0) {
    var mPattern: Pattern = init()

    fun init(): Pattern {
       return Pattern.compile(pattern, flags).sure()
    }

    /**
    * Apply this formatting rule to the given input string, and return the
    * resulting new string.
    */
    public fun apply(val input: String): String {
        val m = mPattern.matcher(input)
        return m?.replaceAll(mReplaceWith).sure()
    }

}