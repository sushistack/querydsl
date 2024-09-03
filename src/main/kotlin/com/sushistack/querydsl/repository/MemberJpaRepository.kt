package com.sushistack.querydsl.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.dto.MemberDTO
import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.dto.QMemberTeamDTO
import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.QMember.member
import com.sushistack.querydsl.entity.QTeam
import com.sushistack.querydsl.entity.QTeam.*
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MemberJpaRepository(
    private val entityManager: EntityManager,
    private val queryFactory: JPAQueryFactory
) {
    fun save(member: Member) = entityManager.persist(member)

    fun findById(id: Long) = entityManager.find(Member::class.java, id)

    fun findAll() = entityManager.createQuery("SELECT m FROM Member m", Member::class.java).resultList

    fun findAllQuerydsl() =
        queryFactory
            .selectFrom(member)
            .fetch()

    fun findByUsername(username: String) =
        entityManager.createQuery("SELECT m FROM Member m WHERE m.username = :username", Member::class.java)
            .setParameter("username", username)
            .resultList

    fun findByUsernameQuerydsl(username: String) =
        queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch()

    fun searchByBuilder(condition: MemberSearchCondition) = BooleanBuilder().let { b ->
        condition.username?.let { b.and(member.username.eq(it)) }
        condition.teamName?.let { b.and(team.name.eq(it)) }
        condition.ageGoe?.let { b.and(member.age.goe(it)) }
        condition.ageLoe?.let { b.and(member.age.loe(it)) }

        queryFactory
            .select(
                QMemberTeamDTO(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name.`as`("teamName")
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(b)
            .fetch()
    }


}