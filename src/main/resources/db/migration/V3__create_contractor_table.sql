-- Create Contractor table
-- Migration V3: Add Contractor entity

CREATE TABLE contractor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT chk_contractor_type CHECK (type IN ('CONTRACTOR', 'PERMANENT')),
    CONSTRAINT chk_contractor_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Create indexes for performance
CREATE INDEX idx_contractor_email ON contractor(email);
CREATE INDEX idx_contractor_name ON contractor(name);
CREATE INDEX idx_contractor_type ON contractor(type);
CREATE INDEX idx_contractor_status ON contractor(status);

-- Add comments for documentation
COMMENT ON TABLE contractor IS 'Stores contractor (worker) information';
COMMENT ON COLUMN contractor.id IS 'Primary key, auto-incremented';
COMMENT ON COLUMN contractor.name IS 'Contractor full name';
COMMENT ON COLUMN contractor.email IS 'Contractor email address (unique)';
COMMENT ON COLUMN contractor.phone IS 'Contractor phone number';
COMMENT ON COLUMN contractor.type IS 'Type: CONTRACTOR (temporary) or PERMANENT';
COMMENT ON COLUMN contractor.status IS 'Status: ACTIVE or INACTIVE';
COMMENT ON COLUMN contractor.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN contractor.updated_at IS 'Timestamp when record was last updated';
