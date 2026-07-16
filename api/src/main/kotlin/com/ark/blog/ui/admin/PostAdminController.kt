package com.ark.blog.ui.admin

import com.ark.base.user.UserRepository
import com.ark.blog.application.PostCreateRequest
import com.ark.blog.application.PostService
import com.ark.blog.application.PostUpdateRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/posts")
class PostAdminController(
    private val postService: PostService,
    private val userRepository: UserRepository,
) {
    @GetMapping
    fun list(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        model.addAttribute("posts", postService.listPosts(pageable))
        return "blog/admin/posts"
    }

    @GetMapping("/new")
    fun createForm(model: Model): String {
        model.addAttribute("mode", "create")
        return "blog/admin/post-form"
    }

    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: Long,
        model: Model,
    ): String {
        model.addAttribute("post", postService.getById(id))
        model.addAttribute("mode", "edit")
        return "blog/admin/post-form"
    }

    @PostMapping
    fun create(
        authentication: Authentication,
        @RequestParam title: String,
        @RequestParam content: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        val author =
            userRepository.findByEmail(authentication.name)
                ?: return "redirect:/admin/login"
        return runCatching {
            postService.create(author.id, PostCreateRequest(title = title, content = content))
        }.fold(
            onSuccess = {
                redirectAttributes.addFlashAttribute("success", "포스트가 생성되었습니다.")
                "redirect:/admin/posts/${it.id}"
            },
            onFailure = {
                redirectAttributes.addFlashAttribute("error", it.message ?: "생성에 실패했습니다.")
                "redirect:/admin/posts/new"
            },
        )
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam content: String,
        redirectAttributes: RedirectAttributes,
    ): String =
        runCatching {
            postService.update(id, PostUpdateRequest(title = title, content = content))
        }.fold(
            onSuccess = {
                redirectAttributes.addFlashAttribute("success", "포스트가 수정되었습니다.")
                "redirect:/admin/posts/$id"
            },
            onFailure = {
                redirectAttributes.addFlashAttribute("error", it.message ?: "수정에 실패했습니다.")
                "redirect:/admin/posts/$id"
            },
        )

    @PostMapping("/{id}/publish")
    fun publish(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { postService.publish(id) }

    @PostMapping("/{id}/unpublish")
    fun unpublish(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { postService.unpublish(id) }

    @PostMapping("/{id}/archive")
    fun archive(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { postService.archive(id) }

    @PostMapping("/{id}/delete")
    fun delete(
        @PathVariable id: Long,
        authentication: Authentication,
        redirectAttributes: RedirectAttributes,
    ): String {
        runCatching { postService.delete(id, authentication.name) }
            .onSuccess { redirectAttributes.addFlashAttribute("success", "포스트가 삭제되었습니다.") }
            .onFailure { redirectAttributes.addFlashAttribute("error", it.message ?: "삭제에 실패했습니다.") }
        return "redirect:/admin/posts"
    }

    private fun transition(
        id: Long,
        redirectAttributes: RedirectAttributes,
        action: () -> Unit,
    ): String {
        runCatching(action)
            .onSuccess { redirectAttributes.addFlashAttribute("success", "상태가 변경되었습니다.") }
            .onFailure { redirectAttributes.addFlashAttribute("error", it.message ?: "오류가 발생했습니다.") }
        return "redirect:/admin/posts/$id"
    }
}
