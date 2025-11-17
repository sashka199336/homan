CREATE TABLE IF NOT EXISTS claim (
    claim_id UUID PRIMARY KEY,
    claim_type VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    customer_id UUID NOT NULL,
    credit_term_month INT NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    claim_status VARCHAR(20),
    to_bill VARCHAR(20)
);

ALTER TABLE claim ADD CONSTRAINT ck_claim_type CHECK (claim_type IN ('CREDIT', 'PAYMENT', 'ACCOUNT'));
ALTER TABLE claim ADD CONSTRAINT ck_claim_status CHECK (claim_status IN ('PENDING', 'REJECTED', 'ACCEPTED', 'CONFIRMED', 'DONE'));

CREATE INDEX idx_claim_customer_id ON claim(customer_id);
CREATE INDEX idx_claim_status ON claim(claim_status);