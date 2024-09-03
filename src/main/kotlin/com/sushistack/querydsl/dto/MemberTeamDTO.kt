package com.sushistack.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

data class MemberTeamDTO @QueryProjection constructor(
    var memberId: Long = 0,
    var username: String = "",
    var age: Int = 0,
    var teamId: Long = 0,
    var teamName: String = ""
)