CREATE TABLE class_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    base_duration_minutes INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Ensures nobody can accidentally create a class that lasts for 0 or negative minutes
    CONSTRAINT chk_class_duration CHECK (base_duration_minutes > 0)
);

-- Seed the catalog with some standard enterprise offerings
INSERT INTO class_types (name, description, base_duration_minutes) VALUES 
    ('Power Yoga', 'High intensity flowing yoga', 60),
    ('HIIT Bootcamp', 'Interval training using bodyweight and kettlebells', 45),
    ('Spin Class', 'Indoor cycling with high energy music', 45);
