package com.yourssu.inhouse.member.storage

import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberHistory
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberStatus

fun MemberJpaEntity.toDomain(): Member {
    val status = when (state) {
        "active" -> MemberStatus.Active(
            isOnLeave = requireNotNull(isOnLeave) { "active 상태에서 isOnLeave는 필수" },
            grade = requireNotNull(grade) { "active 상태에서 grade는 필수" },
            isDuesPaid = requireNotNull(isDuesPaid) { "active 상태에서 isDuesPaid는 필수" }
        )
        "inactive" -> MemberStatus.Inactive(
            inActiveReason = requireNotNull(inActiveReason) { "inactive 상태에서 inActiveReason은 필수" },
            expectedReturnSemester = requireNotNull(expectedReturnSemester) { "inactive 상태에서 expectedReturnSemester는 필수" }
        )
        "completed" -> MemberStatus.Completed(
            completedSemester = requireNotNull(completedSemester) { "completed 상태에서 completedSemester는 필수" }
        )
        "withdrawn" -> MemberStatus.Withdrawn(
            withdrawnSemester = requireNotNull(withdrawnSemester) { "withdrawn 상태에서 withdrawnSemester는 필수" }
        )
        else -> error("Unknown state: $state")
    }

    return Member(
        id = requireNotNull(id),
        name = name,
        nickname = nickname,
        nicknameKo = nicknameKo,
        email = email,
        phoneNumber = phoneNumber,
        department = department,
        studentId = studentId,
        birthDate = birthDate,
        joinSemester = joinSemester,
        position = MemberPosition.valueOf(position),
        parts = parts.map { MemberPart.valueOf(it.part) },
        status = status,
        histories = histories.map { MemberHistory(it.semester, it.state) },
        note = note
    )
}

fun Member.toJpaEntity(): MemberJpaEntity {
    val entity = MemberJpaEntity(
        id = if (id == 0L) null else id,
        name = name,
        nickname = nickname,
        nicknameKo = nicknameKo,
        email = email,
        phoneNumber = phoneNumber,
        department = department,
        studentId = studentId,
        birthDate = birthDate,
        joinSemester = joinSemester,
        position = position.name,
        state = status.toStateString(),
        note = note,
        isOnLeave = (status as? MemberStatus.Active)?.isOnLeave,
        grade = (status as? MemberStatus.Active)?.grade,
        isDuesPaid = (status as? MemberStatus.Active)?.isDuesPaid,
        inActiveReason = (status as? MemberStatus.Inactive)?.inActiveReason,
        expectedReturnSemester = (status as? MemberStatus.Inactive)?.expectedReturnSemester,
        completedSemester = (status as? MemberStatus.Completed)?.completedSemester,
        withdrawnSemester = (status as? MemberStatus.Withdrawn)?.withdrawnSemester,
    )
    parts.forEach { entity.parts.add(MemberPartJpaEntity(member = entity, part = it.name)) }
    histories.forEach { entity.histories.add(MemberHistoryJpaEntity(member = entity, semester = it.semester, state = it.state)) }
    return entity
}

fun MemberJpaEntity.updateFrom(member: Member) {
    name = member.name
    nickname = member.nickname
    nicknameKo = member.nicknameKo
    email = member.email
    phoneNumber = member.phoneNumber
    department = member.department
    studentId = member.studentId
    birthDate = member.birthDate
    joinSemester = member.joinSemester
    position = member.position.name
    note = member.note
    state = member.status.toStateString()
    isOnLeave = (member.status as? MemberStatus.Active)?.isOnLeave
    grade = (member.status as? MemberStatus.Active)?.grade
    isDuesPaid = (member.status as? MemberStatus.Active)?.isDuesPaid
    inActiveReason = (member.status as? MemberStatus.Inactive)?.inActiveReason
    expectedReturnSemester = (member.status as? MemberStatus.Inactive)?.expectedReturnSemester
    completedSemester = (member.status as? MemberStatus.Completed)?.completedSemester
    withdrawnSemester = (member.status as? MemberStatus.Withdrawn)?.withdrawnSemester

    parts.clear()
    member.parts.forEach { parts.add(MemberPartJpaEntity(member = this, part = it.name)) }

    histories.clear()
    member.histories.forEach { histories.add(MemberHistoryJpaEntity(member = this, semester = it.semester, state = it.state)) }
}

private fun MemberStatus.toStateString() = when (this) {
    is MemberStatus.Active -> "active"
    is MemberStatus.Inactive -> "inactive"
    is MemberStatus.Completed -> "completed"
    is MemberStatus.Withdrawn -> "withdrawn"
}
