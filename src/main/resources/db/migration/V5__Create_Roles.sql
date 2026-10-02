CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) UNIQUE NOT NULL, 
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Seed the table with our 3 initial roles
-- Because of the Open/Closed Principle, adding a new role later is just adding another row here!
INSERT INTO roles (name, description) VALUES 
    ('ROLE_ADMIN', 'Full system access and management'),
    ('ROLE_TRAINER', 'Can manage class sessions and view attendance'),
    ('ROLE_USER', 'Standard gym member who can book classes and pay invoices');
