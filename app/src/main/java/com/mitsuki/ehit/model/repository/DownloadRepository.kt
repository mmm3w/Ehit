package com.mitsuki.ehit.model.repository

import com.mitsuki.ehit.crutch.network.RequestResult
import java.io.File

interface DownloadRepository {

    suspend fun downloadImage(url: String, folder: File, name: String): RequestResult<File>

}