CREATE TABLE payment_schema.outbox_phs (
	id uuid NOT NULL,
	payload jsonb NOT NULL,
	CONSTRAINT outbox_phs_pkey PRIMARY KEY (id)
);