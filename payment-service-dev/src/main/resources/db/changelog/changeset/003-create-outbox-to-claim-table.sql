CREATE TABLE payment_schema.outbox_to_claim (
	id uuid NOT NULL,
	payload jsonb NOT NULL,
	CONSTRAINT outbox_to_claim_pkey PRIMARY KEY (id)
);