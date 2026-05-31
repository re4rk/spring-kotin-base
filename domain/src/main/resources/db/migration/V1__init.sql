-- =============================================================================
-- 프로젝트: base
-- 설명: 전체 도메인 스키마 초기화
--
-- 도메인 구조
--   users          - 사용자 계정 (소프트 삭제)
--   product       - 상품 (DRAFT → PENDING → ON_SALE / SOLD_OUT / DISCONTINUED)
--   inventory     - 상품별 재고 (낙관적 락, product 1:1)
--   orders        - 주문 (PLACED → CONFIRMED → SHIPPING → DELIVERED / CANCELLED)
--   file_metadata - 업로드 파일 메타데이터 (실제 파일은 MinIO 오브젝트 스토리지)
--
-- 공통 패턴
--   - 모든 테이블은 created_at/created_by/updated_at/updated_by 감사(audit) 컬럼 보유
--   - 삭제는 deleted_at/deleted_by/is_deleted 를 통한 소프트 삭제
--   - 소프트 삭제 적용 테이블(users, file_metadata)은 @SQLRestriction("deleted_at IS NULL") 으로 자동 필터
-- =============================================================================

-- =============================================================================
-- users: 사용자 계정
--   - 비밀번호는 BCrypt 해시 후 저장 (평문 저장 없음)
--   - 이메일 형식·이름 길이(max 100)는 도메인 레이어에서 검증
--   - @SQLRestriction 으로 deleted_at IS NULL 조건이 모든 쿼리에 자동 적용
-- =============================================================================
CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY     COMMENT '사용자 PK',
    email         VARCHAR(255) NOT NULL                 COMMENT '로그인 이메일 (형식 검증: ^[A-Za-z0-9+_.-]+@...)',
    name          VARCHAR(100) NOT NULL                 COMMENT '표시 이름 (공백 불가, max 100)',
    password_hash VARCHAR(255) NOT NULL                 COMMENT 'BCrypt 해시 비밀번호',

    -- audit
    created_at    DATETIME(6)  NOT NULL                 COMMENT '생성 일시',
    created_by    VARCHAR(255)                          COMMENT '생성 주체 (Spring Data Auditing)',
    updated_at    DATETIME(6)  NOT NULL                 COMMENT '마지막 수정 일시',
    updated_by    VARCHAR(255)                          COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at    DATETIME(6)                           COMMENT '삭제 일시 (NULL = 활성)',
    deleted_by    VARCHAR(255)                          COMMENT '삭제 주체',
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE   COMMENT '삭제 여부 플래그'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '사용자 계정';

-- =============================================================================
-- product: 상품
--   - 상태 머신: DRAFT → PENDING → ON_SALE
--                                 ↓         ↓
--                              SOLD_OUT  DISCONTINUED
--   - 재고는 inventory 테이블에서 별도 관리 (product_id 1:1)
-- =============================================================================
CREATE TABLE product
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY    COMMENT '상품 PK',
    name          VARCHAR(255) NOT NULL                COMMENT '상품명',
    price         BIGINT       NOT NULL                COMMENT '가격 (원 단위)',
    status        VARCHAR(50)  NOT NULL                COMMENT '상태: DRAFT|PENDING|ON_SALE|SOLD_OUT|DISCONTINUED',
    description   TEXT                                 COMMENT '상품 설명',
    category      VARCHAR(100)                         COMMENT '카테고리',
    thumbnail_url VARCHAR(500)                         COMMENT '썸네일 이미지 URL',

    -- audit
    created_at DATETIME(6)  NOT NULL                COMMENT '생성 일시',
    created_by VARCHAR(255)                         COMMENT '생성 주체',
    updated_at DATETIME(6)  NOT NULL                COMMENT '마지막 수정 일시',
    updated_by VARCHAR(255)                         COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at DATETIME(6)                          COMMENT '삭제 일시',
    deleted_by VARCHAR(255)                         COMMENT '삭제 주체',
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE  COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '상품';

