# Enterprise Gym Platform - Database Schema

This document outlines the strict BCNF (Boyce-Codd Normal Form) database schema for the financial core of the Gym System.

## Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    users ||--o{ invoices : "owes money"
    users ||--o{ ledger_entries : "has balance history"
    invoices ||--o{ payments : "receives payment attempts"
    payments ||--o{ ledger_entries : "triggers accounting record (optional)"

    users {
        UUID id PK
        VARCHAR email
        VARCHAR password_hash
        VARCHAR first_name
        VARCHAR account_status
    }
    invoices {
        UUID id PK
        UUID user_id FK
        DECIMAL total_amount
        VARCHAR status
    }
    payments {
        UUID id PK
        UUID invoice_id FK
        DECIMAL amount_paid
        VARCHAR payment_method
        VARCHAR idempotency_key
        VARCHAR status
    }
    ledger_entries {
        UUID id PK
        UUID user_id FK
        UUID payment_id FK
        VARCHAR transaction_type
        DECIMAL amount
        TEXT description
    }
```

## Architectural Notes (The "Bank-Worthy" Features)
* **Idempotency (`payments`):** Uses a `UNIQUE` constraint on `idempotency_key` to prevent double-charging users during network retries.
* **Immutable Ledger (`ledger_entries`):** Protected by a PostgreSQL Trigger that actively blocks any `UPDATE` or `DELETE` commands, ensuring a strict append-only financial audit trail.
* **Referential Integrity:** `ON DELETE RESTRICT` guarantees that users with financial history cannot be accidentally deleted from the system.
