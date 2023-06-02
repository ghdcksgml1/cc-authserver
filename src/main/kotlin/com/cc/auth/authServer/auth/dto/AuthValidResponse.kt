package com.cc.auth.authServer.auth.dto

data class AuthValidResponse(
    val id: Long,
    val email: String,
    val company: String
) {
}