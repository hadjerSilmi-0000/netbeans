# MiniShop — Jakarta EE Architecture Refactoring

> **University project** — Practical work on enterprise architecture improvement.  
> Course: Architecture et Gestion des Systèmes d'Information Avancés  
> Stack: Jakarta EE 11 · GlassFish 7 · Derby DB · Maven · JUnit 4 · ArchUnit 1.4.1

---

## Overview

MiniShop is a Jakarta EE web application that started as a basic shopping cart (Servlet + EJB + JPA) and was progressively refactored to reflect real-world enterprise architecture. The project identifies **7 architectural weaknesses** in the original design and addresses each one with a concrete pattern-based solution, backed by unit tests and architecture tests.

---

## The 7 Weaknesses & Fixes

| # | Weakness | Problem | Solution Applied |
|---|----------|---------|-----------------|
| W1 | SRP Violation | Servlet handled HTTP, validation, parsing, coordination, and response — 5 responsibilities in one class | MVC refactoring — Servlet is now a pure controller (3 lines) |
| W2 | String-based Cart | Cart stored items as `List<String>` — no quantity grouping, no subtotal, no domain logic | Value Object pattern — `CartItem` with `productId`, `price`, `quantity`, `subtotal()` |
| W3 | Anemic DAO | `ProductDAO` had only `findById` — no `findAll` (forcing hardcoded HTML), no `updateStock` (broken inventory) | Repository Pattern — full DAO covering all persistence needs of the `Product` entity |
| W4 | No Validation Pipeline | Only a null check existed — out-of-stock and unavailable products were never blocked | Chain of Responsibility — `NullCheck → Format → Existence → Availability` (4 independent stages) |
| W5 | No Service Layer | Servlet injected `MiniShopEJB` and `MiniShopFull` directly — tight coupling, untestable | Service Layer (Façade) — `ShopService` interface + `ShopServiceImpl @Stateless EJB` |
| W6 | No Plugin System | Adding features (discounts, suggestions) required modifying `MiniShopFull` directly (OCP violated) | Microkernel Architecture — `CartPlugin` interface + `PluginRegistry @Singleton` |
| W7 | No Event System | Cart additions triggered nothing — no logging, no stock reservation, no notifications | Event-Driven Architecture — `CartEventBus @Singleton` + `LogListener`, `StockListener`, `NotificationListener` |

---

## Architecture

The application follows a strict **5-layer architecture** where dependencies flow top → bottom only.

```
┌─────────────────────────────────────────────────────┐
│  L1 — Web Layer                                     │
│  MiniShopServFull (@WebServlet) · index.html · JSP  │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  L2 — Service Layer                (W5)             │
│  ShopService (interface) · ShopServiceImpl          │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  L3 — Business Layer          (W1, W4, W6, W7)      │
│  MiniShopEJB (@Stateless) · MiniShopFull (@Stateful)│
│  ValidationPipeline · PluginRegistry · CartEventBus │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  L4 — Persistence Layer            (W3)             │
│  ProductDAO (interface) · ProductDAOImpl            │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  L5 — Domain Layer                 (W2)             │
│  Product (@Entity) · CartItem (Value Object)        │
└─────────────────────────────────────────────────────┘
```

---

## Project Structure

