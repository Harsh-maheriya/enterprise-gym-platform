CREATE TABLE class_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    class_type_id UUID NOT NULL,
    facility_id UUID NOT NULL,
    trainer_id UUID NOT NULL,
    
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    max_capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Keys linking the entire gym together (RESTRICT prevents deleting a room that has scheduled classes)
    CONSTRAINT fk_session_class_type FOREIGN KEY (class_type_id) REFERENCES class_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_session_facility FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE RESTRICT,
    CONSTRAINT fk_session_trainer FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE RESTRICT,

    -- Business Logic Defensive Constraints
    CONSTRAINT chk_session_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELED')),
    CONSTRAINT chk_session_capacity CHECK (max_capacity > 0),
    CONSTRAINT chk_valid_time_range CHECK (end_time > start_time)
);

-- Performance Indexes (Because users will constantly query "Show me schedule for this week")
CREATE INDEX idx_session_start_time ON class_sessions(start_time);
CREATE INDEX idx_session_facility ON class_sessions(facility_id);
CREATE INDEX idx_session_trainer ON class_sessions(trainer_id);
