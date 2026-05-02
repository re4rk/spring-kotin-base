package com.ark.base.common

enum class ErrorCode(val code: String, val message: String) {

    // Common
    NOT_FOUND("C001", "리소스를 찾을 수 없습니다."),
    INVALID_INPUT("C002", "잘못된 입력입니다."),

    // User
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),

    // Order
    ORDER_NOT_FOUND("O001", "주문을 찾을 수 없습니다."),

    // Inventory
    INVENTORY_NOT_FOUND("I001", "재고를 찾을 수 없습니다."),
    STOCK_INSUFFICIENT("I002", "재고가 부족합니다."),
}
