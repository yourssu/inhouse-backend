package com.yourssu.inhouse.member.application

import com.yourssu.inhouse.member.business.MemberService
import com.yourssu.inhouse.member.implement.MemberPart
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(private val memberService: MemberService) {

    @GetMapping
    fun search(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) part: MemberPart?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): PageResponse<MemberResponse> {
        val result = memberService.search(query, part, page, size)
        return PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
        )
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MemberResponse =
        memberService.get(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateMemberRequest): MemberResponse =
        memberService.create(request.toCommand()).toResponse()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateMemberRequest): MemberResponse =
        memberService.update(id, request.toCommand()).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = memberService.delete(id)
}
