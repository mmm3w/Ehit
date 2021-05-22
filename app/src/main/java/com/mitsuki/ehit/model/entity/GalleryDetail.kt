package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.const.ParseError
import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.*
import com.mitsuki.ehit.model.entity.db.GalleryCommentCache
import com.mitsuki.ehit.model.entity.db.GalleryInfoCache
import com.mitsuki.ehit.model.entity.db.GalleryTagCache
import org.jsoup.Jsoup

data class GalleryDetail(
    val info: GalleryInfoCache,
    val tagGroup: Array<TagGroup>,
    val comments: Array<Comment>
) {
    val categoryColor: Int get() = Category.getColor(info.category)

    val pages: Int get() = info.pagesStr.matchNumber("1").toInt()

    val favoriteCount: Int
        get() = when (info.favorite) {
            "Never" -> 0
            "Once" -> 1
            else -> info.favorite.matchNumber().toInt()
        }

    val isFavorited: Boolean get() = info.favoriteName != null

    val tagCache: List<GalleryTagCache>
        get() = tagGroup.flatMap { group ->
            group.tags.map { tag -> GalleryTagCache(tag, group.groupName, info.gid, info.token) }
        }

    val commentCache: List<GalleryCommentCache>
        get() = comments.map {
            GalleryCommentCache(info.gid, info.token, it.id, it.time, it.user, it.text)
        }

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(content: String?): GalleryDetail {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")

            val detail = content.parseDetail("detail block".prefix())
            val gid = detail[0].toLong()
            val token = detail[1]
            val apiUID = detail[2]
            val apiKey = detail[3]

            val torrent = content.parseTorrent()
            val torrentCount = torrent[1].toInt()
            val torrentUrl = torrent[0].htmlEscape()

            val archiveUrl = content.parseArchive().htmlEscape().trim()

            val document = Jsoup.parse(content)
            val gmNode = document.byClassFirst("gm", "gm node".prefix())

            val detailThumb =
                gmNode.getElementById("gd1")?.child(0)?.attr("style")
                    ?.parseDetailThumb("detailThumb (gd1 node child0 style)".prefix())
                    ?: throw ParseThrowable("detailThumb (gd1 node child0 style)".prefix())


            val title = gmNode.getElementById("gn")?.text()
                ?: throw ParseThrowable("title (gn node text)".prefix())

            val titleJP = gmNode.getElementById("gj")?.text()
                ?: throw ParseThrowable("titleJP (gj node text)".prefix())

            val category = gmNode.getElementById("gdc")?.child(0)?.text()
                ?: throw ParseThrowable("category (gdc node child0 text)".prefix())

            val uploader = gmNode.getElementById("gdn")?.text()
                ?: throw ParseThrowable("uploader (gdn node text)".prefix())

            val infoArray = Array(7) { "" }

            val detailBlock = gmNode.getElementById("gdd").child(0).child(0).children()
                ?: throw ParseThrowable("detailBlock (gdd node)".prefix())

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

            val ratingCount =
                gmNode.getElementById("rating_count")?.run { text().toIntOrNull() } ?: 0

            val ratingNode = gmNode.getElementById("rating_label")?.let {
                with(it.text()) { if ("Not Yet Rated" == this) -1f else matchNumber("-1").toFloat() }
            } ?: -1f

            val gdfNode = gmNode.getElementById("gdf")
                ?: throw ParseThrowable("gdf node".prefix())

            val favoriteName =
                if (!gdfNode.text().contains("Add to Favorites")) gdfNode.text() else null

            val tagSetArray: Array<TagGroup> =
                document.getElementById("taglist")?.let { tagList ->
                    if (tagList.childrenSize() < 1) {
                        emptyArray()
                    } else {
                        tagList.child(0)?.child(0)?.children()
                            ?.let { Array(it.size) { number -> TagGroup.parse(it[number]) } }
                    }
                } ?: emptyArray()


            val commentData = Comment.parse(
                document.getElementById("cdiv") ?: throw ParseThrowable("cdiv node".prefix())
            )

            val previewPages =
                with(
                    document.byClassFirst("ptt", "ptt node".prefix()).child(0).child(0).children()
                ) {
                    get(size - 2).text().toInt()
                }

            val info = GalleryInfoCache(
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
                previewPages,
                commentData.second
            )


            return GalleryDetail(
                info,
                tagSetArray,
                commentData.first
            )
        }

        private fun String.prefix(): String = String.format(ParseError.GALLERY_DETAIL, this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is GalleryDetail &&
                info == other.info &&
                tagGroup.contentEquals(other.tagGroup) &&
                comments.contentEquals(other.comments)
    }

    override fun hashCode(): Int {
        var result = info.hashCode()
        result = 31 * result + tagGroup.contentHashCode()
        result = 31 * result + comments.contentHashCode()
        return result
    }


    fun obtainOperating(): GalleryDetailWrap.DetailPart {
        return GalleryDetailWrap.DetailPart(info.rating, info.ratingCount, pages)
    }

    fun obtainComments(): Array<Comment> {
        return Array(GalleryDetailWrap.MAX_COMMENT.coerceAtMost(comments.size)) { index -> comments[index] }
    }

    fun obtainCommentState(): GalleryDetailWrap.CommentState {
        return when (comments.size) {
            0 -> GalleryDetailWrap.CommentState.NoComments
            in 1..GalleryDetailWrap.MAX_COMMENT -> GalleryDetailWrap.CommentState.AllLoaded
            else -> GalleryDetailWrap.CommentState.MoreComments
        }
    }
}


