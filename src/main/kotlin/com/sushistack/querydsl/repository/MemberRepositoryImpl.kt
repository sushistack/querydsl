package com.sushistack.querydsl.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO
import com.sushistack.querydsl.dto.QMemberTeamDTO
import com.sushistack.querydsl.entity.QMember.member
import com.sushistack.querydsl.entity.QTeam.team
import jakarta.persistence.EntityManager

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
}