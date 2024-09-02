package com.sushistack.querydsl.repository

import com.sushistack.querydsl.entity.Member
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun basicTest() {
        val member = Member(username = "member1", age = 10)
            .also { memberJpaRepository.save(it) }

        val findMember = memberJpaRepository.findById(member.id)
        assertThat(findMember).isEqualTo(member)

        // val members1 = memberJpaRepository.findAll()
        val members1 = memberJpaRepository.findAllQuerydsl()
        assertThat(members1).containsExactly(member)

        // val members2 = memberJpaRepository.findByUsername("member1")
        val members2 = memberJpaRepository.findByUsernameQuerydsl("member1")
        assertThat(members2).containsExactly(member)
    }

}