    -- changeset artem.severin:2
    CREATE TABLE finance_service.outbox_event (
        id UUID PRIMARY KEY NOT NULL,
        aggregate_type TEXT NOT NULL,
        aggregate_id TEXT NOT NULL,
        type TEXT NOT NULL,
        payload TEXT NOT NULL,
        created_at TIMESTAMP NOT NULL,
        status TEXT NOT NULL,
        last_attempt_at TIMESTAMP,
        retries INT NOT NULL DEFAULT 0
    );

    CREATE INDEX idx_outbox_event_created_at ON finance_service.outbox_event(created_at);
    CREATE INDEX idx_outbox_event_aggregate_type ON finance_service.outbox_event(aggregate_type);
    CREATE INDEX idx_outbox_event_type ON finance_service.outbox_event(type);