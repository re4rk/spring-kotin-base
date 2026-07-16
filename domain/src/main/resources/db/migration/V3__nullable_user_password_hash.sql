-- OAuth-only users (e.g. Kakao) may not have a password until set in settings.
ALTER TABLE base_users
    MODIFY COLUMN password_hash VARCHAR(255) NULL COMMENT 'BCrypt 해시 비밀번호 (소셜만 가입 시 NULL 가능)';
