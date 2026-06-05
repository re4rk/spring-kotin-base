package com.ark.base.common

enum class ErrorCode(
    val message: String,
) {
    // Common
    NOT_FOUND("리소스를 찾을 수 없습니다."),
    INVALID_INPUT("잘못된 입력입니다."),
    ACCESS_DENIED("접근 권한이 없습니다."),
    UNAUTHORIZED("인증이 필요합니다."),

    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_INVALID_EMAIL("유효하지 않은 이메일 형식입니다."),
    USER_BLANK_NAME("이름은 필수입니다."),
    USER_NAME_TOO_LONG("이름은 100자를 초과할 수 없습니다."),
    USER_DUPLICATE_EMAIL("이미 등록된 이메일입니다."),
    USER_LOGIN_FAILED("이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_RESET_TOKEN_INVALID("유효하지 않거나 만료된 토큰입니다."),
    USER_REFRESH_TOKEN_INVALID("유효하지 않거나 만료된 리프레시 토큰입니다."),
    USER_REFRESH_TOKEN_REUSED("리프레시 토큰이 재사용되어 모든 세션이 무효화되었습니다."),

    // Product
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    PRODUCT_INVALID_STATUS("현재 상태에서 해당 작업을 수행할 수 없습니다."),
    PRODUCT_OPTION_GROUP_NOT_FOUND("상품 옵션 그룹을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND("상품 옵션을 찾을 수 없습니다."),
    PRODUCT_SKU_NOT_FOUND("상품 SKU를 찾을 수 없습니다."),
    PRODUCT_OPTION_IN_USE("해당 옵션을 사용 중인 SKU가 있어 삭제할 수 없습니다."),

    // Order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELLED("이미 취소된 주문입니다."),
    ORDER_INVALID_STATUS("현재 상태에서 해당 작업을 수행할 수 없습니다."),

    STOCK_INSUFFICIENT("재고가 부족합니다."),
    STOCK_CONCURRENT_MODIFICATION("재고가 다른 요청에 의해 변경되었습니다. 다시 시도해 주세요."),

    // File
    FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
    FILE_INVALID_TYPE("지원하지 않는 파일 형식입니다. 이미지 파일만 업로드 가능합니다."),
    FILE_EMPTY("파일이 비어있습니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),

    // OAuth
    OAUTH_INVALID_STATE("유효하지 않거나 만료된 OAuth state입니다."),
    OAUTH_PROVIDER_ERROR("OAuth 공급자로부터 오류 응답을 받았습니다."),
    OAUTH_EMAIL_REQUIRED("OAuth 공급자로부터 이메일 정보를 가져올 수 없습니다."),
    OAUTH_UNSUPPORTED_PROVIDER("지원하지 않는 OAuth 공급자입니다."),
}
