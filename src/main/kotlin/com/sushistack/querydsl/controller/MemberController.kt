package com.sushistack.querydsl.controller

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.repository.MemberJpaRepository
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequiredArgsConstructor
class MemberController(private val memberJpaRepository: MemberJpaRepository) {

    @GetMapping("/v1/members")
    fun searchMemberV1(condition: MemberSearchCondition?): List<MemberTeamDTO> =
        memberJpaRepository.search(condition!!)
}

