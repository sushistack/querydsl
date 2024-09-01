package com.sushistack.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.QMember
import com.sushistack.querydsl.entity.QMember.*
import com.sushistack.querydsl.entity.Team
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class QuerydslBasicTest {

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @BeforeEach
    fun setup() {
        val teamA = Team(name = "teamA").also { entityManager.persist(it) }
        val teamB = Team(name = "teamB").also { entityManager.persist(it) }

        Member(username = "member1", age = 10, team = teamA).also { entityManager.persist(it) }
        Member(username = "member2", age = 20, team = teamA).also { entityManager.persist(it) }
        Member(username = "member3", age = 30, team = teamB).also { entityManager.persist(it) }
        Member(username = "member4", age = 40, team = teamB).also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun jpql() {
        val members = entityManager.createQuery("SELECT m FROM Member m WHERE username = :username", Member::class.java)
            .setParameter("username", "member1")
            .resultList

        assertThat(members[0].username).isEqualTo("member1")
    }

    @Test
    fun querydsl() {
        // val qMember = new QMember("aliasName") => 같은 테이블 조인할 때 사용
        val member = jpaQueryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        assertThat(member?.username).isEqualTo("member1")
    }
}