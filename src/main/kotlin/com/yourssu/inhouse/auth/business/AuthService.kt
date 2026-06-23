package com.yourssu.inhouse.auth.business

import com.yourssu.inhouse.auth.application.AuthResponse
import com.yourssu.inhouse.auth.implement.JwtProvider
import com.yourssu.inhouse.member.storage.MemberJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberJpaRepository: MemberJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
) {
    @Transactional
    fun signUp(email: String, password: String): AuthResponse {
        val member = memberJpaRepository.findByEmail(email)
            ?: throw IllegalArgumentException("등록되지 않은 이메일입니다.")
        if (member.passwordHash != null) {
            throw AlreadyRegisteredException("이미 회원가입된 이메일입니다.")
        }
        member.passwordHash = passwordEncoder.encode(password)
        return AuthResponse(token = jwtProvider.generate(requireNotNull(member.id), member.email))
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): AuthResponse {
        val member = memberJpaRepository.findByEmail(email)
        val hash = member?.passwordHash
        if (member == null || hash == null || !passwordEncoder.matches(password, hash)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }
        return AuthResponse(token = jwtProvider.generate(requireNotNull(member.id), member.email))
    }
}
