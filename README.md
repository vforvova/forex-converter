# Forex Converter

A REST API and web UI for currency conversion. Converts monetary values between
currencies using live exchange rates from [swop.cx](https://swop.cx).

| Component | Technology              | Port |
|-----------|-------------------------|------|
| Backend   | Java 21, Spring Boot    | 8080 |
| Frontend  | Vue 3, TypeScript, Vite | 5173 |
| InfluxDB  | Time-series metrics     | 8086 |
| Grafana   | Monitoring dashboards   | 3000 |

## Quick Start

### Prerequisites

- Docker & Docker Compose
- A swop.cx API key

> [!TIP]
> Sign up at [swop.cx](https://swop.cx) to get your API key. The free tier
> provides daily exchange rates for all major currency pairs.

### Start the stack

> [!IMPORTANT]
> `SWOP_API_KEY` is required. The backend will not start without it.

1. Create `backend/.env`:
   ```env
   SWOP_API_KEY=your_api_key_here
   ```
2. Start services:
   ```bash
   docker compose up --build
   ```

The following services will be available:

| Service     | URL                   |
|-------------|-----------------------|
| Backend API | http://localhost:8080 |
| Grafana     | http://localhost:3000 |
| InfluxDB    | http://localhost:8086 |

> [!NOTE]
> The frontend is not part of the Docker Compose stack. See
> [frontend/README.md](frontend/README.md) to run it locally.

> [!WARNING]
> InfluxDB and Grafana are pre-configured with default dev credentials
> (`admin` / `admin`, token `token`). Do not use these in any deployed environment.

## API

### Convert currency

```
GET /convert/{from}-{to}
```

| Parameter | Type  | Required | Description                                                                                              |
|-----------|-------|----------|----------------------------------------------------------------------------------------------------------|
| `from`    | path  | ✅        | ISO 4217 source currency code (e.g. `USD`)                                                               |
| `to`      | path  | ✅        | ISO 4217 target currency code (e.g. `EUR`)                                                               |
| `amount`  | query | ❌        | Amount to convert. Decimal, max 2 fraction digits, between `0.01` and `100000000000`. When omitted, the raw exchange rate is returned. |

**Convert with amount:**
```bash
curl "http://localhost:8080/convert/USD-EUR?amount=100"
```
```json
{ "result": 92.12 }
```

**Get exchange rate only (no amount):**
```bash
curl "http://localhost:8080/convert/EUR-USD"
```
```json
{ "result": 1.079301 }
```

**Same-currency (identity):**
```bash
curl "http://localhost:8080/convert/USD-USD?amount=42"   # → { "result": 42 }
curl "http://localhost:8080/convert/USD-USD"             # → { "result": 1 }
```

### Errors

All error responses share the same shape:
```json
{ "error": "..." }
```

| Status | Cause                                                                               |
|--------|-------------------------------------------------------------------------------------|
| `400`  | Unrecognised currency code, invalid `amount` format, or value out of allowed range  |
| `404`  | Currency pair rate not available from the rate provider                             |
| `500`  | Rate provider unreachable or unexpected server error                                |

> [!NOTE]
> Currency codes are validated against the ISO 4217 standard. An unrecognised code
> (e.g. `FAKE`) returns `400`. A `404` means the code is structurally valid but
> no exchange rate is currently available for that pair.

### Health

```
GET /actuator/health
```

Returns `200 OK` when the application is ready to serve requests. Used internally
by the Docker health check to gate dependent services on backend readiness.

> [!NOTE]
> Health details are suppressed in the default profile (`show-details: never`).
> Activate the `dev` profile locally to see full component health information.

## Business Logic & Caching

- **Optional amount** — when `amount` is omitted, the API returns the raw exchange rate. Useful for clients that only need the rate, not a converted value.
- **Same-currency identity** — converting a currency to itself returns the input `amount` unchanged, or `1` when no amount is provided. The cache is not persisted and resets on restart. Cache is populated on application start, on schedule, and after cache-miss fallback.

> [!NOTE]
> Cache warmup could be controlled via configuration (`forex.cache.warmup.enabled=false`).

## Observability

Metrics flow from the backend via [Micrometer](https://micrometer.io/) to InfluxDB. Grafana is pre-provisioned with a datasource and an application
metrics dashboard.

> [!WARNING]
> The observability stack is configured for **local development only**.
> Credentials and tokens must be rotated before any real deployment.

## Local Development

For development outside of Docker:
- Backend: see [backend/README.md](backend/README.md)
- Frontend: see [frontend/README.md](frontend/README.md)
