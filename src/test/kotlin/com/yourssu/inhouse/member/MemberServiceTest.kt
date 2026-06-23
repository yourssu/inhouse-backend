package com.yourssu.inhouse.member

import com.yourssu.inhouse.member.business.CreateMemberCommand
import com.yourssu.inhouse.member.business.MemberService
import com.yourssu.inhouse.member.business.UpdateMemberCommand
import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberNotFoundException
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberReader
import com.yourssu.inhouse.member.implement.MemberStatus
import com.yourssu.inhouse.member.implement.MemberWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {

    @Mock private lateinit var memberReader: MemberReader
    @Mock private lateinit var memberWriter: MemberWriter
    @InjectMocks private lateinit var memberService: MemberService

    private fun sampleMember(id: Long = 1L) = Member(
        id = id,
        name = "홍길동",
        nickname = "hong",
        nicknameKo = "홍",
        email = "hong@yourssu.com",
        phoneNumber = "010-0000-0000",
        department = "컴퓨터학부",
        studentId = "20200001",
        birthDate = LocalDate.of(2000, 1, 1),
        joinSemester = "2020-1",
        position = MemberPosition.MEMBER,
        parts = listOf(MemberPart.BACKEND),
        status = MemberStatus.Active(isOnLeave = false, grade = 3, isDuesPaid = true),
        histories = emptyList(),
    )

    @Test
    fun `getAll returns all members from reader`() {
        val members = listOf(sampleMember(1L), sampleMember(2L))
        whenever(memberReader.readAll()).thenReturn(members)

        val result = memberService.getAll()

        assertEquals(2, result.size)
        verify(memberReader).readAll()
    }

    @Test
    fun `get returns member by id`() {
        val member = sampleMember()
        whenever(memberReader.read(1L)).thenReturn(member)

        val result = memberService.get(1L)

        assertEquals(member, result)
        verify(memberReader).read(1L)
    }

    @Test
    fun `get throws MemberNotFoundException for unknown id`() {
        whenever(memberReader.read(99L)).thenThrow(MemberNotFoundException(99L))

        assertThrows<MemberNotFoundException> { memberService.get(99L) }
    }

    @Test
    fun `create calls writer and returns saved member`() {
        val command = CreateMemberCommand(
            name = "홍길동", nickname = "hong", nicknameKo = "홍",
            email = "hong@yourssu.com", phoneNumber = "010-0000-0000",
            department = "컴퓨터학부", studentId = "20200001",
            birthDate = LocalDate.of(2000, 1, 1), joinSemester = "2020-1",
            position = MemberPosition.MEMBER, parts = listOf(MemberPart.BACKEND),
            status = MemberStatus.Active(isOnLeave = false, grade = 3, isDuesPaid = true),
        )
        val created = sampleMember()
        whenever(memberWriter.create(command.toDomain())).thenReturn(created)

        val result = memberService.create(command)

        assertEquals(created, result)
        verify(memberWriter).create(command.toDomain())
    }

    @Test
    fun `update calls writer with correct id and member`() {
        val command = UpdateMemberCommand(
            name = "수정됨", nickname = "updated", nicknameKo = "수",
            email = "updated@yourssu.com", phoneNumber = "010-1111-1111",
            department = "소프트웨어학부", studentId = "20200001",
            birthDate = LocalDate.of(2000, 1, 1), joinSemester = "2020-1",
            position = MemberPosition.LEAD, parts = listOf(MemberPart.BACKEND, MemberPart.FRONTEND),
            status = MemberStatus.Active(isOnLeave = false, grade = 4, isDuesPaid = true),
            histories = emptyList(),
        )
        val updated = sampleMember().copy(name = "수정됨")
        whenever(memberWriter.update(1L, command.toDomain())).thenReturn(updated)

        val result = memberService.update(1L, command)

        assertEquals(updated, result)
        verify(memberWriter).update(1L, command.toDomain())
    }

    @Test
    fun `delete calls writer with correct id`() {
        memberService.delete(1L)

        verify(memberWriter).delete(1L)
    }

    @Test
    fun `search delegates to memberReader with correct params`() {
        val page = PageImpl(listOf(sampleMember()), PageRequest.of(0, 10), 1)
        whenever(memberReader.search("홍", MemberPart.BACKEND, 0, 10)).thenReturn(page)

        val result = memberService.search("홍", MemberPart.BACKEND, 0, 10)

        assertEquals(1, result.content.size)
        verify(memberReader).search("홍", MemberPart.BACKEND, 0, 10)
    }

    @Test
    fun `search with null params delegates to memberReader`() {
        val page = PageImpl(listOf(sampleMember(), sampleMember(2L)), PageRequest.of(0, 20), 2)
        whenever(memberReader.search(null, null, 0, 20)).thenReturn(page)

        val result = memberService.search(null, null, 0, 20)

        assertEquals(2, result.content.size)
        verify(memberReader).search(null, null, 0, 20)
    }
}
