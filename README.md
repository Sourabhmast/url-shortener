# URL Shortener Service

A production-grade URL Shortener Service built with Java 17, Spring Boot 3, PostgreSQL, and Redis.

## Architecture Diagram

```mermaid
graph TD
    Client((Client))
    LB[Load Balancer / API Gateway]
    App[Spring Boot App]
    Redis[(Redis Cache)]
    DB[(PostgreSQL DB)]

    Client -->|HTTP POST /api/shorten| LB
    Client -->|HTTP GET /r/{shortCode}| LB
    LB --> App
    App -->|Cache lookup| Redis
    App -->|DB read/write| DB
```

## Technologies Used
- **Java 17**
- **Spring Boot 3.2.x** (Web, Data JPA, Cache, Actuator)
- **PostgreSQL 15** (Primary Datastore)
- **Redis 7** (Caching Layer)
- **Flyway** (Database Migrations)
- **Testcontainers** (Integration Testing)
- **Docker & Docker Compose** (Containerization)

## Quick Start

The easiest way to run the application is using Docker Compose.

```bash
docker compose up -d
```

This will start:
1. PostgreSQL on port 5432
2. Redis on port 6379
3. Spring Boot App on port 8080

Once running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## API Examples

### Shorten a URL

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.example.com/very/long/path/to/some/resource"}'
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/r/000001",
  "shortCode": "000001",
  "createdAt": "2023-10-01T12:00:00"
}
```

### Redirect

```bash
curl -v http://localhost:8080/r/000001
```

*(Returns a 302 Found with the `Location` header set to the original URL)*

### Get URL Stats

```bash
curl http://localhost:8080/api/stats/000001
```

**Response:**
```json
{
  "shortCode": "000001",
  "originalUrl": "https://www.example.com/very/long/path/to/some/resource",
  "clickCount": 1,
  "createdAt": "2023-10-01T12:00:00"
}
```

## Design Decisions

### Base62 Encoding
Base62 encoding (using characters `0-9, a-z, A-Z`) is used to generate the short code. We use the PostgreSQL auto-incremented primary key (`id`) as the seed. This approach ensures:
- **Collision-free codes**: Since the DB ID is unique, the encoded string is unique.
- **Deterministic & Reversible**: No random collisions to handle.
- **Scalability**: Can easily be scaled by pre-allocating ID ranges to multiple application instances (e.g., using a Snowflake-like ID generator or Twitter Snowflake) if we move away from simple auto-increment.
- **Padding**: Codes are padded to a minimum of 6 characters using the first character of the Base62 charset (`0`).

### Caching Strategy with Redis
Redis is used as a caching layer to reduce read load on PostgreSQL for redirects.
- **Cache-Aside Pattern**: On redirect, we check Redis first. If it's a HIT, we return immediately (zero DB calls). If it's a MISS, we query the DB and populate the cache.
- **TTL = 24 Hours**: URL access often follows a power-law distribution (most clicks happen shortly after a link is shared). A 24-hour TTL keeps the cache size manageable while absorbing the bulk of the redirect traffic.
- **Metrics**: A custom `LoggingCache` wrapper is implemented over Spring's `RedisCacheManager` to track and log Cache Hits and Misses for observability.
