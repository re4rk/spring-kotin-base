# 모니터링 (Self-hosted LGTM + Faro)

Prometheus + Grafana + **Loki** + **Tempo** + **Alloy (Faro receiver)** 스택입니다.

## 아키텍처

```
브라우저 (Faro SDK → /faro/collect)
  → ark-base.site/faro → Alloy
      ├→ Loki   (프론트 로그/에러/RUM)
      └→ Tempo  (프론트 트레이스)

API (Spring Boot OTel)
## 백엔드 트레이싱

- Spring Boot OpenTelemetry → Tempo OTLP export
- W3C `traceparent` (Faro가 주입) 자동 연결
- 로그 MDC `traceId`/`spanId` — Grafana Tempo ↔ Loki 상관관계용 (커스텀 헤더/API 필드 없음)

Grafana (grafana.ark-base.site)
  ├ Prometheus (메트릭)
  ├ Loki       (로그)
  └ Tempo      (트레이스, traces-to-logs 연동)
```

## 사전 요구사항

- `helm` 3.x, `kubectl`
- Traefik + cert-manager
- DNS A 레코드 (`ark-base.site`와 동일 IP): `grafana.ark-base.site`
- Faro는 **별도 DNS 불필요** — `https://ark-base.site/faro/collect`

## 설치

```bash
export GRAFANA_ADMIN_PASSWORD='...'
./k8s/monitoring/install.sh
```

GitHub Actions: **Deploy Monitoring** (수동)

## 프론트엔드 (Faro)

- `@grafana/faro-web-sdk` + `@grafana/faro-web-tracing`
- 프로덕션 collector: `/faro/collect` (same-origin, `https://ark-base.site/faro/collect`)
- `traceparent` 자동 주입 → 백엔드 Tempo와 end-to-end trace

로컬 개발 (Alloy port-forward):

```bash
kubectl port-forward -n monitoring svc/alloy 12347:12347
VITE_FARO_URL=/faro/collect npm run dev
```

## Grafana에서 확인

| 데이터 | Explore |
|--------|---------|
| 프론트 에러/로그 | Loki → `{service_name="base-frontend"}` |
| E2E 트레이스 | Tempo → Search |
| Pod 메트릭 | Prometheus dashboards |

## 구성 파일

| 파일 | 역할 |
|------|------|
| `values.yaml` | kube-prometheus-stack + Grafana datasources |
| `values-loki.yaml` | Loki |
| `values-tempo.yaml` | Tempo |
| `values-alloy.yaml` | Alloy |
| `alloy.config.river` | Faro receiver → Loki/Tempo |
| `grafana-ingress.yaml` | Grafana Ingress |
| `../faro-route.yaml` | Faro collector (`/faro` → Alloy) |

## 제거

```bash
helm uninstall kube-prometheus-stack alloy tempo loki -n monitoring
kubectl delete namespace monitoring
```
