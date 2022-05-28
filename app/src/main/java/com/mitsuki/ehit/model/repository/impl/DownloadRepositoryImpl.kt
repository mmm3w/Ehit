package com.mitsuki.ehit.model.repository.impl

import com.mitsuki.armory.httprookie.get
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.convert.ImageDownloadConcert
import com.mitsuki.ehit.model.repository.DownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    val client: OkHttpClient,
) : DownloadRepository {

    override suspend fun downloadImage(
        url: String,
        folder: File,
        name: String
    ): RequestResult<File> {
        return withContext(Dispatchers.IO) {
            if (folder.exists() && folder.isFile) {
                RequestResult.Fail(IllegalStateException("can not create folder"))
            } else {
                val result = client.get<File>(url) {
                    convert = ImageDownloadConcert(File(folder, name))
                }.execute()

                try {
                    when (result) {
                        is Response.Success<File> -> RequestResult.Success(result.requireBody())
                        is Response.Fail<*> -> throw result.throwable
                    }
                } catch (inner: Throwable) {
                    RequestResult.Fail(inner)
                }
            }
        }
    }

}