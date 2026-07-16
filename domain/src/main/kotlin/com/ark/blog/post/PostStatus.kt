package com.ark.blog.post

enum class PostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED,
    ;

    fun canTransitionTo(target: PostStatus): Boolean =
        when (this) {
            DRAFT -> target == PUBLISHED || target == ARCHIVED
            PUBLISHED -> target == DRAFT || target == ARCHIVED
            ARCHIVED -> target == DRAFT
        }
}
