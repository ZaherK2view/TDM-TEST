CREATE TABLE IF NOT EXISTS public.subscriber (
    subscriber_id bigint NOT NULL,
    subscriber_type character varying(200) DEFAULT NULL::character varying,
    vip_status character varying(200) DEFAULT NULL::character varying,
    first_name character varying(200),
    last_name character varying(200),
	CONSTRAINT subscriber_pkey PRIMARY KEY (subscriber_id)
);
ALTER TABLE public.subscriber OWNER TO "TAR_BILLING_USER";

CREATE TABLE IF NOT EXISTS public.balance (
    subscriber_id bigint,
    balance_id bigint NOT NULL,
    balance_ref_id bigint,
    available_amount bigint,
    reset_amount bigint,
    reset_date timestamp without time zone,
	CONSTRAINT balance_pkey PRIMARY KEY (balance_id),
	CONSTRAINT balance_fk FOREIGN KEY (subscriber_id) REFERENCES public.subscriber(subscriber_id)
);
ALTER TABLE public.balance OWNER TO "TAR_BILLING_USER";

CREATE TABLE IF NOT EXISTS public.invoice (
    subscriber_id bigint,
    invoice_id bigint NOT NULL,
    issued_date timestamp without time zone,
    due_date timestamp without time zone,
    status character varying(2000) DEFAULT NULL::character varying,
    balance bigint,
    invoice_image bytea,
	CONSTRAINT invoice_pkey PRIMARY KEY (invoice_id),
	CONSTRAINT invoice_fk FOREIGN KEY (subscriber_id) REFERENCES public.subscriber(subscriber_id) 
);
ALTER TABLE public.invoice OWNER TO "TAR_BILLING_USER";

CREATE TABLE IF NOT EXISTS public.payment (
    invoice_id bigint,
    payment_id bigint NOT NULL,
    issued_date timestamp without time zone,
    status character varying(200) DEFAULT NULL::character varying,
    amount character varying(200) DEFAULT NULL::character varying,
	CONSTRAINT payment_pkey PRIMARY KEY (payment_id),
	CONSTRAINT payment_fk FOREIGN KEY (invoice_id) REFERENCES public.invoice(invoice_id)
);
ALTER TABLE public.payment OWNER TO "TAR_BILLING_USER";