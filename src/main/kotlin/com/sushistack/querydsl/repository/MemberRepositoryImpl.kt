package com.sushistack.querydsl.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.dto.QMemberTeamDTO
import com.sushistack.querydsl.entity.QMember.member
import com.sushistack.querydsl.entity.QTeam.team
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.support.PageableUtils
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val entityManager: EntityManager,
    private val queryFactory: JPAQueryFactory
): MemberRepositoryCustom {
    override fun search(condition: MemberSearchCondition): List<MemberTeamDTO> =
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
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .fetch()

    private fun usernameEq(usernameCond: String?) =
        usernameCond?.let { member.username.eq(it) }

    private fun teamNameEq(teamNameCond: String?) =
        teamNameCond?.let { member.team.name.eq(it) }

    private fun ageGoe(ageCond: Int?) =
        ageCond?.let { member.age.goe(it) }

    private fun ageLoe(ageCond: Int?) =
        ageCond?.let { member.age.loe(it) }

    override fun searchPageSimple(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO> =
        queryFactory
            .select(
                QMemberTeamDTO(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()
            .let { PageImpl(it.results, pageable, it.total) }

    override fun searchPageComplex(condition: MemberSearchCondition, pageable: Pageable): Page<MemberTeamDTO> {
        val content = queryFactory
            .select(
                QMemberTeamDTO(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(member)
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe))
            // .fetchCount()

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchCount() }

        // return PageImpl(content, pageable, total)
    }

}