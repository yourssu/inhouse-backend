package com.yourssu.inhouse.member.application

import com.yourssu.inhouse.member.business.CreateMemberCommand
import com.yourssu.inhouse.member.business.UpdateMemberCommand
import com.yourssu.inhouse.member.implement.MemberHistory
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberStatus
import java.time.LocalDate

data class MemberHistoryDto(
    val semester: String,
    val state: String,
) {
    fun toDomain() = MemberHistory(semester = semester, state = state)
}

private fun statusFromFields(
    state: String,
    isOnLeave: Boolean?,
    grade: Int?,
    isDuesPaid: Boolean?,
    inActiveReason: String?,
    expectedReturnSemester: String?,
    completedSemester: String?,
    withdrawnSemester: String?,
): MemberStatus = when (state) {
    "active" -> MemberStatus.Active(
        isOnLeave = requireNotNull(isOnLeave) { "isOnLeave is required for active status" },
        grade = requireNotNull(grade) { "grade is required for active status" },
        isDuesPaid = requireNotNull(isDuesPaid) { "isDuesPaid is required for active status" },
    )
    "inactive" -> MemberStatus.Inactive(
        inActiveReason = requireNotNull(inActiveReason) { "inActiveReason is required for inactive status" },
        expectedReturnSemester = requireNotNull(expectedReturnSemester) { "expectedReturnSemester is required for inactive status" },
    )
    "completed" -> MemberStatus.Completed(
        completedSemester = requireNotNull(completedSemester) { "completedSemester is required for completed status" },
    )
    "withdrawn" -> MemberStatus.Withdrawn(
        withdrawnSemester = requireNotNull(withdrawnSemester) { "withdrawnSemester is required for withdrawn status" },
    )
    else -> throw IllegalArgumentException("Unknown status state: $state")
}

data class CreateMemberRequest(
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
    val note: String? = null,
) {
    fun toCommand() = CreateMemberCommand(
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
        status = statusFromFields(state, isOnLeave, grade, isDuesPaid, inActiveReason, expectedReturnSemester, completedSemester, withdrawnSemester),
        note = note,
    )
}

data class UpdateMemberRequest(
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
    val histories: List<MemberHistoryDto> = emptyList(),
    val note: String? = null,
) {
    fun toCommand() = UpdateMemberCommand(
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
        status = statusFromFields(state, isOnLeave, grade, isDuesPaid, inActiveReason, expectedReturnSemester, completedSemester, withdrawnSemester),
        histories = histories.map { it.toDomain() },
        note = note,
    )
}
