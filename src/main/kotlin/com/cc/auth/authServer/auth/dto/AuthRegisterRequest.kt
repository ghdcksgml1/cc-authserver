package com.proseed.api.auth.dto

data class AuthRegisterRequest(
    val platformId: String,
    val platformType: String,
    val company: String
)