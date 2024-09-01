package com.sushistack.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.querydsl.entity.Hello
import com.sushistack.querydsl.entity.QHello
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@Rollback(false)
class QuerydslApplicationTests {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @Test
    fun contextLoads() {
        val hello = Hello()
        entityManager.persist(hello)

        val qHello = QHello("h")

        val result = jpaQueryFactory
            .selectFrom(qHello)
            .fetchOne()

        assertThat(result).isEqualTo(hello)
        assertThat(result?.id).isEqualTo(hello.id)
    }

}
