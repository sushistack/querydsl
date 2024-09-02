package com.sushistack.querydsl.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(private val entityManager: EntityManager) {

    /**
     * 1. jpaQueryFactory 는 entityManager 에 의존
     * 2. entityManager 는 spring 에서 동작 시, 실제로는 proxy 로 넣어지고 각 tx 마다 독립적으로 실행되도록 하기에 동시성 문제가 없어진다.
     */
    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(entityManager)
}