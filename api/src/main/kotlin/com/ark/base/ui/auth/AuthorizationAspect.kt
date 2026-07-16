package com.ark.base.ui.auth

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class AuthorizationAspect(
    private val accessGuard: AccessGuard,
    private val accessTypeHandlers: List<AccessTypeHandler>,
) {
    @Before("@annotation(authorize)")
    fun authorize(
        joinPoint: JoinPoint,
        authorize: Authorize,
    ) {
        when (authorize.value) {
            AccessType.SELF -> accessGuard.requireSelf(resolveLong(joinPoint, authorize.param))
            AccessType.SELF_BY_EMAIL -> accessGuard.requireSelfByEmail(resolveString(joinPoint, authorize.param))
            else -> {
                val handler =
                    accessTypeHandlers.find { it.type == authorize.value }
                        ?: error("No AccessTypeHandler registered for ${authorize.value}")
                handler.authorize(resolveParam(joinPoint, authorize.param))
            }
        }
    }

    private fun resolveLong(
        joinPoint: JoinPoint,
        paramName: String,
    ): Long = resolveParam(joinPoint, paramName) as Long

    private fun resolveString(
        joinPoint: JoinPoint,
        paramName: String,
    ): String = resolveParam(joinPoint, paramName) as String

    private fun resolveParam(
        joinPoint: JoinPoint,
        paramName: String,
    ): Any {
        val signature = joinPoint.signature as MethodSignature
        val index = signature.parameterNames.indexOf(paramName)
        require(index >= 0) { "Parameter '$paramName' not found on ${signature.method.name}" }
        return joinPoint.args[index]
            ?: throw IllegalStateException("Parameter '$paramName' is null on ${signature.method.name}")
    }
}
