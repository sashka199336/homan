--liquibase formatted sql

--changeset artem.severin:1
CREATE TABLE loan_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_application_id VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    passport_number VARCHAR(50),
    inn_or_ogrn VARCHAR(20),
    birth_date DATE,
    company_name VARCHAR(255),
    status VARCHAR(50),
    dadata_passport_valid BOOLEAN DEFAULT false,
    dadata_company_valid BOOLEAN DEFAULT false,
    damia_risk_valid BOOLEAN DEFAULT false,
    damia_finance_valid BOOLEAN DEFAULT false,
    created_at DATE DEFAULT CURRENT_DATE
);
