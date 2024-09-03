package com.sushistack.querydsl.repository

import com.sushistack.querydsl.dto.MemberSearchCondition
import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.Team
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    private lateinit var mem: Member

    @BeforeEach
    fun setup() {
        mem = Member(username = "memberX", age = 10)
            .also { memberJpaRepository.save(it) }
    }

    @Test
    fun basicTest() {
        val findMember = memberJpaRepository.findById(mem.id)
        assertThat(findMember).isEqualTo(mem)

        // val members1 = memberJpaRepository.findAll()
        val members1 = memberJpaRepository.findAllQuerydsl()
        assertThat(members1).containsExactly(mem)

        // val members2 = memberJpaRepository.findByUsername("member1")
        val members2 = memberJpaRepository.findByUsernameQuerydsl("memberX")
        assertThat(members2).containsExactly(mem)
    }

    @Test
    fun searchTest() {
        val teamA = Team(name = "teamA").also { entityManager.persist(it) }
        val teamB = Team(name = "teamB").also { entityManager.persist(it) }

        Member(username = "member1", age = 10, team = teamA).also { entityManager.persist(it) }
        Member(username = "member2", age = 20, team = teamA).also { entityManager.persist(it) }
        Member(username = "member3", age = 30, team = teamB).also { entityManager.persist(it) }
        Member(username = "member4", age = 40, team = teamB).also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val result = MemberSearchCondition(teamName = "teamB", ageGoe = 35, ageLoe = 40).let {
            // memberJpaRepository.searchByBuilder(it)
            memberJpaRepository.search(it)
        }

        assertThat(result).extracting("username").containsExactly("member4")
    }
}