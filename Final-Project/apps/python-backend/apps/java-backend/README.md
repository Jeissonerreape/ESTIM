# Java Backend Architecture Overview

This document describes the architecture of the **ESTIM Java backend**, the flow of requests through its layers, and the rationale behind the chosen design.

The backend is implemented following a **layered, hexagonal, domain-centric** architecture, with clear separation between:

- **Presentation layer** (controllers + DTOs)
- **Application layer** (use cases / services / commands / queries)
- **Domain layer** (entities, value objects, domain events, repositories as ports)
- **Infrastructure layer** (adapters: persistence, security, OAuth, payment, messaging)
- **Event handlers layer** (application reactions to domain events)

---

## 1. Architectural Style

The backend combines:

- **Layered architecture**  
  to separate concerns (presentation, application logic, domain model, infrastructure).

- **Hexagonal / Ports-and-Adapters principles**  
  where the domain and application layers define *interfaces (ports)* and infrastructure provides *implementations (adapters)*.

- **Domain-Driven Design (DDD) influences**  
  with explicit domain models (User, LibraryEntry, WishlistItem, etc.) and domain events (UserRegistered, GameAddedToLibrary, etc.).

This structure improves testability, maintainability, and makes it straightforward to add new delivery mechanisms (e.g., CLI, other APIs) or replace infrastructure (e.g., change database, payment provider, or message broker).

---

## 2. Packages and Layers

### 2.1 Presentation Layer

**Packages:**

- `com.estim.javaapi.controllers`
- `com.estim.javaapi.presentation.*`

**Responsibility:**

- Expose HTTP endpoints.
- Map HTTP requests to application commands/queries.
- Map domain results to HTTP responses / DTOs.
- Handle HTTP-specific concerns (status codes, headers, validation).

**Key elements:**

- **Controllers** (`controllers`):
  - `AuthController`
  - `HealthController`
  - `LibraryController`
  - `OAuthController`
  - `PasswordController`
  - `PaymentMethodController`
  - `ProfileController`
  - `WishlistController`

  Each controller:
  - Receives HTTP requests.
  - Uses *presentation DTOs* for input/output.
  - Delegates to application services in `application.*`.

- **DTOs & mappers** (`presentation`):
  - `presentation.auth`  
    `LoginRequest`, `RegisterUserRequest`, `OAuthLoginRequest`, `OAuthLinkRequest`, `LoginResponse`, `RegisterUserResponse`, `CurrentUserResponse`, `AuthenticatedUserSummary`.
  - `presentation.library`  
    `LibraryEntryResponse`, `UpdateLibraryEntryRequest`, `LibraryMapper`.
  - `presentation.payment`  
    `PaymentMethodRequest`, `PaymentMethodResponse`.
  - `presentation.profile`  
    `UpdateUserProfileRequest`, `UserProfileResponse`, `PrivacySettingsResponse`.
  - `presentation.wishlist`  
    `WishlistItemRequest`, `WishlistItemResponse`, `WishlistMapper`.
  - `presentation.common`  
    `ErrorResponse`, `UserDtoMapper`, `PaymentMethodMapper`.

**Flow:**

1. Controller receives an HTTP request + JSON body.
2. Request JSON is deserialized into a *Request DTO* (e.g., `LoginRequest`).
3. Controller builds a **Command** or **Query** object (application layer).
4. Controller calls the appropriate application service.
5. Service returns domain objects or simple structures.
6. Controller converts them into *Response DTOs* using mappers.
7. Controller returns an HTTP response.

---

### 2.2 Application Layer

**Package:**

- `com.estim.javaapi.application.*`

This layer contains all **use cases / business workflows**, expressed in terms of **Commands**, **Queries**, and **Services**.

Subpackages:

- `application.auth`
- `application.library`
- `application.oauth`
- `application.password`
- `application.payment`
- `application.profile`
- `application.wishlist`
- `application.handlers`

#### 2.2.1 Commands, Queries, Services

Each subpackage is organized in a similar pattern:

