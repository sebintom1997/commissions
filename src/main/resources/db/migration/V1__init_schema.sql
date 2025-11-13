-- Initial schema for Commission Management System
-- Created: 2025-01-11

-- Create salesperson table
CREATE TABLE salesperson (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT chk_salesperson_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Create indexes for performance
CREATE INDEX idx_salesperson_email ON salesperson(email);
CREATE INDEX idx_salesperson_status ON salesperson(status);
CREATE INDEX idx_salesperson_name ON salesperson(name);

-- Add comments for documentation
COMMENT ON TABLE salesperson IS 'Stores salesperson information';
COMMENT ON COLUMN salesperson.id IS 'Primary key, auto-incremented';
COMMENT ON COLUMN salesperson.name IS 'Salesperson full name';
COMMENT ON COLUMN salesperson.email IS 'Unique email address';
COMMENT ON COLUMN salesperson.status IS 'Status: ACTIVE or INACTIVE';
COMMENT ON COLUMN salesperson.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN salesperson.updated_at IS 'Timestamp when record was last updated';
