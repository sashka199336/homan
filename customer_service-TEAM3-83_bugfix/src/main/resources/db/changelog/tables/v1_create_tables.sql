SET search_path TO customer_service;

CREATE TABLE IF NOT EXISTS customers
(
    id            UUID PRIMARY KEY                    NOT NULL,
    inn           VARCHAR(255),
    customer_type VARCHAR(50),
    status_type   VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS addresses
(
    id               UUID PRIMARY KEY                    NOT NULL,
    country          VARCHAR(255),
    city             VARCHAR(255),
    street           VARCHAR(255),
    house_number     VARCHAR(255),
    apartment_number VARCHAR(255),
    postal_code      VARCHAR(255),
    address_type     VARCHAR(50),
    customer_id      UUID,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP,
    CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE IF NOT EXISTS bank_accounts
(
    id                    UUID PRIMARY KEY                    NOT NULL,
    settlement_account    VARCHAR(255),
    correspondent_account VARCHAR(255),
    bic                   VARCHAR(255),
    customer_id           UUID,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP,
    CONSTRAINT fk_bank_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE IF NOT EXISTS contacts
(
    id           UUID PRIMARY KEY                    NOT NULL,
    phone_number VARCHAR(255),
    email        VARCHAR(255),
    channel      VARCHAR(50),
    customer_id  UUID,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT fk_contacts_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE IF NOT EXISTS customer_passport
(
    id            UUID PRIMARY KEY                    NOT NULL,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    patronymic    VARCHAR(255),
    date_of_Birth DATE,
    series        VARCHAR(255),
    number        VARCHAR(255),
    issue_date    DATE,
    issued_by     VARCHAR(255),
    issue_code    VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documents_types
(
    id            UUID PRIMARY KEY                    NOT NULL,
    document_date DATE,
    details       JSONB,
    document_scan BYTEA,
    customer_id   UUID,
    passport_id   UUID,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_documents_types_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_documents_types_passport FOREIGN KEY (passport_id) REFERENCES customer_passport (id)
);

CREATE TABLE IF NOT EXISTS individual_entrepreneurs
(
    id                UUID PRIMARY KEY                    NOT NULL,
    entrepreneur_name VARCHAR(255),
    ogrnip            VARCHAR(255),
    customer_id       UUID,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP,
    CONSTRAINT fk_individual_entrepreneurs_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE IF NOT EXISTS legal_entities
(
    id           UUID PRIMARY KEY                    NOT NULL,
    company_name VARCHAR(255),
    ogrn         VARCHAR(255),
    kpp          VARCHAR(255),
    position     VARCHAR(255),
    customer_id  UUID,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT fk_legal_entities_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);
