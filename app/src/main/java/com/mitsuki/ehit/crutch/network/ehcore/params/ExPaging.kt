package com.mitsuki.ehit.crutch.network.ehcore.params

class ExPaging {
    val pagingParams = Array<String?>(4) {
        null
    }

    companion object {
        fun obtain(): ExPaging {
            return ExPaging()
        }

        fun obtainNext(next: String, jump: String? = null, seek: String? = null): ExPaging {
            return obtain().apply {
                pagingParams[0] = next
                pagingParams[2] = jump
                pagingParams[3] = seek
            }
        }

        fun obtainPrev(prev: String, jump: String? = null, seek: String? = null): ExPaging {
            return obtain().apply {
                pagingParams[1] = prev
                pagingParams[2] = jump
                pagingParams[3] = seek
            }
        }
    }
}