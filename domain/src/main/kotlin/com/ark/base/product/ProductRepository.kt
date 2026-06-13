package com.ark.base.product

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {
    @Query(
        """
        SELECT p FROM Product p
        WHERE (:#{#query.status} IS NULL OR p.status = :#{#query.status})
          AND (:#{#query.name} IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#query.name}, '%')))
          AND (:#{#query.minPrice} IS NULL OR p.price >= :#{#query.minPrice})
          AND (:#{#query.maxPrice} IS NULL OR p.price <= :#{#query.maxPrice})
          AND (:#{#query.category} IS NULL OR p.category = :#{#query.category})
        """,
    )
    fun findAllByFilter(
        @Param("query") query: ProductQueryFilter,
    ): List<Product>

    @Query(
        value = """
        SELECT p FROM Product p
        WHERE (:#{#query.status} IS NULL OR p.status = :#{#query.status})
          AND (:#{#query.name} IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#query.name}, '%')))
          AND (:#{#query.minPrice} IS NULL OR p.price >= :#{#query.minPrice})
          AND (:#{#query.maxPrice} IS NULL OR p.price <= :#{#query.maxPrice})
          AND (:#{#query.category} IS NULL OR p.category = :#{#query.category})
        """,
        countQuery = """
        SELECT COUNT(p) FROM Product p
        WHERE (:#{#query.status} IS NULL OR p.status = :#{#query.status})
          AND (:#{#query.name} IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#query.name}, '%')))
          AND (:#{#query.minPrice} IS NULL OR p.price >= :#{#query.minPrice})
          AND (:#{#query.maxPrice} IS NULL OR p.price <= :#{#query.maxPrice})
          AND (:#{#query.category} IS NULL OR p.category = :#{#query.category})
        """,
    )
    fun findAllByFilter(
        @Param("query") query: ProductQueryFilter,
        pageable: Pageable,
    ): Page<Product>
}

fun ProductRepository.findByIdOrThrow(id: Long): Product = findById(id).orElseThrow { BaseException(ErrorCode.PRODUCT_NOT_FOUND) }
