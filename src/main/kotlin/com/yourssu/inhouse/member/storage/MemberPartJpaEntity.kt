package com.yourssu.inhouse.member.storage

import jakarta.persistence.*

@Entity
class MemberPartJpaEntity(
    @Id @GeneratedValue(GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    val part: String,
)