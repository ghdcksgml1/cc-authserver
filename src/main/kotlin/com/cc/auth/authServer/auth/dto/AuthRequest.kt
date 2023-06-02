package com.cc.auth.authServer.auth.dto

data class AuthRequest(
    val email: String,
    val platformId: String
)