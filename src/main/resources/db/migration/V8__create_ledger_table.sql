-- Create ledger table for transaction tracking
CREATE TABLE ledger (
    id BIGSERIAL PRIMARY KEY,

    -- Foreign keys
    commission_plan_id BIGINT,
    salesperson_id BIGINT NOT NULL,
    placement_id BIGINT,

    -- Entry details
    entry_type VARCHAR(30) NOT NULL
        CHECK (entry_type IN ('COMMISSION_ACCRUED', 'COMMISSION_ADJUSTED', 'COMMISSION_RECOGNIZED',
                              'COMMISSION_PAID', 'DRAWDOWN_REQUESTED', 'DRAWDOWN_APPROVED',
                              'DRAWDOWN_REJECTED', 'REVERSAL', 'ADJUSTMENT')),

    amount DECIMAL(12, 2) NOT NULL,
    description VARCHAR(500),

    -- Reference info
    reference_type VARCHAR(50),
    reference_id BIGINT,

    -- Status
    status VARCHAR(20),

    -- Notes
    notes VARCHAR(500),

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT fk_ledger_commission_plan
        FOREIGN KEY (commission_plan_id) REFERENCES commission_plan(id) ON DELETE SET NULL,
    CONSTRAINT fk_ledger_salesperson
        FOREIGN KEY (salesperson_id) REFERENCES salesperson(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ledger_placement
        FOREIGN KEY (placement_id) REFERENCES placement(id) ON DELETE SET NULL
);

-- Indexes for query performance
CREATE INDEX idx_ledger_salesperson_id ON ledger(salesperson_id);
CREATE INDEX idx_ledger_commission_plan_id ON ledger(commission_plan_id);
CREATE INDEX idx_ledger_placement_id ON ledger(placement_id);
CREATE INDEX idx_ledger_entry_type ON ledger(entry_type);
CREATE INDEX idx_ledger_created_at ON ledger(created_at DESC);
CREATE INDEX idx_ledger_salesperson_type ON ledger(salesperson_id, entry_type);
CREATE INDEX idx_ledger_salesperson_created ON ledger(salesperson_id, created_at DESC);
