# AI Agent Development Guide

## Project Overview

**Document Intelligence Venue** — shared venue domain model and MyBatis persistence for the DocIntelligence suite. Holds the platform-owned master catalog types and, as the tenant side lands, the tenant-scoped venue types. It is a library: no application class, no HTTP surface.

**Key characteristics:**

- Java 25, single Maven module, parent `com.iqkv:boot-parent-pom`
- One flat package per bounded context — `com.iqkv.docintelligence.venue`; the context is carried by the type name (`MasterVenue` = platform table, `Venue` = tenant table), not by a sub-package
- Model classes plus the mapper interface, its XML and the MyBatis type handlers — no services, no Spring beans
- MyBatis is the only compile dependency and it is `optional` — every consumer already provides it
- Mapper XML ships in the jar; consumers resolve it through their existing `classpath*:mappers/` scan
- ArchUnit tests verify package structure
- Checkstyle enforced at `validate` phase via `com.iqkv:checkstyle-config`
- JaCoCo: ≥ 90% instruction + line + branch coverage per class (`jacoco.skip=true` by default; CI enables it)

## Dependency Relationship

```
docintelligence-venue                     (venue model, mappers, type handlers)
        ↓
docintelligence-catalog-service           (HTTP surface, admin CRUD, DDL owner)
docintelligence-catalog-processing-worker (tenant extraction and catalog matching)
docintelligence-master-venue-worker       (sole writer of public.master_venue*)
```

One artifact, three consumers: a change to a shared type compiles in all three at once, so read every call site before editing. This replaces the earlier rule where each service carried its own copy of the model — do not reintroduce the copies.

## Project Structure

```
src/main/java/com/iqkv/docintelligence/venue/
├── package-info.java
├── MasterVenue.java              # One row of the platform-owned public.master_venue
├── MasterVenueMapper.java        # MyBatis reads and writes for public.master_venue
└── mybatis/
    ├── JsonTypeHandler.java      # JSON/JSONB column bound as Types.OTHER
    └── UuidTypeHandler.java      # UUID registration for the session

src/main/resources/mappers/venue/
└── MasterVenueMapper.xml         # Statements behind MasterVenueMapper — ships in the jar
```

Tenant-scoped types (`Venue`, `VenueAsset`, `VenueMetadata`) land in the same flat `venue` package, not in a `tenant/` sub-package.

## Design Rules

### What belongs here

- Venue domain types for both contexts: platform (`MasterVenue`) and tenant (`Venue`, `VenueAsset`, `VenueMetadata`)
- The MyBatis mapper interfaces for those types, with a matching XML file per mapper
- MyBatis type handlers shared with those mappers (`JsonTypeHandler`, `UuidTypeHandler`)

### What does NOT belong here

- Spring beans of any kind — `@Service`, `@RestController`, `@Configuration`, `@Component`
- Business logic — this is a model and persistence layer, not an application layer
- DTOs and their mappers (`MasterVenueDtos`, `MasterVenueDtoMapper`): HTTP payload shapes belong to the HTTP surface in `docintelligence-catalog-service`
- Liquibase changelogs, DDL, and schema creation — `docintelligence-catalog-service` owns every migration
- Tenant-schema switching (`MyBatisSchemaInterceptor`) and `MyBatisConfig` — each service decides per workload whether a tenant context applies
- Persistence annotations (JPA, Hibernate)

### Table ownership

`docintelligence-master-venue-worker` is the sole writer of `public.master_venue`, `public.master_venue_alias`, and `public.master_venue_external`. `docintelligence-catalog-service` reads them for the admin CRUD surface. Nothing in this repository issues DDL.

### Mapper and XML move together

A method on a mapper interface needs its statement in the matching XML in the same change, and the `resultMap` column list follows the type's fields. A half-applied pair fails at runtime, not at build time.

### Compatibility

The artifact is consumed by three services. Adding a type or a field is compatible. Changing a field's type, renaming a type, or removing a mapper method is **breaking for all consumers at once** — check all three before editing.

### Javadoc

