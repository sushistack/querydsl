package com.sushistack.querydsl.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.entity.Member
import com.sushistack.querydsl.entity.QMember.member
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MemberJpaRepository(
    private val entityManager: EntityManager,
    private val queryFactory: JPAQueryFactory
) {
    fun save(member: Member) = entityManager.persist(member)

    fun findById(id: Long) = entityManager.find(Member::class.java, id)

    fun findAll() = entityManager.createQuery("SELECT m FROM Member m", Member::class.java).resultList

    fun findAllQuerydsl() =
        queryFactory
            .selectFrom(member)
            .fetch()

    fun findByUsername(username: String) =
        entityManager.createQuery("SELECT m FROM Member m WHERE m.username = :username", Member::class.java)
            .setParameter("username", username)
            .resultList

    fun findByUsernameQuerydsl(username: String) =
        queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch()
}