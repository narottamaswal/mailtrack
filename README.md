# MailTrack

**MailTrack** is a full-stack email tracking and campaign analytics platform. Whether you're a marketer running drip campaigns, a sales rep following up on proposals, a recruiter tracking candidate engagement, or a job seeker checking if their application landed — MailTrack gives you real signal. A tool that lets you track whether your emails were opened and how long the recipient spent reading them.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red?style=flat-square&logo=angular)](https://angular.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

---

## Overview

- Open detection via a 1×1 invisible tracking pixel
- Read time estimation via `navigator.sendBeacon` heartbeats
- Hashed short URLs with custom slugs and per-click analytics
- Password-protected links for sensitive documents
- Campaign dashboards with per-recipient breakdowns and timeline views

Backend is a stateless Spring Boot 3 REST API secured with JWT. Frontend is an Angular 17 SPA using standalone components and Signals.

---

## Features

- 📧 **Open Tracking** — 1×1 GIF pixel injected into email `<img>` tag; fires a tracking event on every load
- ⏱ **Read Time** — `sendBeacon` heartbeat measures active session duration
- 🔗 **URL Shortening** — SHA-256 + Base62 slug generation with collision safety
- 🔒 **Password-Protected Links** — BCrypt-hashed passwords; no plaintext stored
- 🎯 **Campaigns** — group emails, track aggregate + per-recipient stats
- 📊 **Analytics** — open rate, CTR, avg read time, geo/device breakdown
- 🔔 **Live Notifications** — SSE push to dashboard when an email is opened
- 🔐 **Auth** — JWT access + refresh tokens, stateless Spring Security

---

## Tech Stack

**Backend:** Spring Boot 3.2, Spring Security 6, Spring Data JPA, PostgreSQL 15, Redis, `@Async` + virtual threads (Java 21), SSE via `SseEmitter`, MaxMind GeoLite2

**Frontend:** Angular 17 (standalone components), Angular Signals, RxJS, SCSS, Chart.js

---

## Getting Started

### Prerequisites

- Java 21+, Maven 3.9+
- Node.js 20+ / npm 9+
- Docker & Docker Compose

### Environment Variables

Create a `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mailtrack
DB_USERNAME=mailtrack_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_256_bit_base64_secret
JWT_ACCESS_EXPIRY_MS=900000
JWT_REFRESH_EXPIRY_MS=604800000

REDIS_HOST=localhost
REDIS_PORT=6379

APP_BASE_URL=https://yourdomain.com
GEOIP_DB_PATH=/opt/mailtrack/GeoLite2-City.mmdb
```

### Run with Docker Compose

```bash
git clone https://github.com/your-username/mailtrack.git
cd mailtrack
cp .env.example .env
docker compose up --build

# Frontend → http://localhost:4200
# API      → http://localhost:8080
```

### Run Locally

```bash
# Backend
cd mailtrack-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
cd mailtrack-ui
npm install && ng serve
```

---

## How It Works

### Open Tracking

Each email gets a unique `trackingId`. A 1×1 pixel is injected into the email HTML:

```html
<img src="https://yourdomain.com/track/open/abc123.gif"
     width="1" height="1" style="display:none" />
```

On load, Spring Boot responds with a real transparent GIF (`Cache-Control: no-store`), persists an `OpenEvent` asynchronously, enriches it with IP/UA metadata, and pushes an SSE notification to the sender's dashboard.

### Read Time

A JS snippet fires `sendBeacon` every 10s while the tab is active, and on `pagehide`. The backend updates `session_duration_seconds` on the `OpenEvent` record.

### URL Shortening & Redirect

Slugs are generated from SHA-256 + Base62 encoding. Redis acts as a read-through cache for sub-millisecond lookups. Redirect chain:

```
GET /r/{slug}
  → Redis cache (~1ms) or PostgreSQL fallback
  → [if password protected] → challenge page
  → record ClickEvent (async)
  → HTTP 302 → original URL
```

### Password-Protected Links

Passwords are BCrypt-hashed at creation. On access, the user hits `/unlock/:slug`, submits the password, and receives a short-lived signed JWT (15min TTL) stored in `sessionStorage` — no server-side session needed.

---

## API Reference

All endpoints prefixed `/api/v1`. Auth via `Authorization: Bearer <token>`.

**Auth:** `POST /auth/register` · `POST /auth/login` · `POST /auth/refresh` · `POST /auth/logout`

**Tracking:** `GET /track/open/{trackingId}.gif` · `POST /track/session/{trackingId}` · `GET /track/events/{trackingId}` (SSE)

**Links:** `POST /links` · `GET /links` · `GET /links/{id}` · `DELETE /links/{id}` · `GET /r/{slug}` · `POST /r/{slug}/unlock`

**Campaigns:** `POST /campaigns` · `GET /campaigns` · `GET /campaigns/{id}` · `GET /campaigns/{id}/analytics` · `POST /campaigns/{id}/emails`

---

## Security

- BCrypt (strength 12) for all passwords — never plaintext
- JWT secrets via environment variables only
- Refresh token rotation — previous token invalidated on each refresh
- Redis sliding-window rate limiting on pixel and redirect endpoints
- SHA-256 slugs prevent enumeration attacks
- CORS locked to configured frontend origin in production

---

## Roadmap

- [ ] Webhook support on open/click events
- [ ] Bulk CSV import for campaign recipients
- [ ] Unsubscribe link management (CAN-SPAM)
- [ ] White-label domain for link shortener
- [ ] React Native mobile app

---

## License

MIT — see [LICENSE](LICENSE).
