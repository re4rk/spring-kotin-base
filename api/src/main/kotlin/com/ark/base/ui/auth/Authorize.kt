package com.ark.base.ui.auth

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorize(
    val value: AccessType,
    val param: String,
)
