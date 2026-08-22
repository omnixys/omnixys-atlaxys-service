# 🌍 Address-Service

### Global Address & Geo Intelligence Microservice

Part of the **Omnixys Ecosystem**

---

## 📖 Overview

**Address** is a globally scalable, high-performance Address & Geo-Data Microservice designed for enterprise systems such as FinTech, Identity, Event Management, KYC, and Compliance platforms.

It provides a normalized, geo-spatially optimized data model for worldwide location data and acts as the single source of truth for all geo-related information inside the Omnixys ecosystem.

> Address is not just an address CRUD service —
> it is a structured global geo intelligence layer.

---

## 🎯 Purpose

Address solves common architectural problems:

- ❌ Duplicate country/state logic in multiple services
- ❌ Inconsistent address formatting
- ❌ Missing geo coordinates
- ❌ Poor performance on spatial queries
- ❌ Weak data integrity across regions

Address provides:

- ✅ Normalized global address hierarchy
- ✅ ISO-compliant country metadata
- ✅ Geo-spatial queries via PostGIS
- ✅ Strong relational integrity
- ✅ Optimized search (trigram + GIST)
- ✅ External data seeding

---

## 🏗 Architecture

Spiegelt die TS-Referenzarchitektur 1:1 (siehe `docs/architecture/service-architecture-charter.md`).

```text
src/main/java/com/omnixys/address/
├── AddressApplication.java       Spring-Boot-Main
├── config/                       AppProperties, GraphqlConfig, CorsConfig, HttpsConfig, BannerService
├── core/                         Scalars, globale Beans
├── security/http/                SecurityHttpHeadersFilter (CSP/HSTS-Pendant zu TS helmet)
├── health/                       HealthController + Database/Keycloak/Kafka/Valkey-Indicators
├── analytics/                    AnalyticsOutboxService, AnalyticsOutboxPublisher
├── handlers/                     Kafka-Listener (AuthenticationHandler, EventHandler)
├── repository/                   Spring-Data-JPA-Repositories (Persistenz, mappt auf TS prisma/)
├── services/                     user/event: read- und write-services (CQRS-Split)
├── resolvers/                    GraphQL-Resolver (UserAddress, EventAddress, Country, …)
└── models/                       entitys/ dtos/ inputs/ payloads/ mappers/ enums/
```

### Tech Stack

| Layer      | Technology                                  |
| ---------- | ------------------------------------------- |
| Language   | Java 26                                     |
| Framework  | Spring Boot 4 (GraphQL)                     |
| Database   | PostgreSQL 18                               |
| Geo Engine | PostGIS                                     |
| Migration  | Flyway                                      |
| ORM        | Spring Data JPA                             |
| Indexing   | GIST + TRGM                                 |
| Seeding    | External APIs (Countries, States, GeoNames) |
| Messaging  | Kafka (@KafkaEvent)                         |
| Cache      | Valkey                                      |
| Health     | /health/liveness, /health/readiness         |

---

## 🌍 Domain Model

### Hierarchical Structure

```
Continent
  └── Subregion
        └── Country
              └── State
                    └── City
                          └── PostalCode
                                └── Street
                                      └── HouseNumber
```

---

## 🧱 Core Entities

### Continent

- id
- name
- code

### Subregion

- id
- name
- continent_id (FK)

### Country

- id
- name
- iso2
- iso3
- numericCode
- population
- areaSqKm
- latitude
- longitude
- flagSvg
- flagPng
- continent_id
- subregion_id

### State

- id
- name
- country_id

### City

- id
- name
- state_id
- latitude
- longitude

### PostalCode

- id
- zip
- city_id

### Street

- id
- name
- city_id
- location (GEOGRAPHY(Point, 4326))

### HouseNumber

- id
- number
- street_id
- location (optional precise coordinate)

---

## 🗺 Geo-Spatial Capabilities

Address uses **PostGIS** for spatial queries.

Example use cases:

- Radius search around coordinates
- Nearest city lookup
- Geofencing
- Distance calculation
- Proximity-based suggestions

### Example Spatial Column

```sql
location GEOGRAPHY(Point, 4326)
```

### Index Strategy

```sql
CREATE INDEX idx_street_location
ON address.street USING GIST (location);

CREATE INDEX idx_street_name_trgm
ON address.street USING GIN (name gin_trgm_ops);
```

---

## 🚀 Seeding Strategy

Address supports controlled seeding via configuration:

```yaml
app:
  seed:
    enabled: true
```

### External Data Sources

#### Countries API

```
https://www.apicountries.com/countries
```

Provides:

- Country names
- ISO2
- ISO3
- Flags (SVG + PNG)

#### States API

```
https://api.countrystatecity.in/v1/countries/{country}/states
```

#### Postal Data

- GeoNames dataset (recommended for large imports)

---

## 🔎 Performance Considerations

- All frequently queried fields are indexed
- Case-insensitive uniqueness on street names
- Trigram search for fuzzy matching
- Cascading deletes only where logically valid
- UUID primary keys for distributed safety

---

## 🔐 Data Integrity Rules

- No duplicate street per city (case-insensitive)
- No duplicate postal code per city
- ISO2 and ISO3 must be unique
- Foreign keys enforce strict hierarchy
- Optional soft-delete strategy (if needed)

---

## 🧪 Example Queries

### Find Streets by Fuzzy Search

```sql
SELECT *
FROM address.street
WHERE name ILIKE '%hauptstr%'
ORDER BY similarity(name, 'hauptstr') DESC;
```

---

### Radius Search

```sql
SELECT *
FROM address.street
WHERE ST_DWithin(
    location,
    ST_MakePoint(9.1829, 48.7758)::geography,
    5000
);
```

---

## 🏢 Usage Within Omnixys

| Service              | Purpose                           |
| -------------------- | --------------------------------- |
| Checkpoint           | Event Locations & Guest Addresses |
| Nexys (FinTech)      | KYC Address Validation            |
| Identity Service     | User Profiles                     |
| Notification Service | Regional Routing                  |
| Compliance Engine    | Residency & Sanction Checks       |

---

## 📦 Deployment

### Docker (Recommended)

```bash
docker-compose up -d
```

Ensure PostGIS image compatibility for architecture (ARM vs AMD).

---

## ⚙️ Local Development

Requirements:

- Java 26
- PostgreSQL 18
- PostGIS extension
- Flyway migrations enabled
- Kafka, Valkey, Keycloak (via Omnixys-Infra compose)

Enable extension manually if needed:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

---

## 🛡 Future Enhancements

- Reverse geocoding endpoint
- Geo-hashing optimization
- Country-specific address formatting rules
- Address validation scoring engine

---

## 📈 Scalability Strategy

- Stateless service
- Horizontal scaling ready
- DB indexing optimized
- Read-heavy architecture
- Partitioning possible for massive postal datasets

---

## 🧠 Design Philosophy

Address follows:

- Strict normalization
- High integrity over convenience
- Global compatibility
- Enterprise-grade performance
- Microservice isolation

---

## 📄 License

GPL-3.0-or-later (as per Omnixys standard)

---

## 👤 Maintainer

**Caleb Gyamfi**
Founder of Omnixys
Architect of scalable distributed systems
