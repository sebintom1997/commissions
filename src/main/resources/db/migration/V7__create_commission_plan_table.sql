-- Create commission plan table
CREATE TABLE commission_plan (
    id BIGSERIAL PRIMARY KEY,

    -- Foreign keys
    placement_id BIGINT NOT NULL,
    salesperson_id BIGINT NOT NULL,

    -- Financial amounts
    planned_amount DECIMAL(12, 2) NOT NULL,
    confirmed_amount DECIMAL(12, 2),
    recognized_amount DECIMAL(12, 2),
    paid_amount DECIMAL(12, 2),

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED'
        CHECK (status IN ('PLANNED', 'CONFIRMED', 'RECOGNIZED', 'PAID', 'REVERSED')),

    -- Recognition period
    recognition_start_date DATE,
    recognition_end_date DATE,
    months_to_recognize INTEGER,
    months_recognized INTEGER DEFAULT 0,

    -- Drawdown
    eligible_for_drawdown BOOLEAN,
    drawdown_month INTEGER,

    -- Notes
    notes VARCHAR(500),

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_commission_plan_placement
        FOREIGN KEY (placement_id) REFERENCES placement(id) ON DELETE CASCADE,
    CONSTRAINT fk_commission_plan_salesperson
        FOREIGN KEY (salesperson_id) REFERENCES salesperson(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_commission_plan_placement_id ON commission_plan(placement_id);
CREATE INDEX idx_commission_plan_salesperson_id ON commission_plan(salesperson_id);
CREATE INDEX idx_commission_plan_status ON commission_plan(status);
CREATE INDEX idx_commission_plan_salesperson_status
    ON commission_plan(salesperson_id, status);
CREATE INDEX idx_commission_plan_created_at ON commission_plan(created_at DESC);
CREATE INDEX idx_commission_plan_eligible_drawdown
    ON commission_plan(salesperson_id) WHERE eligible_for_drawdown = true;
