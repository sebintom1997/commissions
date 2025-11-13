-- Create PolicySettings table
-- Migration V4: Add PolicySettings entity (Singleton)

CREATE TABLE policy_settings (
    id BIGSERIAL PRIMARY KEY,
    admin_percentage DECIMAL(5,2) NOT NULL,
    insurance_percentage DECIMAL(5,2) NOT NULL,
    leave_percentage DECIMAL(5,2) NOT NULL,
    prsi_percentage DECIMAL(5,2) NOT NULL,
    pension_percentage DECIMAL(5,2) NOT NULL,
    pension_cap DECIMAL(12,2),
    weeks_per_year INTEGER NOT NULL,
    first_contract_commission DECIMAL(5,2) NOT NULL,
    second_contract_commission DECIMAL(5,2) NOT NULL,
    third_contract_commission DECIMAL(5,2) NOT NULL,
    drawdown_min_month INTEGER NOT NULL,
    drawdown_max_per_quarter INTEGER NOT NULL,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

-- Insert default settings (SINGLETON - only one record)
INSERT INTO policy_settings (
    admin_percentage,
    insurance_percentage,
    leave_percentage,
    prsi_percentage,
    pension_percentage,
    pension_cap,
    weeks_per_year,
    first_contract_commission,
    second_contract_commission,
    third_contract_commission,
    drawdown_min_month,
    drawdown_max_per_quarter,
    updated_by,
    created_at
) VALUES (
    6.00,    -- admin_percentage
    2.00,    -- insurance_percentage
    14.54,   -- leave_percentage
    11.25,   -- prsi_percentage
    1.50,    -- pension_percentage
    2000.00, -- pension_cap
    45,      -- weeks_per_year
    15.00,   -- first_contract_commission
    10.00,   -- second_contract_commission
    8.00,    -- third_contract_commission
    3,       -- drawdown_min_month (Month 3)
    1,       -- drawdown_max_per_quarter (1 per quarter)
    'SYSTEM',
    CURRENT_TIMESTAMP
);

-- Add comments for documentation
COMMENT ON TABLE policy_settings IS 'System-wide policy settings (SINGLETON - only one record)';
COMMENT ON COLUMN policy_settings.admin_percentage IS 'Administrative overhead percentage';
COMMENT ON COLUMN policy_settings.insurance_percentage IS 'Insurance overhead percentage';
COMMENT ON COLUMN policy_settings.leave_percentage IS 'Leave/holiday percentage';
COMMENT ON COLUMN policy_settings.prsi_percentage IS 'PRSI (social insurance) percentage';
COMMENT ON COLUMN policy_settings.pension_percentage IS 'Pension contribution percentage';
COMMENT ON COLUMN policy_settings.pension_cap IS 'Maximum pension contribution amount';
COMMENT ON COLUMN policy_settings.weeks_per_year IS 'Working weeks per year (typically 45 or 52)';
COMMENT ON COLUMN policy_settings.first_contract_commission IS 'Commission % for 1st contract (15%)';
COMMENT ON COLUMN policy_settings.second_contract_commission IS 'Commission % for 2nd contract (10%)';
COMMENT ON COLUMN policy_settings.third_contract_commission IS 'Commission % for 3rd+ contracts (8%)';
COMMENT ON COLUMN policy_settings.drawdown_min_month IS 'Minimum month before drawdown allowed (3)';
COMMENT ON COLUMN policy_settings.drawdown_max_per_quarter IS 'Maximum drawdowns per quarter (1)';
