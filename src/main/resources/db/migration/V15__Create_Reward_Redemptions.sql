CREATE TABLE reward_redemptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    reward_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_FULFILLMENT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_redemption_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_redemption_reward FOREIGN KEY (reward_id) REFERENCES reward_catalog(id) ON DELETE RESTRICT,
    
    CONSTRAINT chk_redemption_status CHECK (status IN ('PENDING_FULFILLMENT', 'FULFILLED'))
);
