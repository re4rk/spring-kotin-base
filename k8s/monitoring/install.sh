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
helm repo update prometheus-community

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
echo "Grafana: https://grafana.ark-base.site"
echo "  user: admin"
echo "  pass: \$GRAFANA_ADMIN_PASSWORD"
echo ""
echo "DNS에 grafana.ark-base.site A 레코드가 클러스터 노드 IP를 가리키는지 확인하세요."
echo ""
echo "로컬 접속 (Ingress 없이):"
echo "  kubectl port-forward -n ${NAMESPACE} svc/${RELEASE}-grafana 3000:80"
echo "  → http://localhost:3000"
echo ""
echo "Prometheus UI (내부용):"
echo "  kubectl port-forward -n ${NAMESPACE} svc/${RELEASE}-prometheus 9090:9090"
echo "  → http://localhost:9090"
