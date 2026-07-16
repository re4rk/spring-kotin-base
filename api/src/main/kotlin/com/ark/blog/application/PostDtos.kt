package com.ark.blog.application

import com.ark.blog.post.Post
import com.ark.blog.post.PostStatus
import java.time.LocalDateTime

data class PostCreateRequest(
    val title: String,
    val content: String,
)

data class PostUpdateRequest(
    val title: String,
    val content: String,
)

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
    val status: PostStatus,
    val publishedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(post: Post) =
            PostResponse(
                id = post.id,
                title = post.title,
                content = post.content,
                authorId = post.authorId,
                status = post.status,
                publishedAt = post.publishedAt,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            )
    }
}
