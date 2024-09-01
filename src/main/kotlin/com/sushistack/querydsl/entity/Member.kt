package com.sushistack.querydsl.entity

import jakarta.persistence.*

@Entity
class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    val id: Long = 0,
    var username: String = "",
    val age: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null,
) {
    fun changeTeam(team: Team) {
        this.team = team
        team.members.add(this)
    }

    override fun toString() = "Member(id=$id, username='$username', age=$age)"
}
