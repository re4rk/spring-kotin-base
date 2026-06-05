package com.ark.base.ui

import com.ark.base.application.OAuthAuthorizationResponse
import com.ark.base.application.OAuthCallbackRequest
import com.ark.base.application.OAuthService
import com.ark.base.application.TokenResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/oauth")
class OAuthController(
    private val oauthService: OAuthService,
) {
    @GetMapping("/{provider}/authorize")
    fun authorize(
        @PathVariable provider: String,
        @RequestParam redirectUri: String,
    ): OAuthAuthorizationResponse = oauthService.getAuthorizationUrl(provider, redirectUri)

    @PostMapping("/{provider}/callback")
    fun callback(
        @PathVariable provider: String,
        @RequestBody request: OAuthCallbackRequest,
    ): TokenResponse = oauthService.callback(provider, request)
}
