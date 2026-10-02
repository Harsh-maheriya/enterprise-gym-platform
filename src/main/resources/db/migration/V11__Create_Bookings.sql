CREATE TABLE bookings (
    class_session_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Keys linking the user to the event
    CONSTRAINT fk_booking_class_session FOREIGN KEY (class_session_id) REFERENCES class_sessions(id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,

    -- Business Logic Constraints
    CONSTRAINT chk_booking_status CHECK (status IN ('CONFIRMED', 'WAITLISTED', 'CANCELED', 'ATTENDED')),

    -- The Flex: Composite Primary Key prevents duplicate reservations
    PRIMARY KEY (class_session_id, user_id)
);

-- Index for trainers who want to pull up the class roster instantly
CREATE INDEX idx_bookings_class_session_id ON bookings(class_session_id);
