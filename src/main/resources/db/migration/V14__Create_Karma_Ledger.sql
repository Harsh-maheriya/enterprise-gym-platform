CREATE TABLE karma_ledger (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    amount INT NOT NULL, 
    transaction_type VARCHAR(50) NOT NULL,
    reference_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Connect it to the user
    CONSTRAINT fk_karma_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Prevent inserting a row with 0 points (as we discussed earlier!)
    CONSTRAINT chk_karma_amount_not_zero CHECK (amount != 0),

    -- Ensure developers use valid transaction types
    CONSTRAINT chk_karma_transaction_type CHECK (transaction_type IN ('CLASS_REWARD', 'REWARD_CLAIM'))
);

-- Index for insanely fast balance calculations:
-- SELECT SUM(amount) FROM karma_ledger WHERE user_id = ?
CREATE INDEX idx_karma_ledger_user_id ON karma_ledger(user_id);
