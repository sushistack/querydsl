package com.sushistack.querydsl.repository

import com.sushistack.querydsl.entity.Member
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var memberRepository: MemberRepository

    private lateinit var mem: Member

    @BeforeEach
    fun setup() {
        mem = Member(username = "memberX", age = 10)
            .also { memberRepository.save(it) }
    }

    @Test
    fun basicTest() {
        val findMember = memberRepository.findById(mem.id).get()
        assertThat(findMember).isEqualTo(mem)

        // val members1 = memberJpaRepository.findAll()
        val members1 = memberRepository.findAll()
        assertThat(members1).containsExactly(mem)

        // val members2 = memberJpaRepository.findByUsername("member1")
        val members2 = memberRepository.findByUsername("memberX")
        assertThat(members2).containsExactly(mem)
    }
}