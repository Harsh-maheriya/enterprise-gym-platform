CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Keys tying the tables together
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,

    -- The Composite Primary Key guarantees no duplicate role assignments
    PRIMARY KEY (user_id, role_id)
);

-- Crucial Index: Spring Security will query this every time a user logs in
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
