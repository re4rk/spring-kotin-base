package com.ark.base.auth.oauth

import org.springframework.data.repository.CrudRepository

interface OAuthStateRepository : CrudRepository<OAuthState, String>
