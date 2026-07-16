-- blog_posts: 블로그 포스트 (DRAFT → PUBLISHED / ARCHIVED)
CREATE TABLE blog_posts
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '포스트 PK',
    title      VARCHAR(200) NOT NULL               COMMENT '제목',
    content    TEXT         NOT NULL               COMMENT '본문',
    author_id  BIGINT       NOT NULL               COMMENT '작성자 유저 ID',
    status     VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '상태 (DRAFT / PUBLISHED / ARCHIVED)',
    published_at DATETIME(6)                        COMMENT '발행 일시',

    created_at DATETIME(6)  NOT NULL               COMMENT '생성 일시',
    created_by VARCHAR(255)                         COMMENT '생성 주체',
    updated_at DATETIME(6)  NOT NULL               COMMENT '마지막 수정 일시',
    updated_by VARCHAR(255)                         COMMENT '마지막 수정 주체',

    deleted_at DATETIME(6)                          COMMENT '삭제 일시',
    deleted_by VARCHAR(255)                         COMMENT '삭제 주체',
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '삭제 여부'
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '블로그 포스트';

CREATE INDEX idx_blog_posts_status ON blog_posts (status);
CREATE INDEX idx_blog_posts_author_id ON blog_posts (author_id);
CREATE INDEX idx_blog_posts_created_at ON blog_posts (created_at);
