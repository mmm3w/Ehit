package com.mitsuki.ehit.model.entity

data class DownloadListInfo(
    val gid: Long,
    val token: String,
    val thumb: String,
    val title: String,
//    val timestamp: Long,
    val total: Int,
    val completed:Int
) {
    override fun equals(other: Any?): Boolean {
        return other is DownloadListInfo &&
                other.gid == gid &&
                other.token == token &&
                other.thumb == thumb &&
                other.title == title &&
//                other.timestamp == timestamp &&
                other.total == total
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + thumb.hashCode()
        result = 31 * result + title.hashCode()
//        result = 31 * result + timestamp.hashCode()
        result = 31 * result + total
        return result
    }
}