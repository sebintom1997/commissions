-- Create Client table
-- Migration V2: Add Client entity

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT chk_client_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Create indexes for performance
CREATE INDEX idx_client_name ON client(name);
CREATE INDEX idx_client_email ON client(email);
CREATE INDEX idx_client_status ON client(status);

-- Add comments for documentation
COMMENT ON TABLE client IS 'Stores client (company) information';
COMMENT ON COLUMN client.id IS 'Primary key, auto-incremented';
COMMENT ON COLUMN client.name IS 'Client company name';
COMMENT ON COLUMN client.contact_person IS 'Primary contact person at the client company';
COMMENT ON COLUMN client.email IS 'Client contact email';
COMMENT ON COLUMN client.phone IS 'Client contact phone number';
COMMENT ON COLUMN client.address IS 'Client company address';
COMMENT ON COLUMN client.status IS 'Status: ACTIVE or INACTIVE';
COMMENT ON COLUMN client.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN client.updated_at IS 'Timestamp when record was last updated';
