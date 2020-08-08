package com.mitsuki.ehit.mvvm.model.entity

import com.mitsuki.ehit.mvvm.model.ehparser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.Exception

@Suppress("ArrayInDataClass")
data class GalleryDetail(
    val gid: Long,
    val token: String,
    val apiUID: String,
    val apiKey: String,
    val torrentUrl: String = "",
    val torrentCount: Int = 0,
    val archiveUrl: String = "",
    val detailThumb: String,
    val title: String,
    val titleJP: String,
    val category: String = "Unknow",
    val uploader: String,
    val posted: String,
    val parent: String,
    val visible: String,
    val language: String,
    val size: String,
    val pagesStr: String,
    val favorite: String,
    val ratingCount: Int,
    val rating: Float,
    val favoriteName: String? = null,
    val tagSet: Array<TagSet>,
    val commentSet: CommentSet,
    val previewPages: Int,
    val images: ArrayList<ImageSource>
) {
    val categoryColor: Int = Category.getColor(category)
    val pages = pagesStr.matchNumber("1").toInt()
    val favoriteCount = when (favorite) {
        "Never" -> 0
        "Once" -> 1
        else -> favorite.matchNumber().toInt()
    }

    val isFavorited = favoriteName != null

    companion object {
        suspend fun parseCoroutines(content: String?): GalleryDetail? {
            return withContext(Dispatchers.Default) { parse(content) }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(content: String?): GalleryDetail? {
            if (content.isNullOrEmpty()) return null

            val detail = content.parseDetail()
            val gid = detail[0].toLong()
            val token = detail[1]
            val apiUID = detail[2]
            val apiKey = detail[3]

            val torrent = content.parseTorrent()
            val torrentCount = torrent[1].toInt()
            val torrentUrl = torrent[0].htmlEscape()

            val archiveUrl = content.parseArchive().htmlEscape().trim()

            val document = Jsoup.parse(content)
            val gmNode = document.byClassFirst("gm")

            val detailThumb =
                gmNode.byId("gd1").child(0).attr("style").parseDetailThumb()
            val title = gmNode.byId("gn").text()
            val titleJP = gmNode.byId("gj").text()
            val category = gmNode.byId("gdc").child(0).text()
            val uploader = gmNode.byId("gdn").text()

            val infoArray = Array(7) { "" }
            val detailBlock = gmNode.byId("gdd").child(0).child(0).children()
            for (item in detailBlock) {
                if (item.children().size < 2) continue
                val itemText = item.child(0).text()
                when {
                    itemText.startsWith("Posted") -> {
                        infoArray[0] = item.child(1).ownText()
                    }
                    itemText.startsWith("Parent") -> {
                        infoArray[1] =
                            item.child(1).children().first()?.run { attr("href") } ?: ""
                    }
                    itemText.startsWith("Visible") -> {
                        infoArray[2] = item.child(1).ownText()
                    }
                    itemText.startsWith("Language") -> {
                        infoArray[3] = item.child(1).ownText()
                    }
                    itemText.startsWith("File Size") -> {
                        infoArray[4] = item.child(1).ownText()
                    }
                    itemText.startsWith("Length") -> {
                        infoArray[5] = item.child(1).ownText()
                    }
                    itemText.startsWith("Favorited") -> {
                        infoArray[6] = item.child(1).ownText()
                    }
                }
            }

            val ratingCount = gmNode.getElementById("rating_count")?.run { text().toInt() } ?: 0

            val ratingNode = gmNode.getElementById("rating_label")?.let {
                with(it.text()) { if ("Not Yet Rated" == this) -1f else matchNumber("-1").toFloat() }
            } ?: -1f

            val gdfNode = gmNode.byId("gdf")
            val favoriteName =
                if (!gdfNode.text().contains("Add to Favorites")) gdfNode.text() else null

            val tagSetArray: Array<TagSet> =
                document.getElementById("taglist")?.let { tagList ->
                    if (tagList.childrenSize() < 1) {
                        emptyArray()
                    } else {
                        tagList.child(0)?.child(0)?.children()
                            ?.let { Array(it.size) { number -> TagSet.parse(it[number]) } }
                    }
                } ?: emptyArray()


            val commentSet = CommentSet.parse(document.byId("cdiv"))

            val previewPages =
                with(document.byClassFirst("ptt").child(0).child(0).children()) {
                    get(size - 2).text().toInt()
                }

            val images = ImageSource.parse(content)

            return GalleryDetail(
                gid,
                token,
                apiUID,
                apiKey,
                torrentUrl,
                torrentCount,
                archiveUrl,
                detailThumb,
                title,
                titleJP,
                category,
                uploader,
                infoArray[0],
                infoArray[1],
                infoArray[2],
                infoArray[3],
                infoArray[4],
                infoArray[5],
                infoArray[6],
                ratingCount,
                ratingNode,
                favoriteName,
                tagSetArray,
                commentSet,
                previewPages,
                images
            )
        }

    }

}


