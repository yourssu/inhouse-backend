package com.yourssu.inhouse.member.implement

import com.yourssu.inhouse.member.storage.MemberJpaRepository
import com.yourssu.inhouse.member.storage.toDomain
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberReader(private val repository: MemberJpaRepository) {

    @Transactional(readOnly = true)
    fun read(id: Long): Member =
        repository.findById(id).orElseThrow { MemberNotFoundException(id) }.toDomain()

    @Transactional(readOnly = true)
    fun readAll(): List<Member> =
        repository.findAll().map { it.toDomain() }

    @Transactional(readOnly = true)
    fun search(query: String?, part: MemberPart?, page: Int, size: Int): Page<Member> {
        val all = repository.findAll().map { it.toDomain() }
        val filtered = all.filter { member ->
            val matchesQuery = query.isNullOrBlank() ||
                KoreanTextUtils.matches(member.name, query) ||
                KoreanTextUtils.matches(member.nickname, query) ||
                KoreanTextUtils.matches(member.nicknameKo, query)
            val matchesPart = part == null || part in member.parts
            matchesQuery && matchesPart
        }
        val content = filtered.drop(page * size).take(size)
        return PageImpl(content, PageRequest.of(page, size), filtered.size.toLong())
    }
}
