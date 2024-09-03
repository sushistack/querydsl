package com.sushistack.querydsl.repository

import com.sushistack.querydsl.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByUsername(username: String): List<Member>
}