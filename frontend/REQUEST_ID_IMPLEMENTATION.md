# Request ID Implementation

## Overview

A unique `X-Request-ID` header is added to all frontend API requests for traceability and debugging. The backend implements corresponding logging and tracing context handling.

## Implementation Details

### 1. Frontend Implementation

File: `frontend/src/utils/fetchWithRequestId.ts`

A `fetchWithRequestId()` function was created as an enhanced wrapper around the native `fetch()` API:

- Generates a UUID (v4) for each request
- Adds the UUID in the `X-Request-ID` request header
- Logs request and response information to the browser console (including the requestId)
- Logs errors with the associated requestId

### 2. API Fetchers Updated

All 30 API fetcher files were updated:

- Import `fetchWithRequestId`
- Replace direct `fetch()` calls with `fetchWithRequestId()`

### 3. Backend Implementation

File: `backend/src/main/java/dev/meirong/showcase/bookstore/logging/RequestResponseLoggingFilter.java`

A `RequestResponseLoggingFilter` handles request IDs on the backend:

- Extracts `X-Request-ID` from incoming requests
- Puts the value into MDC (Mapped Diagnostic Context) only if present
- Logs request and response details, including headers and payload
- Integrates with Micrometer tracing so `traceId` and `spanId` appear in logs when tracing is enabled

## How to Use

### Frontend

Every API request automatically includes the `X-Request-ID` header; no additional configuration required.

Example request headers:

```
X-Request-ID: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Authorization: Bearer ...
```

### Backend

The backend filter processes request IDs automatically:

1. Extracts the request ID from the `X-Request-ID` header
2. Sets MDC if the request ID exists
3. Logs request and response details
4. When Micrometer tracing is active, `traceId` and `spanId` are added to MDC and included in logs

### Log Configuration

Example `logback-spring.xml` pattern to include MDC fields:

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] [%X{requestId}] [%X{traceId}] [%X{spanId}] %-5level %logger{36} - %msg%n</pattern>
```

## Debugging and Monitoring

### Frontend Console Logs

Requests and responses are logged in the browser console:

```
[Request 550e8400-e29b-41d4-a716-446655440000] GET http://localhost:8000/api/books?page=0&books-per-page=9
[Response 550e8400-e29b-41d4-a716-446655440000] 200 OK
```

### Backend Logs

Backend logs include tracing information when available:

```
2023-12-07 10:30:15 [http-nio-8080-exec-1] [550e8400-e29b-41d4-a716-446655440000] [abc123def456] [span789] INFO  d.m.s.b.l.RequestResponseLoggingFilter - Request: method=GET, uri=/api/books, headers={...}, payload={}
2023-12-07 10:30:15 [http-nio-8080-exec-1] [550e8400-e29b-41d4-a716-446655440000] [abc123def456] [span789] INFO  d.m.s.b.l.RequestResponseLoggingFilter - Response(150 ms): status=200, headers={...}, payload={...}
```

### Error Tracing

When requests fail, use the requestId to quickly locate logs:

```
[Error 550e8400-e29b-41d4-a716-446655440000] TypeError: Failed to fetch
```

## Dependencies

### Frontend Deps

- `uuid` (v10.0.0+): Generates UUIDs  
- `@types/uuid`: TypeScript typings

### Backend Deps

- `spring-boot-starter-opentelemetry`: OpenTelemetry integration (observability)  
- `logstash-logback-encoder`: Structured JSON logging encoder

## logback-spring.xml Example

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %X %-
