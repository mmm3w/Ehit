package com.mitsuki.ehit.model.repository

import com.mitsuki.ehit.crutch.network.RequestResult
import java.io.File

interface DownloadRepository {

    suspend fun downloadThumb(gid: Long, token: String): RequestResult<File>

    suspend fun downloadPage(gid: Long, token: String, index: Int): RequestResult<File>

    suspend fun downloadImage(url: String, folder: File, name: String): RequestResult<File>

}