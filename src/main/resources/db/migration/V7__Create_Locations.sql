CREATE TABLE locations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    contact_number VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_location_status CHECK (status IN ('ACTIVE', 'CLOSED', 'RENOVATION'))
);

-- Corporate seed data for our two franchise locations
INSERT INTO locations (name, address, contact_number) VALUES 
    ('Downtown Core Branch', '123 Financial District, City Center', '555-0100'),
    ('Uptown Suburb Branch', '456 Residential Avenue, Suburbia', '555-0200');
