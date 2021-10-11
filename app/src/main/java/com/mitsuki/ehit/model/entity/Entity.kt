package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.model.entity.db.GalleryCommentCache

fun GalleryCommentCache.toComment() =
    Comment(
        cid,
        user,
        content,
        postTime,
        lastEditedTime,
        score,
        editable,
        voteEnable,
        voteState,
        voteInfo
    )

fun Comment.toGalleryCommentCache(gid: Long, token: String) =
    GalleryCommentCache(
        gid,
        token,
        id,
        user,
        text,
        postTime,
        lastEditedTime,
        score,
        editable,
        voteEnable,
        voteState,
        voteInfo
    )
