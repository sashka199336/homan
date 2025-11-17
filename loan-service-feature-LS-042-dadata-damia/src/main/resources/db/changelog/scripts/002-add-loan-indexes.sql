--liquibase formatted sql

--changeset artem.severin:2
CREATE INDEX idx_loan_applications_external_id ON loan_applications(external_application_id);
CREATE INDEX idx_loan_applications_passport ON loan_applications(passport_number);