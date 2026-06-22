package com.yourssu.inhouse.member.implement

import java.time.LocalDate

data class Member(
    val id: Long,
    val name: String,
    val nickname: String,
    val nicknameKo: String,
    val email: String,
    val phoneNumber: String,
    val department: String,
    val studentId: String,
    val birthDate: LocalDate,
    val joinSemester: String,
    val position: MemberPosition,
    val parts: List<MemberPart>,
    val status: MemberStatus,
    val histories: List<MemberHistory>,
    val note: String? = null,
)