CREATE TABLE IF NOT EXISTS lead_request (
    id BIGSERIAL PRIMARY KEY,
    lead_id VARCHAR(100) UNIQUE NOT NULL,
    request_payload JSONB,
    status VARCHAR(50),
    retry_count INT DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lead_audit (
    id BIGSERIAL PRIMARY KEY,
    lead_id VARCHAR(100),
    event_name VARCHAR(100),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_lead_request_lead_id ON lead_request(lead_id);
CREATE INDEX IF NOT EXISTS idx_lead_request_status ON lead_request(status);
CREATE INDEX IF NOT EXISTS idx_lead_audit_lead_id ON lead_audit(lead_id);
