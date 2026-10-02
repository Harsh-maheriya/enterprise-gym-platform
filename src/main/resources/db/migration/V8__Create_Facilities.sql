CREATE TABLE facilities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    location_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_occupancy INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Connects this room to a specific physical branch
    CONSTRAINT fk_facility_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_facility_status CHECK (status IN ('ACTIVE', 'MAINTENANCE', 'CLOSED')),
    CONSTRAINT chk_max_occupancy CHECK (max_occupancy > 0),
    
    -- The Multi-Location Flex:
    -- This guarantees a branch cannot have two "Studio A"s.
    -- However, it perfectly allows BOTH the Downtown Branch AND the Uptown Branch 
    -- to each have their own independent "Studio A".
    UNIQUE (location_id, name)
);

-- SQL Flex: Seeding data dynamically using subqueries to grab the generated Location UUIDs
INSERT INTO facilities (location_id, name, description, max_occupancy)
SELECT id, 'Yoga Studio A', 'Heated hardwood studio', 30 FROM locations WHERE name = 'Downtown Core Branch';

INSERT INTO facilities (location_id, name, description, max_occupancy)
SELECT id, 'Main Weight Room', 'Free weights and racks', 100 FROM locations WHERE name = 'Downtown Core Branch';

INSERT INTO facilities (location_id, name, description, max_occupancy)
SELECT id, 'Olympic Pool', '6-lane indoor pool', 50 FROM locations WHERE name = 'Uptown Suburb Branch';
