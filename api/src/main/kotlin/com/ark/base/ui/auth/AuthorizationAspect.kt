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
) {
    @Before("@annotation(authorize)")
    fun authorize(
        joinPoint: JoinPoint,
        authorize: Authorize,
    ) {
        when (authorize.value) {
            AccessType.SELF -> accessGuard.requireSelf(resolveLong(joinPoint, authorize.param))
            AccessType.SELF_BY_EMAIL -> accessGuard.requireSelfByEmail(resolveString(joinPoint, authorize.param))
            AccessType.PRODUCT_OWNER -> accessGuard.requireProductOwner(resolveLong(joinPoint, authorize.param))
            AccessType.ORDER_BUYER -> accessGuard.requireOrderBuyer(resolveLong(joinPoint, authorize.param))
            AccessType.ORDER_SELLER -> accessGuard.requireOrderSeller(resolveLong(joinPoint, authorize.param))
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
