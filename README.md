# Salon Booking — Microservices Platform

A production-grade salon booking system built on Spring Cloud microservices. Supports local development via Docker Compose and production deployment via Kubernetes with Helm.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Microservices](#microservices)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Local Development (Docker Compose)](#local-development-docker-compose)
- [Production Deployment (Kubernetes + Helm)](#production-deployment-kubernetes--helm)
  - [Cluster Setup](#cluster-setup)
  - [Nginx Ingress Controller](#nginx-ingress-controller)
  - [Deploy with Helm](#deploy-with-helm)
  - [Manual Kubernetes Manifests](#manual-kubernetes-manifests)
- [Building & Pushing Docker Images](#building--pushing-docker-images)
- [API Gateway Routes](#api-gateway-routes)
- [Authentication (Keycloak)](#authentication-keycloak)
- [Monitoring (Prometheus)](#monitoring-prometheus)
- [Configuration Reference](#configuration-reference)

---

## Architecture Overview

```
                        ┌─────────────────────────────────┐
                        │         Client / Browser         │
                        └────────────────┬────────────────┘
                                         │
                        ┌────────────────▼────────────────┐
                        │      Nginx Ingress Controller    │
                        │  api.salonapp.local              │
                        │  auth.salonapp.local             │
                        └──────┬─────────────────┬────────┘
                               │                 │
               ┌───────────────▼──┐         ┌───▼────────────────┐
               │   API Gateway    │         │     Keycloak        │
               │   (Port 8000)    │         │  (OAuth2 / OIDC)   │
               └──────┬───────────┘         └────────────────────┘
                      │  JWT validation via Keycloak JWK
                      │
          ┌───────────▼───────────────────────────────────────┐
          │               Eureka Service Discovery             │
          │                    (Port 8761)                     │
          └──────┬─────────┬─────────┬──────┬────────┬────────┘
                 │         │         │      │        │
          ┌──────▼──┐ ┌────▼───┐ ┌───▼───┐ │  ┌────▼──────────┐
          │  User   │ │ Salon  │ │Booking│ │  │   Notification │
          │ Service │ │Service │ │Service│ │  │    Service     │
          └──────┬──┘ └────┬───┘ └──┬────┘ │  └───────────────┘
                 │         │       │      │
          ┌──────▼──┐ ┌────▼──┐ ┌──▼──┐  │       ┌────────────┐
          │ userdb  │ │salondb│ │book │  │       │  RabbitMQ  │
          │ (MySQL) │ │(MySQL)│ │  db │  └──────►│ (Messaging)│
          └─────────┘ └───────┘ └─────┘          └─────┬──────┘
                                                        │
                                              ┌─────────▼──────┐
                                              │ Payment Service │
                                              │  (Stripe SDK)  │
                                              └────────────────┘
```

---

## Microservices

| Service | Port | Responsibility | Database |
|---|---|---|---|
| **eureka-server** | 8761 | Service discovery and registration | — |
| **gateway-server** | 8000 | API routing, JWT authentication | — |
| **user-service** | 8080 | User accounts and authentication | MySQL `userdb` |
| **salon-service** | 8081 | Salon information and management | MySQL `salondb` |
| **category-service** | 8082 | Service categories | MySQL `categorydb` |
| **service-offering** | 8083 | Available salon services | MySQL `servicesdb` |
| **booking-service** | 8084 | Booking lifecycle management | MySQL `bookingdb` |
| **payment-service** | 8085 | Stripe payment processing | MySQL `paymentdb` |
| **notification-service** | 8086 | Event-driven notifications | MySQL `notificationdb` |

Each service owns its database (database-per-service pattern). Inter-service communication uses OpenFeign for synchronous calls and RabbitMQ for async booking/payment events.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.x, Spring Cloud 2025.1.1 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway + Spring Security OAuth2 |
| Inter-service Calls | Spring Cloud OpenFeign |
| Async Messaging | Spring AMQP + RabbitMQ |
| Databases | MySQL 8.0 (one per service), PostgreSQL 15 (Keycloak) |
| Authentication | Keycloak 21.0.1 (OAuth2 / OIDC) |
| Payments | Stripe Java SDK |
| Metrics | Micrometer + Prometheus + Spring Boot Actuator |
| Image Build | Google Jib Maven Plugin (no Dockerfiles needed) |
| Container Runtime | Docker |
| Orchestration | Kubernetes, Helm 3 |
| Ingress | Nginx Ingress Controller |

---

## Prerequisites

**Local development:**
- Docker Desktop with Compose V2
- Java 17 + Maven 3.8+

**Kubernetes deployment:**
- A running Kubernetes cluster (Minikube, kubeadm, or managed cloud)
- `kubectl` configured against the cluster
- Helm 3 installed
- Nginx Ingress Controller installed on the cluster
- Docker Hub account (images are pushed to `yasindumalmith/<service>:v1`)

---

## Local Development (Docker Compose)

The Compose file at `docker-compose/default/docker-compose.yml` starts the full stack — six MySQL instances, Eureka, the Gateway, and all application services.

```bash
cd docker-compose/default
docker compose up -d
```

Services come up in dependency order. All services perform health checks before dependants start.

**Port map (host → container):**

| Service | Host Port |
|---|---|
| Eureka Dashboard | 8761 |
| API Gateway | 8000 |
| user-service | 8080 |
| salon-service | 8081 |
| category-service | 8082 |
| service-offering | 8083 |
| booking-service | 8084 |
| payment-service | 8085 |
| notification-service | 8086 |
| userdb (MySQL) | 3301 |
| salondb (MySQL) | 3302 |
| categorydb (MySQL) | 3303 |
| servicesdb (MySQL) | 3304 |
| bookingdb (MySQL) | 3305 |
| paymentdb (MySQL) | 3307 |

Stop the stack:

```bash
docker compose down -v
```

---

## Production Deployment (Kubernetes + Helm)

### Cluster Setup

Add the required DNS entries to `/etc/hosts` (or your cluster's DNS) on every node and on your local machine:

```
<CLUSTER-IP>  api.salonapp.local
<CLUSTER-IP>  auth.salonapp.local
```

### Nginx Ingress Controller

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace
```

Verify the controller pod is running:

```bash
kubectl get pods -n ingress-nginx
```

### Deploy with Helm

The Helm chart at `helm/salon-booking/` deploys the user service, salon service, API gateway, MySQL databases, Keycloak, and the Nginx Ingress rule in one release.

```bash
# Install (first time)
helm install salon-booking ./helm/salon-booking \
  --namespace salon \
  --create-namespace

# Upgrade after changes
helm upgrade salon-booking ./helm/salon-booking \
  --namespace salon
```

**Key default values (`helm/salon-booking/values.yaml`):**

| Key | Default | Description |
|---|---|---|
| `springProfile` | `prod` | Active Spring profile |
| `imagePullPolicy` | `Always` | Image pull policy |
| `userService.replicas` | `3` | User service replica count |
| `salonService.replicas` | `3` | Salon service replica count |
| `gateway.replicas` | `1` | Gateway replica count |
| `gateway.nodePort` | `30080` | NodePort for external access |
| `keycloak.hostname` | `auth.salonapp.local` | Keycloak public hostname |
| `ingress.apiHost` | `api.salonapp.local` | API ingress hostname |
| `ingress.authHost` | `auth.salonapp.local` | Auth ingress hostname |

Override any value at install time:

```bash
helm install salon-booking ./helm/salon-booking \
  --namespace salon \
  --create-namespace \
  --set userService.replicas=2 \
  --set salonService.replicas=2
```

Uninstall:

```bash
helm uninstall salon-booking --namespace salon
```

### Manual Kubernetes Manifests

Raw manifests are available in `k8s/` if you prefer `kubectl apply` instead of Helm.

```bash
# Apply in order
kubectl apply -f k8s/mysql/
kubectl apply -f k8s/keyclock/
kubectl apply -f k8s/user-service/
kubectl apply -f k8s/salon-service/
kubectl apply -f k8s/gateway/
kubectl apply -f k8s/Ingress/
```

**Resources created per service:**
- `Deployment` (3 replicas, liveness + readiness probes on `/actuator/health`)
- `Service` (ClusterIP)
- `ConfigMap` (Spring environment variables, active profile)
- `Secret` (database credentials, base64-encoded)
- `StatefulSet` + `PersistentVolumeClaim` (2 Gi) for each MySQL instance

---

## Building & Pushing Docker Images

Images are built and pushed directly to Docker Hub using the [Google Jib](https://github.com/GoogleContainerTools/jib) Maven plugin — no Dockerfile required.

```bash
# Build and push a single service
cd user-service
mvn jib:build

# Build and push all services from the repo root
mvn jib:build --projects user-service,salon-service,booking-service, \
  payment-service,notification-service,category-service, \
  service-offering,gateway-server,eureka-server
```

Images are tagged `yasindumalmith/<service-name>:v1` on Docker Hub.

> Authenticate with Docker Hub before running `jib:build`:
> ```bash
> docker login
> ```

---

## API Gateway Routes

All requests enter through the API Gateway at `api.salonapp.local` (production) or `localhost:8000` (local). Every route except `/auth/**` requires a valid Bearer JWT.

| Path Prefix | Upstream Service |
|---|---|
| `/api/users/**` | user-service |
| `/auth/**` | user-service (login / token endpoints) |
| `/api/salons/**` | salon-service |
| `/api/categories/**` | category-service |
| `/api/service-offering/**` | service-offering |
| `/api/bookings/**` | booking-service |
| `/api/payments/**` | payment-service |
| `/api/notification/**` | notification-service |

---

## Authentication (Keycloak)

Keycloak acts as the OAuth2 / OIDC authorization server.

| Detail | Value |
|---|---|
| Image | `quay.io/keycloak/keycloak:21.0.1` |
| Admin console | `http://auth.salonapp.local` |
| Default admin user | `admin` / `admin` |
| Realm | `master` |
| Database | PostgreSQL 15 |

**Token flow:**

1. Client POSTs credentials to Keycloak:
   ```
   POST http://auth.salonapp.local/realms/master/protocol/openid-connect/token
   ```
2. Keycloak returns a signed JWT.
3. Client sends the JWT as `Authorization: Bearer <token>` to the API Gateway.
4. Gateway validates the JWT signature against Keycloak's JWK Set:
   ```
   http://keycloak-service:8080/realms/master/protocol/openid-connect/certs
   ```
5. Valid requests are forwarded to the target microservice.

---

## Monitoring (Prometheus)

Salon Service and Booking Service expose a Prometheus-compatible metrics endpoint via Spring Boot Actuator.

**Metrics endpoint:** `GET /actuator/prometheus`

**Prometheus scrape config** (`prometheus/prometheus.yml`):

```yaml
scrape_configs:
  - job_name: 'salon-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8081']
```

**Kubernetes ServiceMonitor** (`prometheus/service-monitoring.yml`) — used with the Prometheus Operator:

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: salon-service-monitor
  namespace: monitoring
spec:
  selector:
    matchLabels:
      app: salon-service
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
```

**Other actuator endpoints:**

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Liveness and readiness (used by K8s probes) |
| `/actuator/metrics` | JSON metrics |
| `/actuator/info` | Application info |

**Alertmanager** config is in `prometheus/alertmanager-email-config.yml` for email-based alerting.

---

## Configuration Reference

### Spring Profiles

| Profile | Usage |
|---|---|
| `dev` (default) | Local, connects to `localhost` databases and Eureka |
| `prod` | Docker Compose / K8s; reads URLs from environment variables |

Set via environment variable: `SPRING_PROFILES_ACTIVE=prod`

### Key Environment Variables

| Variable | Used By | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | All services | JDBC URL for the service's MySQL instance |
| `SPRING_DATASOURCE_USERNAME` | All services | Database username |
| `SPRING_DATASOURCE_PASSWORD` | All services | Database password |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | All services | Eureka registration URL |
| `STRIPE_API_KEY` | payment-service | Stripe publishable key |
| `STRIPE_API_SECRET` | payment-service | Stripe secret key |
| `SPRING_RABBITMQ_HOST` | booking-service, payment-service | RabbitMQ broker host |

### Resource Limits (Kubernetes)

| Service | CPU Request | Memory Request | Memory Limit |
|---|---|---|---|
| user-service | — | 256 Mi | 512 Mi |
| salon-service | — | 256 Mi | 512 Mi |
| gateway-server | — | 256 Mi | 512 Mi |
| MySQL (each) | — | — | — |

Docker Compose sets a 700 MB memory limit per application container.