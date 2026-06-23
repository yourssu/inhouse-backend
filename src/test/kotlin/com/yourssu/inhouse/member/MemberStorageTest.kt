package com.yourssu.inhouse.member

import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberHistory
import com.yourssu.inhouse.member.implement.MemberNotFoundException
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberReader
import com.yourssu.inhouse.member.implement.MemberStatus
import com.yourssu.inhouse.member.implement.MemberWriter
import com.yourssu.inhouse.member.storage.MemberJpaRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.context.annotation.Import
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@Import(MemberReader::class, MemberWriter::class)
class MemberStorageTest {

    @Autowired private lateinit var memberReader: MemberReader
    @Autowired private lateinit var memberWriter: MemberWriter
    @Autowired private lateinit var repository: MemberJpaRepository
    @Autowired private lateinit var em: TestEntityManager

    private fun sampleMember(
        parts: List<MemberPart> = listOf(MemberPart.BACKEND),
        histories: List<MemberHistory> = emptyList(),
        status: MemberStatus = MemberStatus.Active(isOnLeave = false, grade = 3, isDuesPaid = true),
    ) = Member(
        id = 0L,
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
        parts = parts,
        status = status,
        histories = histories,
    )

    @Test
    fun `create saves member with parts and histories`() {
        val histories = listOf(MemberHistory("2020-1", "active"), MemberHistory("2020-2", "active"))
        val member = memberWriter.create(sampleMember(histories = histories))

        em.flush()
        em.clear()

        val found = memberReader.read(member.id)
        assertEquals("홍길동", found.name)
        assertEquals(listOf(MemberPart.BACKEND), found.parts)
        assertEquals(2, found.histories.size)
    }

    @Test
    fun `readAll returns all saved members`() {
        memberWriter.create(sampleMember())
        memberWriter.create(sampleMember())
        em.flush()
        em.clear()

        val all = memberReader.readAll()

        assertTrue(all.size >= 2)
    }

    @Test
    fun `read throws MemberNotFoundException for unknown id`() {
        assertThrows<MemberNotFoundException> { memberReader.read(9999L) }
    }

    @Test
    fun `update changes fields and replaces parts correctly`() {
        val created = memberWriter.create(sampleMember(parts = listOf(MemberPart.BACKEND)))
        em.flush()
        em.clear()

        val updated = memberWriter.update(
            created.id,
            sampleMember(parts = listOf(MemberPart.BACKEND, MemberPart.FRONTEND))
        )
        em.flush()
        em.clear()

        val readBack = memberReader.read(created.id)
        assertEquals(setOf(MemberPart.BACKEND, MemberPart.FRONTEND), readBack.parts.toSet())
    }

    @Test
    fun `update does not duplicate parts on repeated save`() {
        val created = memberWriter.create(sampleMember(parts = listOf(MemberPart.BACKEND)))
        em.flush()
        em.clear()

        memberWriter.update(created.id, sampleMember(parts = listOf(MemberPart.BACKEND)))
        em.flush()
        em.clear()

        val readBack = memberReader.read(created.id)
        assertEquals(1, readBack.parts.size)
    }

    @Test
    fun `update throws MemberNotFoundException for unknown id`() {
        assertThrows<MemberNotFoundException> {
            memberWriter.update(9999L, sampleMember())
        }
    }

    @Test
    fun `delete removes the member`() {
        val member = memberWriter.create(sampleMember())
        em.flush()
        em.clear()

        memberWriter.delete(member.id)

        assertFalse(repository.existsById(member.id))
    }

    @Test
    fun `delete throws MemberNotFoundException for unknown id`() {
        assertThrows<MemberNotFoundException> { memberWriter.delete(9999L) }
    }

    @Test
    fun `inactive status is persisted and restored correctly`() {
        val inactiveStatus = MemberStatus.Inactive(
            inActiveReason = "군휴학",
            expectedReturnSemester = "2025-1",
        )
        val created = memberWriter.create(sampleMember(status = inactiveStatus))
        em.flush()
        em.clear()

        val found = memberReader.read(created.id)
        val status = found.status as MemberStatus.Inactive
        assertEquals("군휴학", status.inActiveReason)
        assertEquals("2025-1", status.expectedReturnSemester)
    }

    @Test
    fun `completed status is persisted and restored correctly`() {
        val completedStatus = MemberStatus.Completed(completedSemester = "2024-2")
        val created = memberWriter.create(sampleMember(status = completedStatus))
        em.flush()
        em.clear()

        val found = memberReader.read(created.id)
        val status = found.status as MemberStatus.Completed
        assertEquals("2024-2", status.completedSemester)
    }

    @Test
    fun `withdrawn status is persisted and restored correctly`() {
        val withdrawnStatus = MemberStatus.Withdrawn(withdrawnSemester = "2023-1")
        val created = memberWriter.create(sampleMember(status = withdrawnStatus))
        em.flush()
        em.clear()

        val found = memberReader.read(created.id)
        val status = found.status as MemberStatus.Withdrawn
        assertEquals("2023-1", status.withdrawnSemester)
    }

    @Test
    fun `search finds member by name query`() {
        memberWriter.create(sampleMember())
        em.flush()
        em.clear()

        val result = memberReader.search("홍길동", null, 0, 20)

        assertTrue(result.content.any { it.name == "홍길동" })
    }

    @Test
    fun `search finds member by chosung query`() {
        memberWriter.create(sampleMember())
        em.flush()
        em.clear()

        val result = memberReader.search("ㅎㄱㄷ", null, 0, 20)

        assertTrue(result.totalElements >= 1)
        assertTrue(result.content.any { it.name == "홍길동" })
    }

    @Test
    fun `search returns empty when query does not match`() {
        memberWriter.create(sampleMember())
        em.flush()
        em.clear()

        val result = memberReader.search("김철수", null, 0, 20)

        assertTrue(result.content.none { it.name == "홍길동" })
    }

    @Test
    fun `search filters by part and excludes non-matching members`() {
        memberWriter.create(sampleMember(parts = listOf(MemberPart.BACKEND)))
        memberWriter.create(sampleMember(parts = listOf(MemberPart.FRONTEND)))
        em.flush()
        em.clear()

        val result = memberReader.search(null, MemberPart.BACKEND, 0, 20)

        assertTrue(result.content.all { MemberPart.BACKEND in it.parts })
        assertTrue(result.content.none { MemberPart.FRONTEND in it.parts && MemberPart.BACKEND !in it.parts })
    }

    @Test
    fun `search pagination returns correct number of results`() {
        repeat(5) { memberWriter.create(sampleMember()) }
        em.flush()
        em.clear()

        val result = memberReader.search(null, null, 0, 3)

        assertEquals(3, result.content.size)
        assertEquals(3, result.size)
        assertTrue(result.totalElements >= 5)
    }

    @Test
    fun `search second page returns remaining results`() {
        repeat(5) { memberWriter.create(sampleMember()) }
        em.flush()
        em.clear()

        val total = memberReader.search(null, null, 0, 20).totalElements
        val firstPage = memberReader.search(null, null, 0, 3)
        val secondPage = memberReader.search(null, null, 1, 3)

        assertEquals(total, firstPage.totalElements)
        assertEquals((total - 3).coerceAtLeast(0), secondPage.content.size.toLong())
    }
}
