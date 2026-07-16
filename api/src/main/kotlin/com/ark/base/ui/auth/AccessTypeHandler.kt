package com.ark.base.ui.auth

/**
 * Domain-specific access checks (e.g. commerce) register implementations.
 * Base keeps only user-centric checks in [AccessGuard].
 */
interface AccessTypeHandler {
    val type: AccessType

    fun authorize(param: Any)
}
