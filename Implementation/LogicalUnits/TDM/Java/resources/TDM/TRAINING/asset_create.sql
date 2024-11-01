CREATE TABLE IF NOT EXISTS public.Asset_Type(
    Asset_Type_ID text NOT NULL,
    Type text NOT NULL,
    EngineSize text ,
    Max_Speed text ,
    Brand text ,
    Model text ,
    Price text ,
	CONSTRAINT Asset_Type_pkey PRIMARY KEY (Asset_Type_ID)
);
ALTER TABLE public.Asset_Type OWNER TO "ASSET_USER";

CREATE TABLE IF NOT EXISTS public.Asset(
    Asset_ID text NOT NULL,
    Asset_Type text,
    Asset_IMEI text,
    Asset_Description text,
    Asset_Status text,
    Asset_Registration text,
	CONSTRAINT Asset_pkey PRIMARY KEY (Asset_ID),
	CONSTRAINT Asset_fk FOREIGN KEY (Asset_Type) REFERENCES public.Asset_Type (Asset_Type_ID)
);
ALTER TABLE public.Asset OWNER TO "ASSET_USER";

CREATE TABLE IF NOT EXISTS public.Asset_Transaction (
    Transaction_ID text NOT NULL,
    Transaction_Start timestamp with time zone,
    StartLAT text,
    StartLONG text,
    Transaction_End timestamp with time zone,
    EndLAT text,
    EndLONG text,
    Asset_ID text NOT NULL,
    Subscriber_ID text,
    Duration text, 
    TransactionCITY TEXT, 
    TransactionAddress TEXT,
	CONSTRAINT Asset_Transaction_pkey PRIMARY KEY (Transaction_ID),
	CONSTRAINT Asset_Transaction_fk FOREIGN KEY (Asset_ID) REFERENCES public.Asset (Asset_ID)
);
ALTER TABLE public.Asset_Transaction OWNER TO "ASSET_USER";

CREATE TABLE IF NOT EXISTS public.Asset_Sampling (
    Asset_Location_ID text NOT NULL,
    Transaction_ID text ,
    Asset_ID text NOT NULL,
    Asset_Location_GeoCoded text ,
    Asset_Telemetry text ,
    Asset_Location_TimeStamp text,
	CONSTRAINT Asset_Sampling_pkey PRIMARY KEY (Asset_Location_ID),
	CONSTRAINT Asset_Sampling_fk1 FOREIGN KEY (Asset_ID) REFERENCES public.Asset(Asset_ID) ,
	CONSTRAINT Asset_Sampling_fk2 FOREIGN KEY (Transaction_ID) REFERENCES public.Asset_Transaction(Transaction_ID)

);
ALTER TABLE public.Asset_Sampling OWNER TO "ASSET_USER";

CREATE TABLE IF NOT EXISTS public.USCities_GEOPoints(
   	City text NOT NULL,
	CityPointLAT text NOT NULL,
	CityPointLONG text NOT NULL
);
ALTER TABLE public.USCities_GEOPoints OWNER TO "ASSET_USER";

CREATE TABLE IF NOT EXISTS public.Asset_Stations(
	Station_Name TEXT,
	Station_City TEXT,
	Station_GeoPos TEXT,
	Station_Availability TEXT,
	Station_Capacity TEXT,
	Event_Type TEXT,
	Customer_ID bigint,
    Station_ID bigint
);
ALTER TABLE public.Asset_Stations OWNER TO "ASSET_USER";