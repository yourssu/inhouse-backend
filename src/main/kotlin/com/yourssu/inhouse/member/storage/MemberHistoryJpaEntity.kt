package com.yourssu.inhouse.member.storage

import jakarta.persistence.*

@Entity
class MemberHistoryJpaEntity(
    @Id @GeneratedValue(GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    val semester: String,
    val state: String,
)