package com.ark.blog.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.blog.post.Post
import com.ark.blog.post.PostRepository
import com.ark.blog.post.PostStatus
import com.ark.blog.post.findByIdOrThrow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional(readOnly = true)
    fun listPosts(pageable: Pageable): Page<Post> = postRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun listPublished(pageable: Pageable): Page<PostResponse> =
        postRepository.findAllByStatus(PostStatus.PUBLISHED, pageable).map { PostResponse.from(it) }

    @Transactional(readOnly = true)
    fun getById(postId: Long): PostResponse = PostResponse.from(postRepository.findByIdOrThrow(postId))

    @Transactional(readOnly = true)
    fun getPublishedById(postId: Long): PostResponse {
        val post = postRepository.findByIdOrThrow(postId)
        if (post.status != PostStatus.PUBLISHED) {
            throw BaseException(ErrorCode.BLOG_POST_NOT_FOUND)
        }
        return PostResponse.from(post)
    }

    @Transactional
    fun create(
        authorId: Long,
        request: PostCreateRequest,
    ): PostResponse {
        val post =
            postRepository.save(
                Post(
                    title = request.title.trim(),
                    content = request.content.trim(),
                    authorId = authorId,
                ),
            )
        return PostResponse.from(post)
    }

    @Transactional
    fun update(
        postId: Long,
        request: PostUpdateRequest,
    ): PostResponse {
        val post = postRepository.findByIdOrThrow(postId)
        post.update(request.title.trim(), request.content.trim())
        return PostResponse.from(post)
    }

    @Transactional
    fun publish(postId: Long): PostResponse {
        val post = postRepository.findByIdOrThrow(postId)
        post.publish()
        return PostResponse.from(post)
    }

    @Transactional
    fun unpublish(postId: Long): PostResponse {
        val post = postRepository.findByIdOrThrow(postId)
        post.unpublish()
        return PostResponse.from(post)
    }

    @Transactional
    fun archive(postId: Long): PostResponse {
        val post = postRepository.findByIdOrThrow(postId)
        post.archive()
        return PostResponse.from(post)
    }

    @Transactional
    fun delete(
        postId: Long,
        deletedBy: String,
    ) {
        val post = postRepository.findByIdOrThrow(postId)
        post.delete(deletedBy)
    }
}
