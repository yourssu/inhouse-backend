package com.yourssu.inhouse.member.implement

import com.yourssu.inhouse.member.storage.MemberJpaRepository
import com.yourssu.inhouse.member.storage.toDomain
import com.yourssu.inhouse.member.storage.toJpaEntity
import com.yourssu.inhouse.member.storage.updateFrom
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberWriter(private val repository: MemberJpaRepository) {

    @Transactional
    fun create(member: Member): Member {
        val entity = member.toJpaEntity()
        return repository.save(entity).toDomain()
    }

    @Transactional
    fun update(id: Long, member: Member): Member {
        val entity = repository.findById(id).orElseThrow { MemberNotFoundException(id) }
        entity.updateFrom(member)
        return repository.save(entity).toDomain()
    }

    @Transactional
    fun delete(id: Long) {
        if (!repository.existsById(id)) throw MemberNotFoundException(id)
        repository.deleteById(id)
    }
}
