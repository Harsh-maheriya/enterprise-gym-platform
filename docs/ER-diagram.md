# Enterprise Gym Platform - Database Schema

This document outlines the strict BCNF database schema for the core modules of the Gym System.

## Entity-Relationship (ER) Diagram (Domains 1, 2, and 3)

```mermaid
erDiagram
    %% Identity Domain
    users ||--o{ user_roles : "assigned"
    roles ||--o{ user_roles : "grants"
    
    %% Finance Domain
    users ||--o{ invoices : "owes"
    users ||--o{ ledger_entries : "balance history"
    invoices ||--o{ payments : "receives attempts"
    payments ||--o{ ledger_entries : "triggers (optional)"

    %% Inventory & Scheduling Domain
    locations ||--o{ facilities : "contains room"
    facilities ||--o{ class_sessions : "hosts"
    class_types ||--o{ class_sessions : "defines template"
    users ||--o{ class_sessions : "teaches (trainer)"
    class_sessions ||--o{ bookings : "has spots"
    users ||--o{ bookings : "reserves spot"

    users {
        UUID id PK
        VARCHAR email
        VARCHAR password_hash
    }
    roles {
        UUID id PK
        VARCHAR name
    }
    user_roles {
        UUID user_id PK,FK
        UUID role_id PK,FK
    }
    
    invoices {
        UUID id PK
        UUID user_id FK
        DECIMAL total_amount
    }
    payments {
        UUID id PK
        UUID invoice_id FK
        VARCHAR idempotency_key
    }
    ledger_entries {
        UUID id PK
        UUID user_id FK
        UUID payment_id FK
        DECIMAL amount
    }
    
    locations {
        UUID id PK
        VARCHAR name
    }
    facilities {
        UUID id PK
        UUID location_id FK
        VARCHAR name
        INT max_occupancy
    }
    class_types {
        UUID id PK
        VARCHAR name
        INT base_duration_minutes
    }
    class_sessions {
        UUID id PK
        UUID class_type_id FK
        UUID facility_id FK
        UUID trainer_id FK
        INT max_capacity
    }
    bookings {
        UUID class_session_id PK,FK
        UUID user_id PK,FK
        VARCHAR status
    }
```

## Architectural Notes (The "Bank-Worthy" Features)
* **Idempotency (`payments`):** Uses a `UNIQUE` constraint on `idempotency_key` to prevent double-charging users during network retries.
* **Immutable Ledger (`ledger_entries`):** Protected by a PostgreSQL Trigger blocking `UPDATE`/`DELETE`, ensuring a strict append-only financial audit trail.
* **Role-Based Access Control (RBAC):** Normalized into `roles` and `user_roles` following the Open/Closed Principle (OCP), preventing schema changes when new roles are created.
* **Composite Primary Keys:** `user_roles` and `bookings` use composite primary keys to physically guarantee no duplicate assignments or double-bookings can occur.
* **Race-Condition Prevention:** The Java `BookingService` leverages pessimistic row-level locking (`SELECT ... FOR UPDATE`) on `class_sessions` to guarantee `max_capacity` is never exceeded during concurrent bookings.
