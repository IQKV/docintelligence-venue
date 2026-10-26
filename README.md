# Document Intelligence Venue 📍

Shared venue domain model and MyBatis persistence for the DocIntelligence suite. One library, consumed by every service that reads or writes the venue tables.

## About

The suite used to carry its own copy of the venue model in each service. This artifact replaces those copies:

- **One flat package** — `com.iqkv.docintelligence.venue`. The bounded context is carried by the type name, not by a sub-package: `MasterVenue` is platform-owned reference data in `public.master_venue`, while the tenant-side types read and write a `t_{tenantKey}` schema.
- **Model and persistence together** — the mapper interface, its XML and the MyBatis type handlers ship in the same jar, so a statement and the type it maps can never drift apart.
- **No services, no beans** — no HTTP surface, no business logic, no DDL. `docintelligence-catalog-service` owns every migration; `docintelligence-master-venue-worker` is the sole writer of `public.master_venue*`.
- **Drop-in for consumers** — MyBatis is an `optional` dependency and the mapper XML is resolved by the consumers' existing `classpath*:mappers/` scan, so no configuration change is needed to pick it up.

## Consumers

| Service                                     | Use                                           |
| ------------------------------------------- | --------------------------------------------- |
| `docintelligence-master-venue-worker`       | Writes the master catalog — the owning writer |
| `docintelligence-catalog-service`           | Admin CRUD and the HTTP surface               |
| `docintelligence-catalog-processing-worker` | Tenant extraction and catalog matching        |

A change to a shared type compiles in all three at once.

## Quick Links

- [API Documentation](./docs/api/README.md)
- [Architecture Overview](./docs/architecture/README.md)
- [Deployment Guide](./docs/deployment/README.md)
- [Contributing Guidelines](.github/CONTRIBUTING.md)

## Tech Stack

- Java 25
- MyBatis (`optional` runtime — every consumer already provides it)
- Maven 3.9+

## Development

```bash
# Build and install to local Maven repository
./mvnw clean install -Dcheckstyle.skip=true
```
