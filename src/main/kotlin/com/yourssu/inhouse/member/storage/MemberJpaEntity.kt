package com.yourssu.inhouse.member.storage

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "members")
class MemberJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var nickname: String,
    var nicknameKo: String,
    var email: String,
    var phoneNumber: String,
    var department: String,
    var studentId: String,
    var birthDate: LocalDate,
    var joinSemester: String,
    var position: String,
    var state: String,
    var note: String? = null,

    var isOnLeave: Boolean? = null,
    var grade: Int? = null,
    var isDuesPaid: Boolean? = null,

    @Column(name = "inactive_reason")
    var inActiveReason: String? = null,
    var expectedReturnSemester: String? = null,

    var completedSemester: String? = null,

    var withdrawnSemester: String? = null,

    var passwordHash: String? = null,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val parts: MutableList<MemberPartJpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val histories: MutableList<MemberHistoryJpaEntity> = mutableListOf(),
)