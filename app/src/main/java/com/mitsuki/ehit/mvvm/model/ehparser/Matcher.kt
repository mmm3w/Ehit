package com.mitsuki.ehit.mvvm.model.ehparser

import java.util.regex.Pattern

object Matcher {
    val ID_TOKEN = Pattern.compile("(\\d+)/([0-9a-f]{10})(?:[^0-9a-f]|\$)")
    val RATING = Pattern.compile("var gid = (\\-?\\d+)px (\\-?\\d+)px")
    val DETAIL = Pattern.compile(
        "var gid = (\\d+);.+?var token = \"([a-f0-9]+)\";.+?var apiuid = ([\\-\\d]+);.+?var apikey = \"([a-f0-9]+)\";",
        Pattern.DOTALL
    )
    val TORRENT =
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Torrent Download \\( (\\d+) \\)</a>")


    val ARCHIVE =
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Archive Download</a>")

    val DETAIL_COVER =
        Pattern.compile("width:(\\d+)px; height:(\\d+)px.+?url\\((.+?)\\)")

    val NUMBER = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])")
}