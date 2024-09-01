package com.sushistack.querydsl

import com.querydsl.core.QueryResults
import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.entity.Member
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

    @Test
    fun search() {
        val member1 = jpaQueryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1").and(member.age.eq(10)))
            .fetchOne()

        assertThat(member1?.username).isEqualTo("member1")
    }

    @Test
    fun searchAndParam() {
        val result1 = jpaQueryFactory
            .selectFrom(member)
            .where(
                // ...으로 모두 AND 조건으로 연결된다.
                member.username.eq("member1"),
                member.age.eq(10)
            )
            .fetch()

        assertThat(result1.size).isEqualTo(1)
    }

    @Test
    fun fetches() {

        //List
        val fetch: List<Member> = jpaQueryFactory
            .selectFrom(member)
            .fetch()

        //단 건
        val findMember1: Member? = jpaQueryFactory
            .selectFrom(member)
            .fetchOne()

        //처음 한 건 조회
        val findMember2: Member? = jpaQueryFactory
            .selectFrom(member)
            .fetchFirst()

        /* 개수 (deprecated)
        val findCount: Long = jpaQueryFactory
            .selectFrom(member)
            .fetchCount()
        */
        val findCount: Long? = jpaQueryFactory
            .select(member.count())
            .from(member)
            .fetchOne()

        /* 페이징에서 사용 (deprecated)
        val results: QueryResults<Member> = jpaQueryFactory
            .selectFrom(member)
            .fetchResults()
        */
    }
}