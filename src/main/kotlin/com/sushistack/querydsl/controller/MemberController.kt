package com.sushistack.querydsl.controller

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.repository.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequiredArgsConstructor
class MemberController(private val memberRepository: MemberRepository) {

    @GetMapping("/v1/members")
    fun searchMemberV1(condition: MemberSearchCondition?): List<MemberTeamDTO> =
        memberRepository.search(condition!!)

    @GetMapping("/v2/members")
    fun searchMemberV2(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO> =
        memberRepository.searchPageSimple(condition, pageable)

    @GetMapping("/v3/members")
    fun searchMemberV3(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO>
    = memberRepository.searchPageComplex(condition, pageable)
}

