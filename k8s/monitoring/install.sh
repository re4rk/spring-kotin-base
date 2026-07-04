#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NAMESPACE="monitoring"
RELEASE="kube-prometheus-stack"
CHART="prometheus-community/kube-prometheus-stack"
CHART_VERSION="${CHART_VERSION:-}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "error: '$1' 명령이 필요합니다." >&2
    exit 1
  fi
}

require_cmd kubectl
require_cmd helm

if [[ -z "${GRAFANA_ADMIN_PASSWORD:-}" ]]; then
  echo "error: GRAFANA_ADMIN_PASSWORD 환경 변수를 설정하세요." >&2
  echo "  export GRAFANA_ADMIN_PASSWORD='your-secure-password'" >&2
  exit 1
fi

echo "==> Helm repo 추가/갱신"
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts >/dev/null 2>&1 || true
helm repo add grafana https://grafana.github.io/helm-charts >/dev/null 2>&1 || true
helm repo update prometheus-community grafana

echo "==> Namespace 적용"
kubectl apply -f "${ROOT_DIR}/namespace.yaml"

echo "==> Grafana admin Secret 생성/갱신"
kubectl create secret generic grafana-admin \
  --namespace="${NAMESPACE}" \
  --from-literal=admin-user=admin \
  --from-literal=admin-password="${GRAFANA_ADMIN_PASSWORD}" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "==> Traefik middleware 적용"
kubectl apply -f "${ROOT_DIR}/middleware.yaml"

echo "==> Loki 설치"
helm upgrade --install loki grafana/loki \
  --namespace "${NAMESPACE}" \
  --create-namespace \
  -f "${ROOT_DIR}/values-loki.yaml" \
  --wait --timeout 10m

echo "==> Tempo 설치"
helm upgrade --install tempo grafana/tempo \
  --namespace "${NAMESPACE}" \
  --create-namespace \
  -f "${ROOT_DIR}/values-tempo.yaml" \
  --wait --timeout 10m

echo "==> Alloy ConfigMap 적용"
kubectl create configmap alloy-config \
  --namespace="${NAMESPACE}" \
  --from-file=config.river="${ROOT_DIR}/alloy.config.river" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "==> Alloy (Faro receiver) 설치"
helm upgrade --install alloy grafana/alloy \
  --namespace "${NAMESPACE}" \
  --create-namespace \
  -f "${ROOT_DIR}/values-alloy.yaml" \
  --wait --timeout 10m

echo "==> Faro route 적용 (ark-base.site/faro → Alloy)"
kubectl apply -f "${ROOT_DIR}/../faro-route.yaml"
kubectl delete ingressroute faro-collector -n base --ignore-not-found

echo "==> Grafana Ingress 적용"
kubectl apply -f "${ROOT_DIR}/grafana-ingress.yaml"

HELM_ARGS=(
  upgrade --install "${RELEASE}" "${CHART}"
  --namespace "${NAMESPACE}"
  --create-namespace
  -f "${ROOT_DIR}/values.yaml"
  --wait
  --timeout 10m
)

if [[ -n "${CHART_VERSION}" ]]; then
  HELM_ARGS+=(--version "${CHART_VERSION}")
fi

echo "==> kube-prometheus-stack 설치"
helm "${HELM_ARGS[@]}"

echo ""
echo "설치 완료."
echo ""
echo "Grafana:  https://grafana.ark-base.site  (admin / \$GRAFANA_ADMIN_PASSWORD)"
echo "Faro:     https://ark-base.site/faro/collect"
echo ""
echo "DNS (ark-base.site와 동일 IP):"
echo "  grafana.ark-base.site"
echo ""
echo "Grafana Explore:"
echo "  Loki  → {app=\"base-frontend\"} 또는 service_name"
echo "  Tempo → frontend + backend end-to-end traces"
