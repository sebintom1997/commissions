-- Create placement table
CREATE TABLE placement (
    id BIGSERIAL PRIMARY KEY,

    -- Foreign keys to related entities
    salesperson_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    contractor_id BIGINT NOT NULL,

    -- Basic fields
    placement_type VARCHAR(20) NOT NULL CHECK (placement_type IN ('CONTRACTOR', 'PERMANENT')),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'TERMINATED')),

    -- Dates
    start_date DATE,
    end_date DATE,

    -- Contractor-specific fields
    hours_per_week DECIMAL(5, 2),
    weeks_per_year INTEGER,

    -- Pay basis
    pay_type VARCHAR(20) CHECK (pay_type IN ('HOURLY', 'SALARY')),
    annual_salary DECIMAL(12, 2),
    hourly_pay_rate DECIMAL(10, 2),

    -- Bill basis
    bill_rate DECIMAL(10, 2),
    margin_percentage DECIMAL(5, 2),

    -- Overhead percentages
    admin_percentage DECIMAL(5, 2),
    insurance_percentage DECIMAL(5, 2),
    fixed_costs DECIMAL(12, 2),

    -- Calculated fields (will be populated by service layer)
    hourly_pay_cost DECIMAL(10, 2),
    margin_per_hour DECIMAL(10, 2),
    weekly_margin DECIMAL(12, 2),
    gross_annual_margin DECIMAL(12, 2),
    net_annual_margin DECIMAL(12, 2),

    -- Commission fields
    sequence_number INTEGER,
    commission_percentage DECIMAL(5, 2),
    commission_total DECIMAL(12, 2),

    -- Permanent-specific fields
    placement_fee DECIMAL(12, 2),
    fee_type VARCHAR(20) CHECK (fee_type IN ('PERCENTAGE', 'FLAT')),
    candidate_salary DECIMAL(12, 2),
    recognition_period_months INTEGER,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_placement_salesperson FOREIGN KEY (salesperson_id) REFERENCES salesperson(id) ON DELETE RESTRICT,
    CONSTRAINT fk_placement_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE RESTRICT,
    CONSTRAINT fk_placement_contractor FOREIGN KEY (contractor_id) REFERENCES contractor(id) ON DELETE RESTRICT
);

-- Indexes for foreign keys
CREATE INDEX idx_placement_salesperson_id ON placement(salesperson_id);
CREATE INDEX idx_placement_client_id ON placement(client_id);
CREATE INDEX idx_placement_contractor_id ON placement(contractor_id);

-- Indexes for common query fields
CREATE INDEX idx_placement_status ON placement(status);
CREATE INDEX idx_placement_type ON placement(placement_type);
CREATE INDEX idx_placement_start_date ON placement(start_date DESC);
CREATE INDEX idx_placement_created_at ON placement(created_at DESC);

-- Composite indexes for common filter combinations
CREATE INDEX idx_placement_status_created_at ON placement(status, created_at DESC);
CREATE INDEX idx_placement_type_status ON placement(placement_type, status);
CREATE INDEX idx_placement_contractor_client ON placement(contractor_id, client_id);

-- Comment on table
COMMENT ON TABLE placement IS 'Stores placement records for both contractor and permanent placements';
COMMENT ON COLUMN placement.sequence_number IS 'Sequence number (1st, 2nd, 3rd) for this contractor at this client';
COMMENT ON COLUMN placement.commission_percentage IS 'Commission percentage based on sequence number from policy settings';
