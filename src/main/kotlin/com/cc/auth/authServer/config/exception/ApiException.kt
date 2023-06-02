package com.cc.auth.authServer.config.exception

open class ApiException : RuntimeException {

    constructor(message: String) : super(message)
}