package com.mitsuki.ehit.core.model.ehparser

import java.util.regex.Pattern

object Matcher {
    val ID_TOKEN: Pattern = Pattern.compile("(\\d+)/([0-9a-f]{10})(?:[^0-9a-f]|\$)")
    val RATING: Pattern = Pattern.compile("var gid = (\\-?\\d+)px (\\-?\\d+)px")
    val DETAIL: Pattern = Pattern.compile(
        "var gid = (\\d+);.+?var token = \"([a-f0-9]+)\";.+?var apiuid = ([\\-\\d]+);.+?var apikey = \"([a-f0-9]+)\";",
        Pattern.DOTALL
    )
    val TORRENT: Pattern =
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Torrent Download \\( (\\d+) \\)</a>")


    val ARCHIVE: Pattern =
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Archive Download</a>")

    val DETAIL_COVER: Pattern =
        Pattern.compile("width:(\\d+)px; height:(\\d+)px.+?url\\((.+?)\\)")

    val NUMBER: Pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])")

    val NORMAL_PREVIEW: Pattern =
        Pattern.compile("<div class=\"gdtm\"[^<>]*><div[^<>]*width:(\\d+)[^<>]*height:(\\d+)[^<>]*\\((.+?)\\)[^<>]*-(\\d+)px[^<>]*><a[^<>]*href=\"(.+?)\"[^<>]*><img alt=\"([\\d,]+)\"")


    val PAGER_INDEX: Pattern =
        Pattern.compile("<td class=\"ptds\"><a.*?>(\\d+)</a></td>") //当前页码
    val PAGER_TOTAL_SIZE =
        Pattern.compile("<tr><td.*?>Length:</td><td.*?>(\\d+) pages</td></tr>") //总数

    //分页的基本信息
    //group1:prevKey
    //group3:index
    //group6:nextKey
    val PAGER_INFO: Pattern =
        Pattern.compile("(\\d+)?(</a>)?</td><td class=\"ptds\"><a.*?>(\\d+)</a></td>(<td.*?>)?(<a.*?>)?(\\d+)?")


}