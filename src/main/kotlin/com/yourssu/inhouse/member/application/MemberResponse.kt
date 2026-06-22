package com.yourssu.inhouse.member.application

import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberHistory
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberStatus
import java.time.LocalDate

data class MemberResponse(
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
    val state: String,
    val isOnLeave: Boolean? = null,
    val grade: Int? = null,
    val isDuesPaid: Boolean? = null,
    val inActiveReason: String? = null,
    val expectedReturnSemester: String? = null,
    val completedSemester: String? = null,
    val withdrawnSemester: String? = null,
    val histories: List<MemberHistoryDto>,
    val note: String? = null,
)

fun Member.toResponse(): MemberResponse {
    val mappedHistories = histories.map { it.toDto() }
    return when (val s = status) {
        is MemberStatus.Active -> MemberResponse(
            id = id, name = name, nickname = nickname, nicknameKo = nicknameKo,
            email = email, phoneNumber = phoneNumber, department = department,
            studentId = studentId, birthDate = birthDate, joinSemester = joinSemester,
            position = position, parts = parts, histories = mappedHistories, note = note,
            state = "active", isOnLeave = s.isOnLeave, grade = s.grade, isDuesPaid = s.isDuesPaid,
        )
        is MemberStatus.Inactive -> MemberResponse(
            id = id, name = name, nickname = nickname, nicknameKo = nicknameKo,
            email = email, phoneNumber = phoneNumber, department = department,
            studentId = studentId, birthDate = birthDate, joinSemester = joinSemester,
            position = position, parts = parts, histories = mappedHistories, note = note,
            state = "inactive", inActiveReason = s.inActiveReason, expectedReturnSemester = s.expectedReturnSemester,
        )
        is MemberStatus.Completed -> MemberResponse(
            id = id, name = name, nickname = nickname, nicknameKo = nicknameKo,
            email = email, phoneNumber = phoneNumber, department = department,
            studentId = studentId, birthDate = birthDate, joinSemester = joinSemester,
            position = position, parts = parts, histories = mappedHistories, note = note,
            state = "completed", completedSemester = s.completedSemester,
        )
        is MemberStatus.Withdrawn -> MemberResponse(
            id = id, name = name, nickname = nickname, nicknameKo = nicknameKo,
            email = email, phoneNumber = phoneNumber, department = department,
            studentId = studentId, birthDate = birthDate, joinSemester = joinSemester,
            position = position, parts = parts, histories = mappedHistories, note = note,
            state = "withdrawn", withdrawnSemester = s.withdrawnSemester,

        )
    }
}

private fun MemberHistory.toDto() = MemberHistoryDto(semester = semester, state = state)
