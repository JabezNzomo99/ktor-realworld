# ![RealWorld Example App](logo.png)

> ### [Ktor](https://ktor.io/) codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


### [Demo](https://github.com/gothinkster/realworld)


This codebase was created to demonstrate a fully fledged application built with **[Ktor](https://ktor.io/)** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **[Ktor](https://ktor.io/)** community style guides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Libraries and Tech Specs

To demonstrate Ktor's full capability in building scalable web applications the following Ktor features and libraries were
leveraged on;

## Ktor Features
- [Routing](https://ktor.io/docs/routing-in-ktor.html): Handles incoming requests and provides a simple DSL to capture route mappings.
- [Authentication](https://ktor.io/docs/authentication.html): Handles API authentication.
- [Call Logging](https://ktor.io/docs/logging.html): Logs application events.
- [Status pages](https://ktor.io/docs/status-pages.html): Handles exceptions and errors that occur within a request lifecycle and returns an appropriate response.
- [Content Negotiation](https://ktor.io/docs/serialization.html#built_in_converters): Negotiating media types and serializing/deserializing JSON content.

## External Libraries

### Database
- [Exposed](https://github.com/JetBrains/Exposed): ORM library developed by JetBrains, allowing for model definitions to be written in SQL format, while proving a simple interface and clean DSL.
- [HikariCP](https://github.com/brettwooldridge/HikariCP): Database connection and connection pooling.
- [Flyway](https://flywaydb.org/): Handles database migrations
- [Caffeine](https://github.com/ben-manes/caffeine): High performance in-memory cache based on Guava.

### Testing
- [Jacoco](https://github.com/jacoco/jacoco): Mature test coverage and reporting.
- [Mockk](https://mockk.io/): Easier mocks, stubs definitions using DSL allowing for easier testing.
- [Ktor-tests](https://ktor.io/docs/testing.html): Provides Ktor test engine to allow for high fidelity end to end integration tests.

### Logging
- [Logback](http://logback.qos.ch/): Mature logging library.

### Utility
- [Koin](https://insert-koin.io/) - Dependency Injection.
- [Gradle with kotlin DSL](https://gradle.org/) - a widely adopted build tool, allowing us to write build scripts and code in Kotlin.
- [Valiktor](https://github.com/valiktor/valiktor) -  type-safe, powerful and extensible fluent DSL to validate objects in Kotlin.


# Getting started

- WIP

