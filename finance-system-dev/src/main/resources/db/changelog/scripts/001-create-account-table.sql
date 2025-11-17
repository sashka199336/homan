-- changeset artem.severin:1
CREATE TABLE finance_service.account (
    id UUID PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    client_id TEXT NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);