-- =============================================================================
-- 도메인별 테이블 prefix 적용
--   base_*      - 인증·유저·파일·알림 등 기반 도메인
--   commerce_*  - 상품·주문 도메인
-- =============================================================================

RENAME TABLE
    users TO base_users,
    user_oauth_account TO base_user_oauth_account,
    file_metadata TO base_file_metadata,
    email_log TO base_email_log,
    push_log TO base_push_log,
    kakao_log TO base_kakao_log,
    slack_log TO base_slack_log,
    sms_log TO base_sms_log,
    sse_log TO base_sse_log,
    product TO commerce_product,
    product_option_group TO commerce_product_option_group,
    product_option TO commerce_product_option,
    product_sku TO commerce_product_sku,
    product_sku_option TO commerce_product_sku_option,
    orders TO commerce_orders;
