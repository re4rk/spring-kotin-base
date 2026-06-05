# base

## .env 템플릿

아래 내용을 프로젝트 루트에 `.env` 파일로 복사한 뒤 값을 채워 사용하세요.  
`*` 표시된 항목은 운영 환경에서 반드시 교체해야 합니다.

```dotenv
# -----------------------------------------------------------------------
# Database (MySQL 8.0)
# -----------------------------------------------------------------------
DB_URL=jdbc:mysql://localhost:3306/base?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
DB_USERNAME=base
DB_PASSWORD=base

# -----------------------------------------------------------------------
# JWT  *
# -----------------------------------------------------------------------
JWT_SECRET=local-dev-secret-key-minimum-256-bits-long

# -----------------------------------------------------------------------
# Email (SMTP)
#   로컬: Mailpit (docker compose up -d)
#   운영: MAIL_SMTP_AUTH=true, MAIL_SMTP_STARTTLS=true 후 자격증명 입력
# -----------------------------------------------------------------------
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_SMTP_AUTH=false
MAIL_SMTP_STARTTLS=false
EMAIL_FROM=noreply@base.local

# -----------------------------------------------------------------------
# MinIO (S3-compatible)
#   로컬: http://localhost:9001 (minioadmin / minioadmin)
#   운영: AWS S3 등 외부 엔드포인트로 교체  *
# -----------------------------------------------------------------------
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=files
```