- **Commands / Queries**  
  Simple immutable objects that carry the input for a use case.

  Examples:
  - `AuthenticateUserCommand`
  - `RegisterUserCommand`
  - `AddGameToLibraryCommand`
  - `RequestPasswordResetCommand`
  - `ResetPasswordCommand`
  - `AddToWishlistCommand`
  - `ListUserLibraryQuery`
  - `GetUserProfileQuery`
  - `ListWishlistForUserQuery`
  - `RemovePaymentMethodCommand`
  - `UpdateUserProfileCommand`

- **Services**  
  Orchestrate the use case logic, coordinate with repositories and domain logic.

  Examples:
  - `AuthenticateUserService`, `RegisterUserService`, `LogoutUserService`, `GetCurrentUserService`
  - `AddGameToLibraryService`, `ListUserLibraryService`
  - `LoginWithOAuthService`, `LinkOAuthAccountService`
  - `RequestPasswordResetService`, `ResetPasswordService`
  - `AddPaymentMethodService`, `RemovePaymentMethodService`, `ListPaymentMethodsService`
  - `GetUserProfileService`, `UpdateUserProfileService`
  - `AddToWishlistService`, `ListWishlistService`, `RemoveFromWishlistService`, `UpdateWishlistItemService`

- **Policies and helpers**
  - `DefaultPasswordPolicy`, `PasswordPolicy`, `PasswordHasher`
  - `TokenService`
  - `PasswordResetTokenGenerator`, `DefaultPasswordResetTokenGenerator`
  - `PaymentProviderClient` (port for external payment providers)

**Responsibilities:**

- Implement **application logic**, not HTTP or persistence details.
- Use domain objects (entities, value objects) and domain repositories (ports).
- Publish domain events via `DomainEventPublisher` when something relevant happens (e.g., user registered, game added to library).

#### 2.2.2 Application Event Handlers

**Package:**

- `application.handlers`

**Representative classes:**

- `AddGameToLibraryOnGamePurchased`
- `AuditLogger`
- `AuditLogOnUserLoggedIn`
- `CreateActivityOnDomainEvent`
- `SendPasswordResetEmailOnRequested`
- `SendWelcomeEmailOnUserRegistered`
- `UnlockAchievementOnGameEvent`
- `WishilistPricingNotificationHandler`
- `EmailSender` (port-like abstraction)

These classes subscribe to **domain events** and orchestrate **side-effect use cases**, for example:

- When `UserRegistered` is raised → `SendWelcomeEmailOnUserRegistered` may send an email.
- When `UserLoggedIn` is raised → `AuditLogOnUserLoggedIn` writes an audit log.
- When a pricing event occurs → `WishilistPricingNotificationHandler` notifies user.

They form a bridge between **pure domain events** and **external effects** via infrastructure (mail, audit logs, messaging).

---

### 2.3 Domain Layer

**Package:**

- `com.estim.javaapi.domain.*`

This is the **core of the system**, independent of HTTP, databases, frameworks, or messaging infrastructure.

Subpackages:

- `domain.common`
- `domain.user`
- `domain.library`
- `domain.wishlist`

#### 2.3.1 Common domain infrastructure

- `AbstractDomainEvent`
- `DomainEvent`
- `IntegrationEvent`
- `DomainEventPublisher` (port/interface)

Domain events model significant state changes at the domain level (e.g., `UserRegistered`, `GameAddedToWishlist`).

#### 2.3.2 User domain

**Entities, value objects, and enums:**

- `User`, `UserId`
- `UserProfile`, `PrivacySettings`, `UserStatus`
- `Email`
- `PasswordHash`
- `OAuthAccount`, `OauthAccountId`, `OAuthProvider`
- `PaymentMethod`, `PaymentMethodId`, `PaymentProvider`
- `PasswordResetToken`, `PasswordResetTokenId`

**Repositories (ports):**

- `UserRepository`
- `PaymentMethodRepository`
- `PasswordResetTokenRepository`

**Domain events:**