-- =============================================================================
-- product_option_group: 상품 옵션 그룹
--   - product 와 N:1 관계 (product_id FK)
--   - 예: 색상, 사이즈
-- =============================================================================
CREATE TABLE product_option_group
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY    COMMENT '옵션 그룹 PK',
    product_id BIGINT       NOT NULL                COMMENT '상품 ID (product.id 참조)',
    name       VARCHAR(100) NOT NULL                COMMENT '옵션 그룹명 (예: 색상, 사이즈)',
    sort_order INT          NOT NULL DEFAULT 0      COMMENT '노출 순서',

    -- audit
    created_at DATETIME(6)  NOT NULL                COMMENT '생성 일시',
    created_by VARCHAR(255)                         COMMENT '생성 주체',
    updated_at DATETIME(6)  NOT NULL                COMMENT '마지막 수정 일시',
    updated_by VARCHAR(255)                         COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at DATETIME(6)                          COMMENT '삭제 일시',
    deleted_by VARCHAR(255)                         COMMENT '삭제 주체',
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE  COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '상품 옵션 그룹';

-- =============================================================================
-- product_option: 상품 옵션
--   - product_option_group 과 N:1 관계
--   - 재고·추가금액은 product_sku 에서 조합 단위로 관리
--   - 예: 빨강, XL
-- =============================================================================
CREATE TABLE product_option
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY    COMMENT '옵션 PK',
    option_group_id BIGINT       NOT NULL                COMMENT '옵션 그룹 ID (product_option_group.id 참조)',
    name            VARCHAR(100) NOT NULL                COMMENT '옵션명 (예: 빨강, XL)',
    sort_order      INT          NOT NULL DEFAULT 0      COMMENT '노출 순서',

    -- audit
    created_at      DATETIME(6)  NOT NULL                COMMENT '생성 일시',
    created_by      VARCHAR(255)                         COMMENT '생성 주체',
    updated_at      DATETIME(6)  NOT NULL                COMMENT '마지막 수정 일시',
    updated_by      VARCHAR(255)                         COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at      DATETIME(6)                          COMMENT '삭제 일시',
    deleted_by      VARCHAR(255)                         COMMENT '삭제 주체',
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE  COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '상품 옵션 (이름·순서 정의)';

-- =============================================================================
-- product_sku: 상품 SKU (옵션 조합별 재고·추가금액)
--   - product 와 N:1 관계 (product_id FK)
--   - 조합 예: 사이즈:S + 색상:빨강 → stock 10, extraPrice 0
--   - version: Hibernate 낙관적 락
-- =============================================================================
CREATE TABLE product_sku
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY    COMMENT 'SKU PK',
    product_id  BIGINT       NOT NULL                COMMENT '상품 ID (product.id 참조)',
    extra_price BIGINT       NOT NULL DEFAULT 0      COMMENT '추가 금액 (원 단위)',
    stock       INT          NOT NULL                COMMENT '재고 수량',
    version     BIGINT       NOT NULL DEFAULT 0      COMMENT 'Hibernate 낙관적 락 버전',

    -- audit
    created_at  DATETIME(6)  NOT NULL                COMMENT '생성 일시',
    created_by  VARCHAR(255)                         COMMENT '생성 주체',
    updated_at  DATETIME(6)  NOT NULL                COMMENT '마지막 수정 일시',
    updated_by  VARCHAR(255)                         COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at  DATETIME(6)                          COMMENT '삭제 일시',
    deleted_by  VARCHAR(255)                         COMMENT '삭제 주체',
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE  COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '상품 SKU (옵션 조합별 재고)';

