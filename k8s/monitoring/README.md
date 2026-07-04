# Kubernetes 모니터링 (kube-prometheus-stack)

클러스터 내부에 Prometheus + Grafana + Alertmanager를 설치합니다.

## 사전 요구사항

- `helm` 3.x, `kubectl`
- Traefik Ingress + cert-manager (기존 `k8s/ingress.yaml`과 동일)
- DNS: `grafana.ark-base.site` → `ark-base.site`와 **동일한 IP** (A 레코드)

## 설치

```bash
export GRAFANA_ADMIN_PASSWORD='강력한-비밀번호'

chmod +x k8s/monitoring/install.sh
./k8s/monitoring/install.sh
```

GitHub Actions: **Deploy Monitoring** 워크플로 (수동 실행)  
Repository Secret `GRAFANA_ADMIN_PASSWORD` 필요

## 접속

| 서비스 | URL |
|--------|-----|
| Grafana | https://grafana.ark-base.site |
| Prometheus | `kubectl port-forward -n monitoring svc/kube-prometheus-stack-prometheus 9090:9090` |

Grafana 계정: `admin` / `GRAFANA_ADMIN_PASSWORD`

## 포함 대시보드

- Cluster / Node / Pod CPU·메모리
- Namespace별 리소스
- `base` 네임스페이스 backend/frontend Pod 모니터링

## 업그레이드

```bash
export GRAFANA_ADMIN_PASSWORD='...'
./k8s/monitoring/install.sh
```

## 제거

```bash
helm uninstall kube-prometheus-stack -n monitoring
kubectl delete namespace monitoring
```

## 다음 단계

Spring Boot `/actuator/prometheus` + `ServiceMonitor` 추가 시 JVM·HTTP 메트릭을 Grafana에서 볼 수 있습니다.
