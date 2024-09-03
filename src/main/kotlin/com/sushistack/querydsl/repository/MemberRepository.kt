package com.sushistack.querydsl.repository

import com.sushistack.querydsl.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor


interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {
    fun findByUsername(username: String): List<Member>
}