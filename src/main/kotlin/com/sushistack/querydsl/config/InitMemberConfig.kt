package com.sushistack.querydsl.config

import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.Team
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Configuration
class InitMemberConfig {
    private val initMemberService: InitMemberService? = null

    @PostConstruct
    fun init() {
        initMemberService!!.init()
    }

    @Component
    internal class InitMemberService {
        @PersistenceContext
        var em: EntityManager? = null

        @Transactional
        fun init() {
            val teamA = Team(name = "teamA")
            val teamB = Team(name = "teamB")
            em!!.persist(teamA)
            em!!.persist(teamB)
            for (i in 0..99) {
                val selectedTeam = if (i % 2 == 0) teamA else teamB
                em!!.persist(Member(username = "member$i", age = i, team = selectedTeam))
            }
        }
    }
}