-- =============================================================================
-- product_sku_option: SKU ↔ 옵션 매핑 (ManyToMany 조인 테이블)
-- =============================================================================
CREATE TABLE product_sku_option
(
    sku_id    BIGINT NOT NULL COMMENT 'SKU ID (product_sku.id 참조)',
    option_id BIGINT NOT NULL COMMENT '옵션 ID (product_option.id 참조)',
    PRIMARY KEY (sku_id, option_id)
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'SKU-옵션 매핑';

-- =============================================================================
-- orders: 주문
--   - 상태 머신: PLACED → CONFIRMED → SHIPPING → DELIVERED
--                                               ↓
--                                           CANCELLED
--   - product_name 은 주문 시점 상품명 스냅샷 (이후 상품명 변경과 무관하게 보존)
--   - user_id / product_id 는 FK 없이 ID 참조 (도메인 레이어에서 관리)
-- =============================================================================
CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '주문 PK',
    user_id      BIGINT       NOT NULL                  COMMENT '주문자 ID (users.id 참조)',
    product_id   BIGINT       NOT NULL                  COMMENT '상품 ID (product.id 참조)',
    sku_id       BIGINT       NOT NULL                  COMMENT 'SKU ID (product_sku.id 참조)',
    product_name VARCHAR(255) NOT NULL                  COMMENT '주문 시점 상품명 스냅샷',
    quantity     INT          NOT NULL                  COMMENT '주문 수량',
    status       VARCHAR(50)  NOT NULL                  COMMENT '상태: PLACED|CONFIRMED|SHIPPING|DELIVERED|CANCELLED',

    -- audit
    created_at   DATETIME(6)  NOT NULL                  COMMENT '주문 일시',
    created_by   VARCHAR(255)                           COMMENT '생성 주체',
    updated_at   DATETIME(6)  NOT NULL                  COMMENT '마지막 수정 일시',
    updated_by   VARCHAR(255)                           COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at   DATETIME(6)                            COMMENT '삭제 일시',
    deleted_by   VARCHAR(255)                           COMMENT '삭제 주체',
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE    COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '주문';

-- =============================================================================
-- file_metadata: 업로드 파일 메타데이터
--   - 실제 파일은 MinIO(S3 호환) 오브젝트 스토리지에 저장
--   - original_name: 사용자가 업로드한 원본 파일명
--   - stored_name: UUID 기반으로 재명명된 스토리지 내 파일명 (충돌 방지)
--   - path: bucket 내 오브젝트 경로 (bucket + path 로 MinIO 접근)
--   - @SQLRestriction 으로 deleted_at IS NULL 조건이 모든 쿼리에 자동 적용
-- =============================================================================
CREATE TABLE file_metadata
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY     COMMENT '파일 메타데이터 PK',
    original_name VARCHAR(255) NOT NULL                 COMMENT '원본 파일명',
    stored_name   VARCHAR(255) NOT NULL                 COMMENT 'MinIO 저장 파일명 (UUID 기반)',
    mime_type     VARCHAR(255) NOT NULL                 COMMENT 'MIME 타입 (예: image/png)',
    size          BIGINT       NOT NULL                 COMMENT '파일 크기 (bytes)',
    bucket        VARCHAR(255) NOT NULL                 COMMENT 'MinIO 버킷명',
    path          VARCHAR(255) NOT NULL                 COMMENT 'MinIO 오브젝트 경로',

    -- audit
    created_at    DATETIME(6)  NOT NULL                 COMMENT '업로드 일시',
    created_by    VARCHAR(255)                          COMMENT '업로드 주체',
    updated_at    DATETIME(6)  NOT NULL                 COMMENT '마지막 수정 일시',
    updated_by    VARCHAR(255)                          COMMENT '마지막 수정 주체',

    -- soft delete
    deleted_at    DATETIME(6)                           COMMENT '삭제 일시 (MinIO 파일도 함께 삭제됨)',
    deleted_by    VARCHAR(255)                          COMMENT '삭제 주체',
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE   COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '업로드 파일 메타데이터 (실제 파일은 MinIO)';
