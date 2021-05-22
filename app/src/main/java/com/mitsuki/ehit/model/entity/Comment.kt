package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.const.ParseError
import com.mitsuki.ehit.model.ehparser.byClassFirst
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

class Comment(val id: Int, val time: String, val user: String, val text: String) {
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
                element.previousElementSibling().attr("name").substring(1).toInt()

            //val c4Node = element.byClassFirst("c4") 这部分暂不解析

            val c3Node = element.byClassFirst("c3", "c3 node".prefix())

            val time =
                with(c3Node.ownText()) { substring("Posted on ".length, length - " by:".length) }

            val user = c3Node.child(0).text()

            val text = element.byClassFirst("c6", "comment text (c6 node text)".prefix()).html()

            return Comment(id, time, user, text)
        }

        private fun String.prefix(): String = String.format(ParseError.GALLERY_COMMENT, this)
    }
}