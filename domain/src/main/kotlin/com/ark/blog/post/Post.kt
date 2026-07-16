package com.ark.blog.post

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "blog_posts")
@SQLRestriction("deleted_at IS NULL")
class Post(
    title: String,
    content: String,
    val authorId: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Post>() {
    @Column(nullable = false, length = 200)
    var title: String = title

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String = content

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PostStatus = PostStatus.DRAFT

    var publishedAt: LocalDateTime? = null

    init {
        validateTitle(title)
        validateContent(content)
    }

    fun update(
        title: String,
        content: String,
    ) {
        validateTitle(title)
        validateContent(content)
        this.title = title
        this.content = content
    }

    fun publish() {
        transitionTo(PostStatus.PUBLISHED)
        if (publishedAt == null) {
            publishedAt = LocalDateTime.now()
        }
    }

    fun unpublish() {
        transitionTo(PostStatus.DRAFT)
    }

    fun archive() {
        transitionTo(PostStatus.ARCHIVED)
    }

    fun delete(by: String? = null) {
        deletedAt = LocalDateTime.now()
        deletedBy = by
        isDeleted = true
    }

    private fun transitionTo(target: PostStatus) {
        if (!status.canTransitionTo(target)) throw BaseException(ErrorCode.BLOG_POST_INVALID_STATUS)
        status = target
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 200

        private fun validateTitle(title: String) {
            if (title.isBlank()) throw BaseException(ErrorCode.INVALID_INPUT)
            if (title.length > MAX_TITLE_LENGTH) throw BaseException(ErrorCode.INVALID_INPUT)
        }

        private fun validateContent(content: String) {
            if (content.isBlank()) throw BaseException(ErrorCode.INVALID_INPUT)
        }
    }
}
