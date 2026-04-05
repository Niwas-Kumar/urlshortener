# URL Shortener

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![Redis](https://img.shields.io/badge/Redis-7.0-orange) ![License](https://img.shields.io/badge/License-MIT-lightgrey)

A full-stack **URL shortening service** built with **Spring Boot**, **MySQL**, and **Redis**. The service converts long URLs into compact, shareable short links, tracks usage analytics, enforces time-based expiry, and serves a built-in browser UI — all from a single deployable JAR.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Configuration](#configuration)

---

## Features

- **URL shortening** — converts any long URL into a compact Base62-encoded short code.
- **Automatic URL normalization** — prepends `https://` when a scheme is missing.
- **Redirect** — visiting a short link transparently redirects the browser to the original URL.
- **TTL-based expiry** — short URLs expire after 24 hours; expired links return HTTP 410 Gone.
- **Redis caching** — resolved URLs are cached in Redis for the remaining TTL, minimizing database load on high-traffic links.
- **Click analytics** — each redirect increments a persistent hit counter; a dedicated endpoint exposes link statistics.
- **Global exception handling** — structured JSON error responses for not-found (404), expired (410), and unexpected (500) conditions.
- **Browser UI** — a self-hosted HTML/JS front end (served as a static resource) with copy-to-clipboard support.

---

## Architecture

```
Browser / API Client
        │
        ▼
  Spring Boot (port 8081)
  ┌─────────────────────────────┐
  │  UrlController              │
  │    POST /shorten            │
  │    GET  /r/{shortCode}      │
  │    GET  /analytics/{code}   │
  └────────────┬────────────────┘
               │
         UrlService
          ┌────┴─────┐
          │          │
       MySQL       Redis
    (persistence) (read cache)
```

**Short-code generation** uses a two-step process: the record is first persisted to obtain an auto-incremented primary key, which is then Base62-encoded (`[0-9A-Za-z]`) to produce a compact, URL-safe short code.

**Cache strategy** — on redirect, the service checks Redis first. On a cache miss the long URL is fetched from MySQL, written to Redis with the remaining TTL, and returned. The cache entry automatically expires in sync with the database record.

---

## Technologies

| Layer       | Technology                      |
|-------------|---------------------------------|
| Language    | Java 17                         |
| Framework   | Spring Boot 4.0.1 (Spring MVC)  |
| Persistence | Spring Data JPA + MySQL 8       |
| Cache       | Spring Data Redis + Redis 7     |
| Frontend    | HTML, CSS, Vanilla JavaScript   |
| Build       | Apache Maven (Maven Wrapper)    |
| Testing     | H2 in-memory database           |

---

## Project Structure

```text
urlshortener/
├── src/
│   ├── main/
│   │   ├── java/com/example/urlshortener/
│   │   │   ├── UrlshortenerApplication.java   # Entry point
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java           # Redis connection & template
│   │   │   ├── controllers/
│   │   │   │   └── UrlController.java         # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── UrlService.java            # Business logic, caching
│   │   │   ├── repository/
│   │   │   │   └── UrlRepository.java         # Spring Data JPA repository
│   │   │   ├── entity/
│   │   │   │   └── Url.java                   # JPA entity (id, shortCode, longUrl, hitCount, expiryAt)
│   │   │   ├── dto/
│   │   │   │   └── UrlRequest.java            # Request body DTO
│   │   │   ├── util/
│   │   │   │   └── Base62Util.java            # ID → short-code encoder
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── UrlNotFoundException.java
│   │   │       └── UrlExpiredException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/index.html              # Embedded browser UI
│   └── test/
│       └── java/com/example/urlshortener/
│           └── UrlshortenerApplicationTests.java
└── pom.xml
```

---

## Getting Started

### Prerequisites

| Requirement | Version  |
|-------------|----------|
| Java        | 17+      |
| Maven       | 3.9+ (or use the included `mvnw` wrapper) |
| MySQL       | 8.0+     |
| Redis       | 7.0+     |

### 1. Set up MySQL

```sql
CREATE DATABASE url_shortener;
```

### 2. Configure the application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener
spring.datasource.username=<your_mysql_user>
spring.datasource.password=<your_mysql_password>

spring.data.redis.host=localhost
spring.data.redis.port=6379

server.port=8081
```

> **Note:** Never commit real credentials. Use environment variables or Spring profiles for production deployments.

### 3. Build and run

```bash
./mvnw spring-boot:run
```

The application starts on **http://localhost:8081**. Open that URL in a browser to access the UI.

---

## API Reference

### Shorten a URL

```
POST /shorten
Content-Type: application/json

{ "longUrl": "https://example.com/very/long/path?with=query" }
```

**Response `200 OK`**

```json
{ "shortUrl": "http://localhost:8081/r/1B" }
```

---

### Redirect to original URL

```
GET /r/{shortCode}
```

- **302 Found** — redirects to the original URL and increments the hit counter.
- **404 Not Found** — short code does not exist.
- **410 Gone** — link has expired.

---

### Get link analytics

```
GET /analytics/{shortCode}
```

**Response `200 OK`**

```json
{
  "shortCode": "1B",
  "longUrl": "https://example.com/very/long/path?with=query",
  "hitCount": 42,
  "expiryAt": "2026-04-06T10:30:00"
}
```

---

### Error response format

All error conditions return a consistent JSON body:

```json
{
  "timestamp": "2026-04-05T10:30:00",
  "status": 404,
  "error": "Url not found"
}
```

| HTTP Status | Condition                  |
|-------------|----------------------------|
| 404         | Short code does not exist  |
| 410         | Short URL has expired      |
| 500         | Unexpected server error    |

---

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8081` | HTTP port the application listens on |
| `spring.datasource.url` | — | JDBC connection string for MySQL |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema management strategy |
| `spring.data.redis.host` | `localhost` | Redis host |
| `spring.data.redis.port` | `6379` | Redis port |

URL expiry defaults to **24 hours** and is set in `UrlService.createShortUrl`. To change the TTL, update the `plusHours(24)` call in that method.