- `UserRegistered`
- `UserEmailVerified`
- `UserLoggedIn`
- `UserProfileUpdated`
- `UserBanned` / `UserUnbanned`
- `PaymentMethodAdded`
- `PaymentMethodRemoved`
- `PasswordResetRequested`
- `PasswordChanged`
- `OAuthAccountLinked`

These events are published through `DomainEventPublisher` and consumed by handlers in `application.handlers` and infrastructure components.

#### 2.3.3 Library domain

- `LibraryEntry`, `LibraryEntryId`
- `GameId` (value object representing a game)
- `LibraryRepository` (port)
- Events:
  - `GameAddedToLibrary`

#### 2.3.4 Wishlist domain

- `WishlistItem`
- `WishlistRepository` (port)

Events:

- `GameAddedToWishlist`
- `GameRemovedFromWishlist`
- `WishlistNotificationTriggered`

The domain layer guarantees invariants and encapsulates rules. Application services rely on these domain objects to enforce business constraints.

---

### 2.4 Infrastructure Layer

**Package:**

- `com.estim.javaapi.infrastructure.*`

Provides **technical adapters** that implement the ports defined in the domain and application layers:

Subpackages:

- `audit`
- `events`
- `mail`
- `oauth`
- `payment`
- `persistence`
- `security`

#### 2.4.1 Audit

- `ConsoleAuditLogger`  
  An audit logging implementation (e.g., logs to console). It can be wired to `AuditLogger` / event handlers.

#### 2.4.2 Events and Outbox

- `EventSerializer`
- `ExternalEventEnvelope`
- `OutboxEvent`
- `OutboxRepository`
- `OutboxEventPublisher`
- `KafkaDomainEventPublisher`
- `SimpleEventBus`

These classes implement:

- An **in-process event bus** (`SimpleEventBus`).
- An **Outbox pattern** for reliable publishing of events (`OutboxEvent`, `OutboxRepository`, `OutboxEventPublisher`).
- An adapter for **external message brokers** such as Kafka (`KafkaDomainEventPublisher`).

They adapt `DomainEventPublisher` (domain port) to actual transport technologies.

#### 2.4.3 Mail

- `ConsoleEmailSender`  
  An implementation of `EmailSender` that sends email-like notifications to the console (or logs). It encapsulates email delivery concerns outside the domain and application core.

#### 2.4.4 OAuth

- `GoogleOAuthClient`
- `SteamOAuthClient`
- `OAuthUserInfo`

These classes provide concrete integrations with external OAuth providers.  
Application services in `application.oauth` call them via interfaces, preserving hexagonal boundaries.

#### 2.4.5 Payment

- `NoopPaymentProviderClient`
- `PagSeguroClient`

These are implementations of the `PaymentProviderClient` port defined in the application layer. Different adapters can be plugged depending on environment (e.g., sandbox, test, production).

#### 2.4.6 Persistence

**Packages:**
- `infrastructure.persistence.library`
- `infrastructure.persistence.user`
- `infrastructure.persistence.wishlist`

Each subpackage typically contains:

- JPA entities (`*JpaEntity`)
- Spring Data repositories (`*JpaRepository`)
- Mappers to translate between Domain entities and JPA entities (`UserMapper`, etc.)
- Implementations of domain repositories (ports):

  - `LibraryRepositoryImpl` implements `LibraryRepository`
  - `UserRepositoryImpl` implements `UserRepository`
  - `WishlistRepositoryImpl` implements `WishlistRepository`
  - `PasswordResetTokenRepositoryImpl` implements `PasswordResetTokenRepository`

This is where **PostgreSQL persistence** is realized, without leaking JPA or SQL into the domain or application layers.

#### 2.4.7 Security

- `AuthenticatedUser`
- `BCryptPasswordHasher`
- `CorsConfig`
- `JwtAuthenticationFilter`
- `JwtAuthenticationProvider`
- `JwtTokenService`
- `SecurityConfig`
- `SecurityContext`

These classes form the integration with **Spring Security**, JWT parsing/validation, and password hashing, while exposing higher-level abstractions used by application and presentation layers.

