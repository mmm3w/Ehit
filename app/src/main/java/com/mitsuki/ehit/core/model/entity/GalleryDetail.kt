package com.mitsuki.ehit.core.model.entity

import com.mitsuki.ehit.being.exception.ParseException
import com.mitsuki.ehit.core.model.ehparser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

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
    val images: PageInfo<ImageSource>
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
        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(content: String?): GalleryDetail {
            if (content.isNullOrEmpty()) throw ParseException("未请求到数据")

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryDetail

        if (gid != other.gid) return false
        if (token != other.token) return false
        if (apiUID != other.apiUID) return false
        if (apiKey != other.apiKey) return false
        if (torrentUrl != other.torrentUrl) return false
        if (torrentCount != other.torrentCount) return false
        if (archiveUrl != other.archiveUrl) return false
        if (detailThumb != other.detailThumb) return false
        if (title != other.title) return false
        if (titleJP != other.titleJP) return false
        if (category != other.category) return false
        if (uploader != other.uploader) return false
        if (posted != other.posted) return false
        if (parent != other.parent) return false
        if (visible != other.visible) return false
        if (language != other.language) return false
        if (size != other.size) return false
        if (pagesStr != other.pagesStr) return false
        if (favorite != other.favorite) return false
        if (ratingCount != other.ratingCount) return false
        if (rating != other.rating) return false
        if (favoriteName != other.favoriteName) return false
        if (!tagSet.contentEquals(other.tagSet)) return false
        if (commentSet != other.commentSet) return false
        if (previewPages != other.previewPages) return false
        if (images != other.images) return false
        if (categoryColor != other.categoryColor) return false
        if (pages != other.pages) return false
        if (favoriteCount != other.favoriteCount) return false
        if (isFavorited != other.isFavorited) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + apiUID.hashCode()
        result = 31 * result + apiKey.hashCode()
        result = 31 * result + torrentUrl.hashCode()
        result = 31 * result + torrentCount
        result = 31 * result + archiveUrl.hashCode()
        result = 31 * result + detailThumb.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + titleJP.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + uploader.hashCode()
        result = 31 * result + posted.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + visible.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + pagesStr.hashCode()
        result = 31 * result + favorite.hashCode()
        result = 31 * result + ratingCount
        result = 31 * result + rating.hashCode()
        result = 31 * result + (favoriteName?.hashCode() ?: 0)
        result = 31 * result + tagSet.contentHashCode()
        result = 31 * result + commentSet.hashCode()
        result = 31 * result + previewPages
        result = 31 * result + images.hashCode()
        result = 31 * result + categoryColor
        result = 31 * result + pages
        result = 31 * result + favoriteCount
        result = 31 * result + isFavorited.hashCode()
        return result
    }
}


