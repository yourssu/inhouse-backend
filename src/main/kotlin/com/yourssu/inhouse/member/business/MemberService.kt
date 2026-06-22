package com.yourssu.inhouse.member.business

import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberReader
import com.yourssu.inhouse.member.implement.MemberWriter
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
) {
    fun getAll(): List<Member> = memberReader.readAll()

    fun get(id: Long): Member = memberReader.read(id)

    fun search(query: String?, part: MemberPart?, page: Int, size: Int): Page<Member> =
        memberReader.search(query, part, page, size)

    fun create(command: CreateMemberCommand): Member = memberWriter.create(command.toDomain())

    fun update(id: Long, command: UpdateMemberCommand): Member = memberWriter.update(id, command.toDomain())

    fun delete(id: Long) = memberWriter.delete(id)
}
