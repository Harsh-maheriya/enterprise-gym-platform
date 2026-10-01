CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Key enforcing referential integrity
    CONSTRAINT fk_invoice_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    
    -- Check constraint for domain integrity
    CONSTRAINT chk_invoice_status CHECK (status IN ('PENDING', 'PAID', 'VOIDED', 'OVERDUE')),
    CONSTRAINT chk_amount_positive CHECK (total_amount > 0)
);

-- Index for fast lookup when a user checks their billing history
CREATE INDEX idx_invoices_user_id ON invoices(user_id);
