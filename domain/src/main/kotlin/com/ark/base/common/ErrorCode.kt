package com.ark.base.common

enum class ErrorCode(
    val message: String,
) {
    // Common
    NOT_FOUND("리소스를 찾을 수 없습니다."),
    INVALID_INPUT("잘못된 입력입니다."),

    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_INVALID_EMAIL("유효하지 않은 이메일 형식입니다."),
    USER_BLANK_NAME("이름은 필수입니다."),
    USER_NAME_TOO_LONG("이름은 100자를 초과할 수 없습니다."),
    USER_DUPLICATE_EMAIL("이미 등록된 이메일입니다."),
    USER_LOGIN_FAILED("이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_RESET_TOKEN_INVALID("유효하지 않거나 만료된 토큰입니다."),

    // Order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),

    // Inventory
    INVENTORY_NOT_FOUND("재고를 찾을 수 없습니다."),
    STOCK_INSUFFICIENT("재고가 부족합니다."),
}
