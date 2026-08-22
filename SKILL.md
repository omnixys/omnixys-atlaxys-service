<!-- repository: services/address | kind: SERVICE | stack: java-maven -->

# address — Skill: Service Development

> Workflow for address (services/address). Execute this workflow before, during, and
> after changes in this repository.

## Repository Facts

- Kind: Service
- Package: `com.omnixys:address` (version: from pom.xml)
- Runtime: Java 26 (Maven, Spring Boot parent 4.1.0)
- Description: Omnixys Address Service – mature Spring Boot service (GraphQL federation, JPA, Flyway, Redis, OAuth2).
- Architecture: src/main/java/com/omnixys/address/{admin, analytics, config, controllers, dev, errors, handlers, health, models, repository, resolvers, security, services}
- Database: PostgreSQL (spring-boot-starter-data-jpa) + Redis (spring-boot-starter-data-redis); Migrations: Flyway (src/main/resources/db/migration/{production,dev})
- API: GraphQL (spring-boot-starter-graphql + federation-graphql-java-support)
- Messaging: none locally (no Kafka starter in pom)
- Tests: JUnit 5 + spring-graphql-test (src/test/java/com/omnixys/address/...)


## Workflow

### 1. Understand the change

- Identify the affected bounded context within `src/main/java/com/omnixys/address/{admin, analytics, config, controllers, dev, errors, handlers, health, models, repository, resolvers, security, services}`.
- Inspect consumers of the GraphQL operations and Kafka events you may touch.
- Never weaken authentication or authorization to make a test pass.

### 2. Implement

- Follow the existing module layout and naming conventions.
- Reuse `omnixys/packages` (shared contracts, cache, kafka, observability, security, ...)
  before reimplementing shared infrastructure.
- Keep tenant isolation intact (`Mature service with 124+ Java files. Migration files under db/migration. Never modify released Flyway migrations.`).

### 3. Write tests

- Unit tests exercise isolated business behavior.
- Integration tests cover repository/Prisma, GraphQL, Kafka, and auth boundaries.
- Cover tenant-isolation and error-contract cases when the code path touches them.

### 4. Validate

## Validation

Run each applicable check and record the result as `PASS`, `FAIL`, `PRE-EXISTING
FAILURE`, or `NOT RUN` (with a reason). Never convert `NOT RUN` into `PASS`.

  - `mvn -B dependency:go-offline`
  - `mvn -B checkstyle:check (when configured)`
  - `compile phase of mvn verify`
  - `mvn -B test`
  - `mvn -B verify (Testcontainers when used)`
  - `mvn -B clean verify`

## Commit

- Use Conventional Commits (`<type>(<scope>): <summary>`), e.g. `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `perf`.
- Stage only files belonging to the logical change. Run `git diff --check` before committing.
- Commit locally; never push.

## Definition of Done

See the "Definition of Done" section in `AGENTS.md`. Before finishing, confirm
`AGENTS.md` and `SKILL.md` remain accurate for this repository.
