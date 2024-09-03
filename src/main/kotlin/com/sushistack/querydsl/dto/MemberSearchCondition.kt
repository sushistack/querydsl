package com.sushistack.querydsl.dto

data class MemberSearchCondition (
    val username: String? = null,
    val teamName: String? = null,
    val ageGoe: Int? = null,
    val ageLoe: Int? = null
)