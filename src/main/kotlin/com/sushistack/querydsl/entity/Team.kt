package com.sushistack.querydsl.entity

import jakarta.persistence.*

@Entity
class Team (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    val id: Long = 0,
    val name: String = "",
    @OneToMany(mappedBy = "team")
    val members: MutableList<Member> = mutableListOf()
) {
    override fun toString() = "Team(id=$id, name='$name')"
}