Every public type and public method requires Javadoc. On model types, name the schema and the table the type maps to and, where it applies, the writer that owns the table. On mapper methods, document ordering and null handling of the returned collection.

## Code Standards

```java
// Model type — name the table and its owning writer in the Javadoc
/**
 * A platform-owned master venue — one row of the {@code public.master_venue} reference dataset.
 *
 * <p>Platform-level data, not tenant-scoped. The table is created and owned by
 * {@code docintelligence-catalog-service}.
 */
public class MasterVenue {

  private UUID id;
  private String metadata;  // raw JSONB document, carries the mandatory _schema_version key
}

// Mapper method — @Mapper on the interface, @Param on every argument, statement in the XML
List<MasterVenue> findAll(@Param("limit") int limit, @Param("offset") int offset);

// Type handler — no PostgreSQL driver dependency: bind as OTHER and let the driver coerce
ps.setObject(i, parameter, Types.OTHER);
```

## Execution Discipline

- Read the call sites in all three consumers before changing a shared type — one edit compiles in three services.
- A mapper interface change without its XML statement, or the reverse, is an incomplete change — apply both or neither.
- Type handlers are registered by class in each service's `MyBatisConfig`; keep handler class names stable or update all three registrations.
- Never add a PostgreSQL driver dependency to make a handler compile — the handlers bind through `Types.OTHER` on purpose.
- After two identical build failures without new evidence, change approach — do not retry blindly.
- Run `./mvnw verify -Djacoco.skip=false` to include coverage gates when touching model or handler classes.

## Minimal Change Discipline

Solve the whole problem with the least new code. The best code is the code never written.

### Scope before you write

Read the task and the code it touches. List every place the change must reach: callers, tests, fixtures, config, exports. Check what it could break for users: data it would destroy or expose, callers that stop working. That is scope. Extra features are not.

### Pick the smallest complete change

Take the first option that fully works:

1. Does it need to exist? Drop features, options and flexibility nobody asked for — name them in one line so the user can decide.
2. Already in this codebase (helper, component, service, pattern)? Use it the way the surrounding code does.
3. Standard library or platform feature? Use it, unless the project has its own.
4. An installed dependency? Use it. Never add a dependency for a few lines.
5. Can it be one line a reader gets at a glance? One line.
6. Otherwise: the minimum code that works.

Be lazy about the solution, never about the change itself:

- Finish every part the task needs, including the callers, tests and fixtures the change breaks.
- No abstraction, wrapper, option, config, boilerplate or "for later" code nobody asked for. Keep values in the form the platform already gives you. Deletion beats addition. Keep the layers, interfaces and conventions the codebase already has.
- The shortest working diff wins, once you know everything it must touch. A one-liner that needs decoding is not short.
- Comment only the why the code cannot show, in one line.
- Bug fix: grep every caller of the function you touch, then fix the root cause once in the shared code.
- Code you move or merge keeps its error handling and validation.
- Between options of equal size, take the one that is correct on edge cases.
- New non-trivial logic (a branch, a loop, a parser, money or security, or a whole new script) leaves one small test or an assert-based self-check. Trivial changes need none.
- A shortcut with a known limit gets a one-line comment: `shortcut: <the limit>, <when to upgrade>`.
- Push back when the need does not justify a part: state the smaller option in one line instead of building the bigger one silently.

Never cut: validation at trust boundaries, error handling that prevents data loss, security, accessibility, the calibration real hardware needs, anything the user explicitly asked for.

### Token discipline

- Locate with Grep/Glob, then read only the ranges you need — not whole trees or whole files.
- Prefer editing existing files to creating new ones; reuse code that is already there before writing.
- Never paste file contents back in a reply. Cite the path and lines.
- Keep replies to the outcome: what changed, what passed, any risk (see AI Communication Standards).

## Security

- `MasterVenue.metadata` holds raw JSONB canonical metadata collected from public sources — treat venue names, addresses, phone numbers, and emails as personal data; no real values in tests, fixtures, or Javadoc examples.
- Mapper XML binds every value as a parameter — never interpolate a value into a statement with `${}`.
- No credentials, connection strings, or tokens in source, tests, or Javadoc examples.