```
tpshop/
├── src/
│   ├── main/java/
│   │   ├── servlets/
│   │   │   └── MiniShopServFull.java       # Web Layer — HTTP controller only
│   │   ├── EJB/
│   │   │   ├── MiniShopEJB.java            # Business logic — add-to-cart processing
│   │   │   ├── MiniShopFull.java           # @Stateful cart EJB
│   │   │   ├── ShopService.java            # Service Layer interface
│   │   │   ├── ShopServiceImpl.java        # Service Layer implementation
│   │   │   ├── event/                      # W7 — Event-Driven system
│   │   │   │   ├── CartEventBus.java
│   │   │   │   ├── CartEvent.java
│   │   │   │   ├── CartEventListener.java
│   │   │   │   ├── LogListener.java
│   │   │   │   ├── StockListener.java
│   │   │   │   └── NotificationListener.java
│   │   │   └── plugin/                     # W6 — Microkernel Plugin system
│   │   │       ├── CartPlugin.java
│   │   │       ├── PluginRegistry.java
│   │   │       ├── DiscountPlugin.java
│   │   │       ├── SuggestionPlugin.java
│   │   │       └── LoyaltyPlugin.java
│   │   ├── dao/
│   │   │   ├── ProductDAO.java             # W3 — Repository interface
│   │   │   └── ProductDAOImpl.java         # Full Repository implementation
│   │   ├── entity/
│   │   │   ├── Product.java                # JPA entity
│   │   │   └── CartItem.java               # W2 — Value Object
│   │   └── validation/                     # W4 — Validation Pipeline
│   │       ├── Validator.java
│   │       ├── ValidationPipeline.java
│   │       ├── NullCheckValidator.java
│   │       ├── FormatValidator.java
│   │       ├── ExistenceValidator.java
│   │       └── AvailabilityValidator.java
│   ├── main/webapp/
│   │   ├── index.html                      # Product selection UI
│   │   └── newjsp.jsp                      # Cart display (JSTL)
│   └── test/java/
│       ├── SRPViolationTest.java           # Proves W1 weakness
│       ├── SRPFixedTest.java               # Proves W1 fix
│       ├── CartStringWeaknessTest.java     # Proves W2 weakness
│       ├── CartItemFixedTest.java          # Proves W2 fix
│       ├── AnemicDAOWeaknessTest.java      # Proves W3 weakness
│       ├── RepositoryFixedTest.java        # Proves W3 fix
│       ├── ValidationWeaknessTest.java     # Proves W4 weakness
│       ├── ValidationPipelineFixedTest.java # Proves W4 fix
│       └── LayerArchitectureTest.java      # 27 ArchUnit rules enforcing all layers
└── pom.xml
```

---

## Tests

**24 unit tests + 27 ArchUnit architecture rules — all passing.**

Each weakness has a paired test file: one proving the original problem exists, one proving the fix works. No GlassFish or HTTP environment needed — all unit tests run standalone with `mvn test`.

```
SRPViolationTest          Tests run: 3, Failures: 0
SRPFixedTest              Tests run: 3, Failures: 0
CartStringWeaknessTest    Tests run: 3, Failures: 0
CartItemFixedTest         Tests run: 3, Failures: 0
AnemicDAOWeaknessTest     Tests run: 3, Failures: 0
RepositoryFixedTest       Tests run: 3, Failures: 0
ValidationWeaknessTest    Tests run: 3, Failures: 0
ValidationPipelineFixedTest Tests run: 3, Failures: 0
LayerArchitectureTest     27 ArchUnit rules — all enforced
```

Architecture rules cover: layered dependency direction, no servlet-to-DAO shortcuts, `@Stateful`/`@Stateless`/`@Singleton` placement, plugin and listener interface compliance, `CartEvent` as a pure data carrier, no cyclic dependencies.

---

## Running the Project

**Prerequisites:** JDK 17+, GlassFish 7, Derby DB, Maven 3.x

```bash
# Build
mvn clean package

# Run unit tests
mvn test

# Deploy to GlassFish (from NetBeans or manually)
# Create JDBC resource: jdbc/MiniShopDS → Derby database
# Deploy tpshop-1.0-SNAPSHOT.war
# Access: http://localhost:8080/tpshop/
```

**Database:** Configure a Derby datasource named `jdbc/MiniShopDS` in GlassFish. The `PRODUCT` table must exist in the `APP` schema with columns: `PRODUCT_ID`, `PURCHASE_COST`, `QUANTITY_ON_HAND`, `AVAILABLE`, `DESCRIPTION`.

---

## Architectural Patterns Used

- **MVC** — strict separation of HTTP controller from business logic
- **Service Layer / Façade** — single entry point per use case
- **Repository Pattern** — DAO covers all persistence operations for its entity
- **Chain of Responsibility** — 4-stage validation pipeline, each stage independently testable
- **Value Object** — `CartItem` encapsulates domain data and behavior
- **Microkernel / Plugin** — features attach to the cart without modifying its core
- **Observer / Event-Driven** — `CartEventBus` decouples producers from consumers

---

## Dependencies

```xml
<dependencies>
    <dependency>jakarta.platform:jakarta.jakartaee-api:11.0.0-M1</dependency>
    <dependency>junit:junit:4.13.2 (test)</dependency>
    <dependency>com.tngtech.archunit:archunit-junit4:1.4.1 (test)</dependency>
</dependencies>
```
