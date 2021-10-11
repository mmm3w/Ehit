package com.mitsuki.ehit.model.ehparser

import com.mitsuki.ehit.crutch.network.Url
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Matcher {
    val ID_TOKEN: Pattern by lazy { Pattern.compile("(\\d+)/([0-9a-f]{10})(?:[^0-9a-f]|\$)") }
    val RATING: Pattern by lazy { Pattern.compile("background-position:(\\-?\\d+)px (\\-?\\d+)px") }
    val DETAIL: Pattern by lazy {
        Pattern.compile(
            "var gid = (\\d+);.+?var token = \"([a-f0-9]+)\";.+?var apiuid = ([\\-\\d]+);.+?var apikey = \"([a-f0-9]+)\";",
            Pattern.DOTALL
        )
    }
    val TORRENT: Pattern by lazy {
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Torrent Download \\( (\\d+) \\)</a>")
    }

    val ARCHIVE: Pattern by lazy {
        Pattern.compile("<a[^<>]*onclick=\"return popUp\\('([^']+)'[^)]+\\)\">Archive Download</a>")
    }

    val DETAIL_COVER: Pattern by lazy {
        Pattern.compile("width:(\\d+)px; height:(\\d+)px.+?url\\((.+?)\\)")
    }

    val NUMBER: Pattern by lazy { Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])") }

    val NORMAL_PREVIEW: Pattern by lazy {
        Pattern.compile("<div class=\"gdtm\"[^<>]*><div[^<>]*width:(\\d+)[^<>]*height:(\\d+)[^<>]*\\((.+?)\\)[^<>]*-(\\d+)px[^<>]*><a[^<>]*href=\"(.+?)\"[^<>]*><img alt=\"([\\d,]+)\"")
    }

    val LARGE_PREVIEW: Pattern by lazy {
        Pattern.compile("<div class=\"gdtl\".+?<a href=\"(.+?)\"><img alt=\"([\\d,]+)\".+?src=\"(.+?)\"")
    }


    val PAGER_INDEX: Pattern by lazy {
        Pattern.compile("<td class=\"ptds\"><a.*?>(\\d+)</a></td>") //当前页码
    }
    val PAGER_TOTAL_SIZE: Pattern by lazy {
        Pattern.compile("<tr><td.*?>Length:</td><td.*?>(\\d+) pages</td></tr>") //总数
    }

    val LOGIN_INFO: Pattern by lazy {
        Pattern.compile("<p>You are now logged in as: (.+?)<")
    }


    //分页的基本信息
    //group1:prevKey
    //group3:index
    //group6:nextKey
    val PAGER_INFO: Pattern by lazy {
        Pattern.compile("(\\d+)?(</a>)?</td><td class=\"ptds\"><a.*?>(\\d+)</a></td>(<td.*?>)?(<a.*?>)?(\\d+)?")
    }

    val PREVIEW_IMG_URL: Pattern by lazy { Pattern.compile("<img[^>]*src=\"([^\"]+)\" style") }

    val PREVIEW_RELOAD_KEY: Pattern by lazy { Pattern.compile("onclick=\"return nl\\('([^\\)]+)'\\)") }
    val PREVIEW_DOWNLOAD_URL: Pattern by lazy { Pattern.compile("<a href=\"([^\"]+)fullimg.php([^\"]+)\">") }

    val PREVIEW_PAGE_TO_TOKEN: Pattern by lazy { Pattern.compile("${Url.currentDomain}/s/([0-9a-f]{10})/(\\d+)-(\\d+)") }

    val WEB_COMMENT_DATE_FORMAT: DateFormat by lazy {
        SimpleDateFormat("dd MMMMM yyyy, HH:mm", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
    }

}