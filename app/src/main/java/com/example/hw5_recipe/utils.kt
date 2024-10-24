package com.example.hw5_recipe

import android.text.Html
import android.text.Spanned

fun String.stripHtml(): String {
    val spanned: Spanned = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    return spanned.toString()
}