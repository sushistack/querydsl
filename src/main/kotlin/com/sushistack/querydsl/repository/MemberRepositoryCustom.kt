package com.sushistack.querydsl.repository

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun search(condition: MemberSearchCondition): List<MemberTeamDTO>
    fun searchPageSimple(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO?>
    fun searchPageComplex(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO?>
}