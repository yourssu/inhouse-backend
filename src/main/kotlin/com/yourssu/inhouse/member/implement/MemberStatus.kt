package com.yourssu.inhouse.member.implement

sealed class MemberStatus {
    data class Active(
        val isOnLeave: Boolean,
        val grade: Int,
        val isDuesPaid: Boolean,
    ) : MemberStatus()

    data class Inactive(
        val inActiveReason: String,
        val expectedReturnSemester: String,
    ) : MemberStatus()

    data class Completed(
        val completedSemester: String,
    ) : MemberStatus()

    data class Withdrawn(
        val withdrawnSemester: String,
    ) : MemberStatus()
}