package com.mitsuki.ehit.model.repository.impl

import android.content.Context
import coil.Coil
import coil.request.ImageRequest
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val mContext: Context,
    ) : DownloadRepository {
    override suspend fun downloadThumb(gid: Long, token: String): RequestResult<File> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadPage(gid: Long, token: String, index: Int): RequestResult<File> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadImage(
        url: String,
        folder: File,
        name: String
    ): RequestResult<File> {






        TODO("Not yet implemented")
    }

}