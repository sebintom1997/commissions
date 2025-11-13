-- Add additional indexes for performance optimization
-- Migration V5: Performance indexes

-- Additional indexes for Salesperson table (some already exist from V1)
-- Index on name for searching
CREATE INDEX IF NOT EXISTS idx_salesperson_name_lower ON salesperson(LOWER(name));

-- Composite index for filtering by status and sorting by created_at
CREATE INDEX IF NOT EXISTS idx_salesperson_status_created ON salesperson(status, created_at DESC);

-- Additional indexes for Client table (some already exist from V2)
-- Index on name for searching (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_client_name_lower ON client(LOWER(name));

-- Composite index for filtering by status and sorting
CREATE INDEX IF NOT EXISTS idx_client_status_created ON client(status, created_at DESC);

-- Index on contact_person for searches
CREATE INDEX IF NOT EXISTS idx_client_contact_person ON client(contact_person);

-- Additional indexes for Contractor table (some already exist from V3)
-- Index on name for searching (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_contractor_name_lower ON contractor(LOWER(name));

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_contractor_type_status ON contractor(type, status);
CREATE INDEX IF NOT EXISTS idx_contractor_status_created ON contractor(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_contractor_type_created ON contractor(type, created_at DESC);

-- Comments
COMMENT ON INDEX idx_salesperson_name_lower IS 'Case-insensitive search on salesperson name';
COMMENT ON INDEX idx_salesperson_status_created IS 'Filter by status with sorting by created date';
COMMENT ON INDEX idx_client_name_lower IS 'Case-insensitive search on client name';
COMMENT ON INDEX idx_client_status_created IS 'Filter by status with sorting by created date';
COMMENT ON INDEX idx_contractor_name_lower IS 'Case-insensitive search on contractor name';
COMMENT ON INDEX idx_contractor_type_status IS 'Filter by type and status together';
COMMENT ON INDEX idx_contractor_status_created IS 'Filter by status with sorting by created date';
COMMENT ON INDEX idx_contractor_type_created IS 'Filter by type with sorting by created date';
