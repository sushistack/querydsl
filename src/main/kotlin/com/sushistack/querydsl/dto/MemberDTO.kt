package com.sushistack.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

data class MemberDTO @QueryProjection constructor(
    var username: String = "",
    var age: Int = 0
)
