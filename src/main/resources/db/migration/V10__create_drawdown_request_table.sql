-- Create drawdown request table
CREATE TABLE drawdown_request (
    id BIGSERIAL PRIMARY KEY,

    salesperson_id BIGINT NOT NULL,

    requested_amount DECIMAL(12, 2) NOT NULL,
    approved_amount DECIMAL(12, 2),

    status VARCHAR(20) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'PAID', 'REJECTED')),

    request_date DATE NOT NULL DEFAULT CURRENT_DATE,
    approved_date DATE,
    paid_date DATE,

    quarter_year INTEGER,
    quarter_number INTEGER,

    payment_method VARCHAR(50),
    reference_number VARCHAR(100),

    notes VARCHAR(500),
    rejection_reason VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    approved_by VARCHAR(100),
    paid_by VARCHAR(100),

    CONSTRAINT fk_drawdown_salesperson
        FOREIGN KEY (salesperson_id) REFERENCES salesperson(id) ON DELETE RESTRICT
);

CREATE INDEX idx_drawdown_salesperson ON drawdown_request(salesperson_id);
CREATE INDEX idx_drawdown_status ON drawdown_request(status);
CREATE INDEX idx_drawdown_quarter ON drawdown_request(quarter_year, quarter_number);
