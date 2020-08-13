package com.mitsuki.ehit.core.model.ehparser

class ParseException : Exception {

    constructor() : super()

    constructor(msg: String) : super(msg)

    constructor(msg: String, cause: Throwable) : super(msg, cause)

    constructor(cause: Throwable) : super(cause)

    constructor(
        msg: String, cause: Throwable, enableSuppression: Boolean, writableStackTrace: Boolean
    ) : super(msg, cause, enableSuppression, writableStackTrace)

}