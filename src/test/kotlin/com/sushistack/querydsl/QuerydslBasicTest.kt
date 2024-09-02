package com.sushistack.querydsl

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.QMember.*
import com.sushistack.querydsl.entity.QTeam.team
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

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)  */
    @Test
    fun sort() {
        entityManager.persist(Member(username = null, age = 100))
        entityManager.persist(Member(username = "member5", age = 100))
        entityManager.persist(Member(username = "member6", age = 100))
        val result: List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch()
        val member5 = result[0]
        val member6 = result[1]
        val memberNull = result[2]
        assertThat(member5.username).isEqualTo("member5")
        assertThat(member6.username).isEqualTo("member6")
        assertThat(memberNull.username).isNull()
    }

    @Test
    fun paging1() {
        val result: List<Member> = jpaQueryFactory
            .selectFrom(member)
            .orderBy(member.username.desc()).offset(1) //0부터 시작(zero index) .limit(2) //최대 2건 조회
            .fetch()

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun paging2() {
        val queryResults: QueryResults<Member> = jpaQueryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetchResults() // deprecated

        assertThat(queryResults.total).isEqualTo(4)
        assertThat(queryResults.limit).isEqualTo(2)
        assertThat(queryResults.offset).isEqualTo(1)
        assertThat(queryResults.results.size).isEqualTo(2)
    }

    /**
     * JPQL
     * select
     * COUNT(m),
     * SUM(m.age),
     * AVG(m.age),
     * MAX(m.age),
     * MIN(m.age)
     * from Member m
     */
    //회원수 //나이 합 //평균 나이 //최대 나이 //최소 나이
    @Test
    @Throws(Exception::class)
    fun aggregation() {
        val result: List<Tuple> = jpaQueryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
            )
            .from(member)
            .fetch()

        val tuple: Tuple = result[0]

        assertThat(tuple.get(member.count())).isEqualTo(4)
        assertThat(tuple.get(member.age.sum())).isEqualTo(100)
        assertThat(tuple.get(member.age.avg())).isEqualTo(25.0)
        assertThat(tuple.get(member.age.max())).isEqualTo(40)
        assertThat(tuple.get(member.age.min())).isEqualTo(10)
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라. */
    @Test
    @Throws(java.lang.Exception::class)
    fun group() {
        val result: List<Tuple> = jpaQueryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch()
        val teamA = result[0]
        val teamB = result[1]

        assertThat(teamA.get(team.name)).isEqualTo("teamA")
        assertThat(teamA.get(member.age.avg())).isEqualTo(15.0)
        assertThat(teamB.get(team.name)).isEqualTo("teamB")
        assertThat(teamB.get(member.age.avg())).isEqualTo(35.0)
    }

    /**
     * 팀 A에 소속된 모든 회원
    */
    @Test
    @Throws(java.lang.Exception::class)
    fun join() {
        val result = jpaQueryFactory
                .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("member1", "member2")
    }


    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     * SELECT FROM Member m, Team t
     * CROSS JOIN, 카테시안 곱
     * */
    @Test
    fun theta_join() {
        entityManager.persist(Member (username = "teamA"))
        entityManager.persist(Member (username = "teamB"))
        val result = jpaQueryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("teamA", "teamB");
    }

    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
     */
    @Test
    fun join_on_filtering() {
        jpaQueryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
            .fetch().forEach {
                println("tuple := $it")
            }
    }

    /**
     * 2. 연관관계 없는 엔티티 외부 조인
     * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun join_on_no_relation() {
        entityManager.persist(Member(username = "teamA"))
        entityManager.persist(Member(username = "teamB"))
        val result: List<Tuple> = jpaQueryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name))
            // leftJoin(member.team, team) -> 연관 관계가 있다.
            .fetch()

        for (tuple in result) {
            println("t=$tuple")
        }
    }
}