## AI Agent Development Guidelines

### Code generation principles

1. **Model and persistence only**: no services, no Spring beans, no HTTP types
2. **Interface, XML and `resultMap` move together**: a mapper method without its statement is an incomplete change
3. **Handlers stay framework-free**: no PostgreSQL driver, no Spring — bind through `Types.OTHER`, and registration stays in each service's `MyBatisConfig`
4. **Javadoc contracts**: schema and table on model types; ordering and null handling on mapper methods

### Approval workflow (MANDATORY)

Ask before applying. For any file creation, modification, or deletion:

```
1. ANALYZE  → understand the request, read all known implementors and callers
2. PRESENT  → describe changes, show interface signature, list affected consumers
3. WAIT     → stop and wait for explicit approval
4. APPLY    → only after approval
5. VERIFY   → run ./mvnw verify, report results concisely
```

**Approval phrases:** "Yes", "Proceed", "Apply", "Do it", "Go ahead", "Looks good"

Operations NOT requiring approval: reading files, explaining structure, running type checks.

## Commit Standards

Format: `type(scope): subject`

- Subject: imperative, lowercase, no trailing period, ≤ 72 chars
- Types: `feat`, `fix`, `improvement`, `refactor`, `docs`, `test`, `chore`, `ci`, `revert`
- Scope: affected type or package (e.g., `master-venue`, `master-venue-mapper`, `mybatis`, `package-info`)
- For `fix`: describe the symptom and trigger, not the code change
    - ✅ `fix(master-venue-mapper): findByNameIgnoreCase misses venues whose name differs only in case`
    - ❌ `fix(master-venue-mapper): add lower() to the where clause`

Examples:

- `feat(venue): add the tenant Venue model`
- `fix(master-venue-mapper): findAll returns the wrong page when limit is zero`
- `refactor(mybatis): move JsonTypeHandler next to the mapper it serves`
- `docs(master-venue): state the owning writer of public.master_venue`

## Development Commands

```bash
# Build, Checkstyle, and tests (coverage gate disabled by default)
./mvnw verify

# Include JaCoCo coverage gates
./mvnw verify -Djacoco.skip=false

# Checkstyle only
./mvnw checkstyle:check

# Skip Checkstyle for a quick compile check
./mvnw verify -Dcheckstyle.skip=true
```

## Evaluation

How we judge whether an agent task in this repository is actually done. Evaluation is evidence-based — a claim without the command and its observed result is not a pass.

> This section starts minimal and grows with the repository. Add a gate only when a change type repeatedly slips through without it.

### Definition of done

A task is evaluated as done only when all of the following hold:

- Every gate required by the change type below has run and passed.
- The final report names the exact command(s) run and the observed result (pass/fail) — per AI Agent Development Guidelines.
- No required gate was skipped silently. A gate that could not run is reported as a gap, never as a pass.

### Required gates by change type

| Change type                             | Required gates                                                                            |
| --------------------------------------- | ----------------------------------------------------------------------------------------- |
| Any change                              | `./mvnw -q compile`, then the narrowest targeted `./mvnw test -Dtest=ClassName`           |
| New or changed model type               | Unit test covering the type, then full `./mvnw verify`                                    |
| Mapper interface or XML change          | Statement/interface pair checked against a service-level test, then full `./mvnw verify`  |
| Type handler change                     | Unit test for the handler, then full `./mvnw verify`                                      |
| Public API change (type, field, method) | Check all three consuming services, then full `./mvnw verify`                             |
| Package move or `package-info` change   | ArchUnit package-structure test, then full `./mvnw verify`                                |
| Dependency or build config              | Full `./mvnw verify`                                                                      |
| Javadoc or `AGENTS.md` only             | No code gate — state that none applies                                                    |

### Ordering

Run the narrowest relevant gate first, then the full set for the change type. On the first failure, stop and report it — do not continue on an unproven base (see Execution Discipline).
