package com.yourssu.inhouse.member.business

import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberHistory
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberStatus
import java.time.LocalDate

data class UpdateMemberCommand(
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
) {
    fun toDomain() = Member(
        id = 0L,
        name = name,
        nickname = nickname,
        nicknameKo = nicknameKo,
        email = email,
        phoneNumber = phoneNumber,
        department = department,
        studentId = studentId,
        birthDate = birthDate,
        joinSemester = joinSemester,
        position = position,
        parts = parts,
        status = status,
        histories = histories,
        note = note,
    )
}
