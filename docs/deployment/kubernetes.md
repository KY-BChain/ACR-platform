# Kubernetes Deployment Guide

## Prerequisites

- Kubernetes cluster (v1.28+)
- kubectl configured
- Helm 3.x installed
- Docker images built and pushed to registry

## Quick Start

```bash
# Deploy to development environment
kubectl apply -f acr-infrastructure/k8s/dev/

# Deploy to production
kubectl apply -f acr-infrastructure/k8s/prod/
```

## Architecture

```
┌─────────────────────────────────────────┐
│          Ingress Controller              │
│         (NGINX or Traefik)               │
└─────────────┬───────────────────────────┘
              │
              ├─────► ACR Web Portal Service
              ├─────► ACR API Gateway Service
              └─────► ACR FL Aggregator Service
```

## Services

### 1. API Gateway
- **Replicas**: 3 (production), 1 (dev)
- **Resources**: 1 CPU, 2Gi RAM
- **Port**: 3000
- **Health Check**: /health

### 2. Web Portal
- **Replicas**: 2 (production), 1 (dev)
- **Resources**: 0.5 CPU, 1Gi RAM
- **Port**: 3001

### 3. Agent System
- **Replicas**: 1 per agent type
- **Resources**: 2 CPU, 4Gi RAM (with GPU support)
- **Ports**: 8001-8004

### 4. FL Aggregator
- **Replicas**: 1 (stateful)
- **Resources**: 4 CPU, 8Gi RAM
- **Port**: 8000

## Persistent Volumes

- PostgreSQL: 20Gi
- Redis: 5Gi
- IPFS: 100Gi
- Model Storage: 50Gi

## Monitoring

- Prometheus for metrics
- Grafana for visualization
- Loki for log aggregation
- Jaeger for distributed tracing

## Scaling

```bash
# Scale API Gateway
kubectl scale deployment acr-api-gateway --replicas=5

# Scale Agent System
kubectl scale deployment acr-agents --replicas=3
```

## Updates

```bash
# Rolling update
kubectl set image deployment/acr-api-gateway acr-api=acr/api-gateway:v0.9

# Rollback
kubectl rollout undo deployment/acr-api-gateway
```
