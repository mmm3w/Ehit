package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.const.ParseError
import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.Matcher
import com.mitsuki.ehit.model.ehparser.byClassFirst
import com.mitsuki.ehit.model.ehparser.byClassFirstIgnoreError
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

class Comment(
    val id: Long,
    val user: String,
    val text: String,
    val postTime: Long,
    val lastEditedTime: Long,
    var score: String,
    val editable: Boolean,
    val voteEnable: Boolean,
    var voteState: Int,
    val voteInfo: String,
) {
    companion object {
        fun parse(element: Element): Pair<Array<Comment>, Boolean> {
            val comments = with(element.getElementsByClass("c1")) {
                Array(size) { number -> parseItem(this[number]) }
            }

            var hasMore = false
            NodeTraversor.traverse(object : NodeVisitor {
                override fun head(node: Node?, depth: Int) {
                    if (node is Element && node.text() == "click to show all") hasMore = true
                }

                override fun tail(node: Node?, depth: Int) {}
            }, element.getElementById("chd"))
            return comments to hasMore
        }

        private fun parseItem(element: Element): Comment {
            val id =
                element.previousElementSibling().attr("name").substring(1).toLong()

            val c3Node = element.byClassFirst("c3", "c3 node".prefix())

            val user = c3Node.child(0).text()

            val text = element.byClassFirst("c6", "comment text (c6 node text)".prefix()).html()

            val postTimeStr =
                with(c3Node.ownText()) { substring("Posted on ".length, length - " by:".length) }
            val postTime =
                Matcher.WEB_COMMENT_DATE_FORMAT.parse(postTimeStr)?.time
                    ?: throw ParseThrowable("Parse comment: Date parse error")

            val lastEditedTime =
                element.byClassFirstIgnoreError("c8")?.children()?.first()?.text()?.run {
                    Matcher.WEB_COMMENT_DATE_FORMAT.parse(this)?.time
                } ?: -1L

            val score =
                element.byClassFirstIgnoreError("c5")?.children()?.first()?.text() ?: ""

            var editable = false

            var voteFlag = 0
            var voteState = 0

            element.byClassFirstIgnoreError("c4")?.children()?.forEach {
                when (it?.text()) {
                    "Edit" -> editable = true
                    "Vote+" -> {
                        voteFlag = voteFlag or 1
                        if (it.attr("style")?.trim()?.isNotEmpty() == true) {
                            voteState = 1
                        }
                    }
                    "Vote-" -> {
                        voteFlag = voteFlag or 2
                        if (it.attr("style")?.trim()?.isNotEmpty() == true) {
                            voteState = -1
                        }
                    }
                }
            }

            val voteInfo =
                element.byClassFirst("c7", "comment info (c7 node)".prefix()).text().trim()

            return Comment(
                id,
                user,
                text,
                postTime,
                lastEditedTime,
                score,
                editable,
                voteFlag == 3,
                voteState,
                voteInfo
            )
        }

        private fun String.prefix(): String = String.format(ParseError.GALLERY_COMMENT, this)
    }

    override fun equals(other: Any?): Boolean {
        return other is Comment &&
                id == other.id &&
                user == other.user &&
                text == other.text &&
                postTime == other.postTime &&
                lastEditedTime == other.lastEditedTime &&
                score == other.score &&
                editable == other.editable &&
                voteState == other.voteState &&
                voteInfo == other.voteInfo
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + postTime.hashCode()
        result = 31 * result + lastEditedTime.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + editable.hashCode()
        result = 31 * result + voteState
        result = 31 * result + voteInfo.hashCode()
        return result
    }
}