# 배포 환경 설정 가이드

## 목차
1. [배포 흐름](#배포-흐름)
2. [GitHub Secrets 목록](#github-secrets-목록)
3. [우분투 서버 설정](#우분투-서버-설정)
4. [배포 트리거 조건](#배포-트리거-조건)
5. [트러블슈팅](#트러블슈팅)

---

## 배포 흐름

```
push to main / dev (frontend/** or api/** or domain/** 변경 시)
       │
       ├─ [build-frontend] Docker 빌드 → ghcr.io/.../base-frontend:<sha>
       └─ [build-backend]  Docker 빌드 → ghcr.io/.../base-backend:<sha>
                │
                ▼
           [deploy]
             SCP docker-compose.yml, nginx/ → 서버
             SSH: .env 생성 → docker compose pull → up -d
```

**서비스 구성**

```
  클라이언트
  :80  ──▶ nginx (301 redirect) ──▶ :443
  :443 ──▶ nginx (Let's Encrypt SSL)   ← 유일한 외부 진입점
               │   /api/     → backend:8080  (내부)
               │   /actuator → 403 차단
               │   /         → frontend:80   (내부)
               └──────┬──────────────┬──────────────
                      │              │
          ┌───────────▼───┐  ┌───────▼──────────────┐
          │  backend      │  │  frontend             │
          │  :8080 (내부) │  │  :80  (내부, SPA)    │
          │  MySQL/Redis  │  └──────────────────────┘
          │  MinIO        │
          └───────────────┘

  MySQL, Redis, MinIO — 모두 내부 네트워크 전용 (외부 포트 없음)
  MinIO 콘솔 접근: ssh -L 9001:localhost:9001 user@서버IP
```

---

## GitHub Secrets 목록

**위치:** `Settings → Secrets and variables → Actions → New repository secret`

### SSH / 배포 인프라

| Secret | 예시 | 설명 |
|---|---|---|
| `SSH_HOST` | `43.201.12.34` | 우분투 서버 IP 또는 도메인 |
| `SSH_USERNAME` | `ubuntu` | SSH 접속 유저명 |
| `SSH_PRIVATE_KEY` | `-----BEGIN RSA PRIVATE KEY-----...` | 서버 SSH 프라이빗 키 전체 내용 |
| `SSH_PORT` | `22` | SSH 포트 (22면 생략 가능) |
| `DEPLOY_PATH` | `/home/ubuntu/app` | 서버의 배포 디렉토리 경로 |

### Database

| Secret | 예시 | 설명 |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | `str0ng-r00t-pw!` | MySQL root 비밀번호 |
| `DB_PASSWORD` | `str0ng-db-pw!` | MySQL `base` 유저 비밀번호 |

### JWT

| Secret | 예시 | 설명 |
|---|---|---|
| `JWT_SECRET` | `minimum-256-bit-random-secret-key-here` | JWT 서명 키 (32자 이상 랜덤 문자열) |

### MinIO (S3 오브젝트 스토리지)

| Secret | 예시 | 설명 |
|---|---|---|
| `MINIO_ACCESS_KEY` | `minioadmin` | MinIO root 유저 (Access Key) |
| `MINIO_SECRET_KEY` | `str0ng-minio-pw!` | MinIO root 비밀번호 (Secret Key) |
| `MINIO_BUCKET` | `files` | 파일 저장 버킷명 |

### SMTP (이메일)

| Secret | 예시 | 설명 |
|---|---|---|
| `EMAIL_FROM` | `noreply@yourdomain.com` | 발신 이메일 주소 |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP 서버 호스트 |
| `MAIL_PORT` | `587` | SMTP 포트 |
| `MAIL_USERNAME` | `your@gmail.com` | SMTP 인증 유저명 |
| `MAIL_PASSWORD` | `gmail-app-password` | SMTP 비밀번호 (Gmail: 앱 비밀번호 사용) |
| `MAIL_SMTP_AUTH` | `true` | SMTP 인증 활성화 여부 |
| `MAIL_SMTP_STARTTLS` | `true` | STARTTLS 활성화 여부 |

### OAuth2 소셜 로그인 (사용하는 것만 설정)

| Secret | 예시 | 설명 |
|---|---|---|
| `GOOGLE_CLIENT_ID` | `123456.apps.googleusercontent.com` | Google OAuth 클라이언트 ID |
| `GOOGLE_CLIENT_SECRET` | `GOCSPX-...` | Google OAuth 시크릿 |
| `GOOGLE_REDIRECT_URI` | `https://yourdomain.com/auth/oauth/google/callback` | Google 리다이렉트 URI |
| `KAKAO_CLIENT_ID` | `abc123...` | Kakao REST API 키 |
| `KAKAO_CLIENT_SECRET` | `...` | Kakao 시크릿 키 |
| `KAKAO_REDIRECT_URI` | `https://yourdomain.com/auth/oauth/kakao/callback` | Kakao 리다이렉트 URI |
| `NAVER_CLIENT_ID` | `...` | Naver 클라이언트 ID |
| `NAVER_CLIENT_SECRET` | `...` | Naver 시크릿 |
| `NAVER_REDIRECT_URI` | `https://yourdomain.com/auth/oauth/naver/callback` | Naver 리다이렉트 URI |

> `GITHUB_TOKEN`은 GitHub이 자동 제공하므로 별도 설정 불필요

---

## 우분투 서버 설정

### 1. Docker 설치

```bash
sudo apt update && sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

### 2. Docker 권한 설정

```bash
sudo usermod -aG docker $USER
newgrp docker
```

### 3. 배포 디렉토리 생성

```bash
mkdir -p /home/ubuntu/app/nginx
mkdir -p /home/ubuntu/app/certbot/www   # Let's Encrypt webroot 챌린지용
```

`DEPLOY_PATH` Secret에 지정한 경로와 동일하게 생성.

### 4. SSL 인증서 발급 (Let's Encrypt, 최초 1회)

```bash
sudo apt install -y certbot

# nginx가 내려가 있는 상태에서 standalone으로 최초 발급
sudo certbot certonly --standalone -d ark-base.site \
  --non-interactive --agree-tos --email re4rks@gmail.com
```

발급 후 `/etc/letsencrypt/renewal/ark-base.site.conf`에서 webroot 방식으로 변경:

```bash
sudo sed -i 's/authenticator = standalone/authenticator = webroot/' \
  /etc/letsencrypt/renewal/ark-base.site.conf

sudo bash -c 'echo "webroot_path = /home/ubuntu/app/certbot/www" \
  >> /etc/letsencrypt/renewal/ark-base.site.conf'
```

이후 nginx가 떠있는 상태에서도 자동 갱신됨. systemd 타이머가 하루 2회 자동 실행.

```bash
# 갱신 시뮬레이션 테스트
sudo certbot renew --dry-run
```

### 5. MinIO 버킷 초기 생성 (최초 1회)

배포 후 MinIO 콘솔(`http://서버IP:9001`)에서 수동으로 버킷 생성.
또는 컨테이너 기동 후:

```bash
# mc (MinIO Client) 설치
docker run --rm --network app_default \
  minio/mc alias set local http://minio:9000 <MINIO_ACCESS_KEY> <MINIO_SECRET_KEY>

docker run --rm --network app_default \
  minio/mc mb local/files
```

### 6. ghcr.io 로그인 (이미지가 private인 경우)

GitHub PAT 발급: `GitHub → Settings → Developer settings → Tokens (classic)`, `read:packages` 권한 필요.

```bash
echo "<GITHUB_PAT>" | docker login ghcr.io -u <GITHUB_USERNAME> --password-stdin
```

### 7. SSH 키 설정 (GitHub Actions → 서버 접속용)

```bash
# 서버에서 키 생성
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/github_actions

# 공개키 등록
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

# 프라이빗 키 내용을 SSH_PRIVATE_KEY Secret에 등록
cat ~/.ssh/github_actions
```

### 8. 방화벽 설정

```bash
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP (HTTPS 리다이렉트용)
sudo ufw allow 443   # HTTPS
sudo ufw enable
```

---

## 배포 트리거 조건

아래 경로 변경 시에만 워크플로 실행됨:

```
- frontend/**
- api/**
- domain/**
- docker-compose.yml
- .github/workflows/deploy-frontend.yml
```

| 브랜치 | 이미지 태그 | 배포 환경 |
|---|---|---|
| `main` | `<sha>` + `latest` | production |
| `dev` | `<sha>` | staging |

---

## 트러블슈팅

### 백엔드 기동 지연 (frontend가 backend healthcheck 실패)

Spring Boot 기동에 최대 60초가 소요될 수 있음. `docker-compose.yml`에 `start_period: 60s`가 설정되어 있으므로 정상.

```bash
docker compose logs -f backend
```

### 이미지 pull 실패 (unauthorized)

```bash
cat ~/.docker/config.json
echo "<PAT>" | docker login ghcr.io -u <USERNAME> --password-stdin
```

### Redis 연결 실패

Redis는 컨테이너 내부에서 `redis` 호스트명으로 접근함. 직접 환경변수(`SPRING_DATA_REDIS_HOST=redis`)로 설정되어 있으므로 application.yml의 `localhost` 고정값이 오버라이드됨.

### MySQL healthcheck 실패

`DB_PASSWORD`가 특수문자를 포함하는 경우 mysqladmin 명령에서 파싱 오류 발생 가능. 비밀번호는 영숫자와 `!@#$%` 정도로 제한 권장.

### MinIO 콘솔 접근 (SSH 터널)

MinIO 포트(9001)는 외부 노출 없음. SSH 터널로 접근:

```bash
ssh -L 9001:localhost:9001 ubuntu@서버IP
# 이후 브라우저에서 http://localhost:9001 접속
```

### 로그 확인

```bash
cd /home/ubuntu/app

docker compose logs -f nginx
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f mysql
```
