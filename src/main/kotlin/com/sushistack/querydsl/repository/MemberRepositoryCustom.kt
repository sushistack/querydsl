package com.sushistack.querydsl.repository

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.dto.MemberTeamDTO

interface MemberRepositoryCustom {
    fun search(condition: MemberSearchCondition): List<MemberTeamDTO>
}