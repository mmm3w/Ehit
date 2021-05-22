package com.mitsuki.ehit.crutch

object MemoryCache {
//    private val mGalleryDetailCache: LruCache<Long, SparseArray<GalleryDetail>> = LruCache(10)
//    private val mGalleryImageTokenCache: LruCache<Long, SparseArray<String>> = LruCache(10)
//    private val mImagePageCache: LruCache<Long, SparseArray<GalleryPreview>> = LruCache(10)
//
//    @Synchronized
//    fun cacheGalleryDetail(gid: Long, index: Int, item: GalleryDetail) {
//        if (mGalleryDetailCache[gid] == null)
//            mGalleryDetailCache.put(gid, SparseArray())
//        mGalleryDetailCache[gid]?.put(index, item)
//    }
//
//    @Synchronized
//    fun getGalleryDetail(gid: Long, index: Int): GalleryDetail? {
//        return mGalleryDetailCache[gid]?.get(index)
//    }
//
//    @Synchronized
//    fun cacheImageToken(gid: Long, data: List<ImageSource>) {
//        for (item in data) {
//            cacheImageToken(gid, item.index, item.pToken)
//        }
//    }
//
//    fun cacheImageToken(gid: Long, index: Int, token: String) {
//        if (mGalleryImageTokenCache[gid] == null)
//            mGalleryImageTokenCache.put(gid, SparseArray())
//        mGalleryImageTokenCache[gid]?.put(index, token)
//    }
//
//    fun getImageToken(gid: Long, index: Int): String? {
//        return mGalleryImageTokenCache[gid]?.get(index)
//    }
//
//    @Synchronized
//    fun cacheImagePage(gid: Long, index: Int, item: GalleryPreview) {
//        if (mImagePageCache[gid] == null)
//            mImagePageCache.put(gid, SparseArray())
//        mImagePageCache[gid]?.put(index, item)
//    }
//
//    fun getImagePage(gid: Long, index: Int): GalleryPreview? {
//        return mImagePageCache[gid]?.get(index)
//    }

    @Volatile
    var detailPageSize = 0
}