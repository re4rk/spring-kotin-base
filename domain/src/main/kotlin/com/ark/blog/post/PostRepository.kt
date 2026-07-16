package com.ark.blog.post

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    fun findAllByStatus(
        status: PostStatus,
        pageable: Pageable,
    ): Page<Post>
}

fun PostRepository.findByIdOrThrow(id: Long): Post = findById(id).orElseThrow { BaseException(ErrorCode.BLOG_POST_NOT_FOUND) }
