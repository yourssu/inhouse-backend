package com.yourssu.inhouse.member

import com.yourssu.inhouse.config.SecurityConfig
import com.yourssu.inhouse.member.application.MemberController
import com.yourssu.inhouse.member.business.MemberService
import com.yourssu.inhouse.member.implement.Member
import com.yourssu.inhouse.member.implement.MemberNotFoundException
import com.yourssu.inhouse.member.implement.MemberPart
import com.yourssu.inhouse.member.implement.MemberPosition
import com.yourssu.inhouse.member.implement.MemberStatus
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

@WebMvcTest(MemberController::class)
@Import(SecurityConfig::class)
class MemberControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var memberService: MemberService

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

    private val createRequestJson = """
        {
            "name": "홍길동",
            "nickname": "hong",
            "nicknameKo": "홍",
            "email": "hong@yourssu.com",
            "phoneNumber": "010-0000-0000",
            "department": "컴퓨터학부",
            "studentId": "20200001",
            "birthDate": "2000-01-01",
            "joinSemester": "2020-1",
            "position": "MEMBER",
            "parts": ["BACKEND"],
            "state": "active",
            "isOnLeave": false,
            "grade": 3,
            "isDuesPaid": true
        }
    """.trimIndent()

    @Test
    fun `GET members returns 200 with member list`() {
        val members = listOf(sampleMember())
        whenever(memberService.search(isNull(), isNull(), eq(0), eq(20)))
            .thenReturn(PageImpl(members, PageRequest.of(0, 20), 1))

        mockMvc.get("/members")
            .andExpect {
                status { isOk() }
                jsonPath("$.content[0].id") { value(1) }
                jsonPath("$.content[0].name") { value("홍길동") }
                jsonPath("$.content[0].state") { value("active") }
                jsonPath("$.totalElements") { value(1) }
            }
    }

    @Test
    fun `GET members-id returns 200 with member`() {
        whenever(memberService.get(1L)).thenReturn(sampleMember())

        mockMvc.get("/members/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(1) }
                jsonPath("$.email") { value("hong@yourssu.com") }
                jsonPath("$.parts[0]") { value("BACKEND") }
            }
    }

    @Test
    fun `GET members-id returns 404 for unknown member`() {
        whenever(memberService.get(99L)).thenThrow(MemberNotFoundException(99L))

        mockMvc.get("/members/99")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error") { exists() }
            }
    }

    @Test
    fun `POST members returns 201 with created member`() {
        whenever(memberService.create(any())).thenReturn(sampleMember())

        mockMvc.post("/members") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequestJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
        }
    }

    @Test
    fun `PUT members-id returns 200 with updated member`() {
        val updated = sampleMember().copy(name = "수정됨")
        whenever(memberService.update(eq(1L), any())).thenReturn(updated)

        mockMvc.put("/members/1") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequestJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("수정됨") }
        }
    }

    @Test
    fun `DELETE members-id returns 204`() {
        mockMvc.delete("/members/1")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `DELETE members-id returns 404 for unknown member`() {
        whenever(memberService.delete(99L)).thenThrow(MemberNotFoundException(99L))

        mockMvc.delete("/members/99")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `GET members with query param returns filtered results`() {
        val members = listOf(sampleMember())
        whenever(memberService.search(eq("홍"), isNull(), eq(0), eq(20)))
            .thenReturn(PageImpl(members, PageRequest.of(0, 20), 1))

        mockMvc.get("/members?query=홍")
            .andExpect {
                status { isOk() }
                jsonPath("$.content[0].name") { value("홍길동") }
                jsonPath("$.totalElements") { value(1) }
            }
    }

    @Test
    fun `GET members with part filter returns filtered results`() {
        val members = listOf(sampleMember())
        whenever(memberService.search(isNull(), eq(MemberPart.BACKEND), eq(0), eq(20)))
            .thenReturn(PageImpl(members, PageRequest.of(0, 20), 1))

        mockMvc.get("/members?part=BACKEND")
            .andExpect {
                status { isOk() }
                jsonPath("$.content[0].parts[0]") { value("BACKEND") }
            }
    }

    @Test
    fun `GET members with pagination params uses correct page and size`() {
        whenever(memberService.search(isNull(), isNull(), eq(1), eq(5)))
            .thenReturn(PageImpl(emptyList(), PageRequest.of(1, 5), 0))

        mockMvc.get("/members?page=1&size=5")
            .andExpect {
                status { isOk() }
                jsonPath("$.page") { value(1) }
                jsonPath("$.size") { value(5) }
                jsonPath("$.totalElements") { value(0) }
            }
    }

    @Test
    fun `POST members with inactive state returns 201 with inactive fields`() {
        val inactiveMember = sampleMember().copy(
            status = MemberStatus.Inactive(
                inActiveReason = "군휴학",
                expectedReturnSemester = "2025-1",
            )
        )
        whenever(memberService.create(any())).thenReturn(inactiveMember)

        val inactiveJson = """
            {
                "name": "홍길동",
                "nickname": "hong",
                "nicknameKo": "홍",
                "email": "hong@yourssu.com",
                "phoneNumber": "010-0000-0000",
                "department": "컴퓨터학부",
                "studentId": "20200001",
                "birthDate": "2000-01-01",
                "joinSemester": "2020-1",
                "position": "MEMBER",
                "parts": ["BACKEND"],
                "state": "inactive",
                "inActiveReason": "군휴학",
                "expectedReturnSemester": "2025-1"
            }
        """.trimIndent()

        mockMvc.post("/members") {
            contentType = MediaType.APPLICATION_JSON
            content = inactiveJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.state") { value("inactive") }
            jsonPath("$.inActiveReason") { value("군휴학") }
            jsonPath("$.expectedReturnSemester") { value("2025-1") }
        }
    }

    @Test
    fun `POST members with unknown state returns 400`() {
        val invalidJson = """
            {
                "name": "홍길동",
                "nickname": "hong",
                "nicknameKo": "홍",
                "email": "hong@yourssu.com",
                "phoneNumber": "010-0000-0000",
                "department": "컴퓨터학부",
                "studentId": "20200001",
                "birthDate": "2000-01-01",
                "joinSemester": "2020-1",
                "position": "MEMBER",
                "parts": ["BACKEND"],
                "state": "unknown_state"
            }
        """.trimIndent()

        mockMvc.post("/members") {
            contentType = MediaType.APPLICATION_JSON
            content = invalidJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { exists() }
        }
    }

    @Test
    fun `PUT members-id returns 404 for unknown member`() {
        whenever(memberService.update(eq(99L), any())).thenThrow(MemberNotFoundException(99L))

        mockMvc.put("/members/99") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequestJson
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.error") { exists() }
        }
    }
}
