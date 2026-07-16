package com.ark.blog.ui

import com.ark.base.common.CurrentUser
import com.ark.base.ui.auth.InjectCurrentUser
import com.ark.blog.application.PostCreateRequest
import com.ark.blog.application.PostResponse
import com.ark.blog.application.PostService
import com.ark.blog.application.PostUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
) {
    @GetMapping
    fun listPublished(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): Page<PostResponse> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt", "id"))
        return postService.listPublished(pageable)
    }

    @GetMapping("/{id}")
    fun getPublished(
        @PathVariable id: Long,
    ): PostResponse = postService.getPublishedById(id)

    @PostMapping
    fun create(
        @InjectCurrentUser currentUser: CurrentUser,
        @RequestBody request: PostCreateRequest,
    ): PostResponse = postService.create(currentUser.requireUserId(), request)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: PostUpdateRequest,
    ): PostResponse = postService.update(id, request)

    @PostMapping("/{id}/publish")
    fun publish(
        @PathVariable id: Long,
    ): PostResponse = postService.publish(id)

    @PostMapping("/{id}/unpublish")
    fun unpublish(
        @PathVariable id: Long,
    ): PostResponse = postService.unpublish(id)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @InjectCurrentUser currentUser: CurrentUser,
    ) = postService.delete(id, currentUser.requireUserId().toString())
}
