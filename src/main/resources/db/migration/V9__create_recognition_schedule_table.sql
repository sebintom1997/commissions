-- Create recognition schedule table
CREATE TABLE recognition_schedule (
    id BIGSERIAL PRIMARY KEY,

    commission_plan_id BIGINT NOT NULL,

    month INTEGER NOT NULL,
    recognition_date DATE NOT NULL,

    planned_amount DECIMAL(12, 2) NOT NULL,
    recognized_amount DECIMAL(12, 2),

    status VARCHAR(20) DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recognition_schedule_plan
        FOREIGN KEY (commission_plan_id) REFERENCES commission_plan(id) ON DELETE CASCADE
);

CREATE INDEX idx_recognition_schedule_plan ON recognition_schedule(commission_plan_id);
CREATE INDEX idx_recognition_schedule_status ON recognition_schedule(status);
CREATE INDEX idx_recognition_schedule_date ON recognition_schedule(recognition_date);
