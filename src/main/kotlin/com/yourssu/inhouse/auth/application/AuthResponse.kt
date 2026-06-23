package com.yourssu.inhouse.auth.application

data class AuthResponse(
    val token: String,
    val memberId: Long,
    val email: String,
)
