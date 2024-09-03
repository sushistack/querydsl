package com.sushistack.querydsl.repository

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import java.awt.print.Pageable


interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByUsername(username: String): List<Member>
}