- `BCryptPasswordHasher` is an implementation of the `PasswordHasher` abstraction from the application layer.
- `JwtTokenService` implements token operations used by `TokenService` / controllers.

---

## 3. End-to-End Request Flow (Example)

### 3.1 User Registration

1. **HTTP Request (Presentation Layer)**
   - `AuthController` receives `POST /auth/register` with a JSON body.
   - JSON is mapped to `RegisterUserRequest`.

2. **Mapping to Application Command**
   - `AuthController` converts `RegisterUserRequest` to `RegisterUserCommand`
     (email, password, display name, etc.).

3. **Application Layer**
   - `RegisterUserService`:
     - Validates password using `PasswordPolicy` / `DefaultPasswordPolicy`.
     - Hasher: uses `PasswordHasher` (implemented by `BCryptPasswordHasher` in infrastructure).
     - Interacts with `UserRepository` (domain port) to persist the new `User` entity.
     - Publishes domain event `UserRegistered` via `DomainEventPublisher`.

4. **Domain Layer**
   - `User` entity enforces invariants (e.g., unique email, valid status).
   - `UserRegistered` event models the significant change.

5. **Event Handlers**
   - `SendWelcomeEmailOnUserRegistered` listens to `UserRegistered`:
     - Uses `EmailSender` (infrastructure adapter `ConsoleEmailSender`) to send a welcome message.
   - `AuditLogOnUserLoggedIn` or other relevant handlers can record activities.

6. **Persistence (Infrastructure)**
   - `UserRepositoryImpl` uses `UserJpaEntity` and `UserJpaRepository` to store user data in PostgreSQL.

7. **HTTP Response**
   - `RegisterUserService` returns domain data (e.g., `User` or a summary).
   - `AuthController` maps this to `RegisterUserResponse` using `UserDtoMapper`.
   - Returns a 201/200 response with the DTO to the client.

### 3.2 Add Game to Library

1. `LibraryController` receives `POST /me/library` with a JSON body.
2. Maps body to `AddGameToLibraryCommand`.
3. Calls `AddGameToLibraryService`.
4. Service fetches user, validates constraints, and calls `LibraryRepository` to persist a new `LibraryEntry`.
5. Domain event `GameAddedToLibrary` is raised.
6. `AddGameToLibraryOnGamePurchased` handles the event (e.g., additional side effects, achievements, stats).
7. Controller maps `LibraryEntry` to `LibraryEntryResponse` using `LibraryMapper`.
8. HTTP response sent to client.

---

## 4. Justification of the Architecture

The architecture used in **com.estim.javaapi** is justified by the following design goals:

1. **Separation of Concerns**
   - Controllers know about HTTP but not about persistence.
   - Application services know about business workflows but not about frameworks.
   - Domain layer models the business rules in a technology-agnostic way.
   - Infrastructure handles frameworks and integrations (Spring, JPA, Kafka, OAuth, etc.).

2. **Testability**
   - Application and domain logic can be tested using in-memory implementations of `UserRepository`, `LibraryRepository`, `PaymentProviderClient`, etc.
   - Controllers can be tested with mocked services.
   - Infrastructure can be tested separately (integration tests with the database or external APIs).

3. **Replaceability (Ports & Adapters)**
   - Easier to swap:
     - Database (change JPA provider or DB engine).
     - OAuth providers (add/remove Google, Steam, etc.).
     - Payment providers (switch between `NoopPaymentProviderClient` and `PagSeguroClient` or others).
     - Messaging implementation (Kafka, RabbitMQ, in-memory).

4. **Scalability & Evolution**
   - Clear domain model and events allow future **microservice extraction** (e.g., a dedicated library service, wishlist service) without redesigning everything.
   - Outbox pattern in `infrastructure.events` prepares the system for **reliable event-driven integration**.

5. **Clarity for Educational Purposes**
   - The project is intended as an academic / teaching platform.
   - Explicit packages (application, domain, infrastructure, presentation) make it easier for students and collaborators to understand:
     - Where to place new use cases.
     - How to define domain events.
     - How to implement new adapters (e.g., new payment providers).

---







