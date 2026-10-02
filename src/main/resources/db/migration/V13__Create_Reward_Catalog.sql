CREATE TABLE reward_catalog (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    points_cost INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- A reward must actually cost something
    CONSTRAINT chk_reward_cost_positive CHECK (points_cost > 0)
);

-- Seed some initial physical rewards
INSERT INTO reward_catalog (name, points_cost) VALUES 
    ('Free Protein Shake', 500),
    ('Gym Merchandise T-Shirt', 2000);
