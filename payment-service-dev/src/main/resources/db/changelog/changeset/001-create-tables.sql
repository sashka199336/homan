CREATE TABLE payment_schema."transaction_history" (
	amount numeric(38, 2) NOT NULL,
	create_date_time timestamptz(6) NULL,
	update_date_time timestamptz(6) NULL,
	claim_id uuid NULL,
	transaction_id uuid NOT NULL,
	status varchar(15) NULL,
	to_bill varchar(20) NOT NULL,
	CONSTRAINT transaction_pkey PRIMARY KEY (transaction_id),
	CONSTRAINT transaction_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'DONE'::character varying, 'REJECTED'::character varying])::text[])))
);
