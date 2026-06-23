package com.yourssu.inhouse.auth.application

data class SignUpRequest(val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)
