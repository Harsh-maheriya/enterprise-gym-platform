CREATE TABLE karma_balances (
    user_id UUID PRIMARY KEY,
    current_balance INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Key strictly linking it 1-to-1 with the core Identity table
    CONSTRAINT fk_karma_balances_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Defensive Constraint: Prevents a user from spending points they don't have
    CONSTRAINT chk_karma_balance_positive CHECK (current_balance >= 0)
);
