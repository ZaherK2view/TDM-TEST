CREATE TABLE IF NOT EXISTS public.case_activity
(
    activity_text text,
    case_type text,
    note_text text 
);

CREATE TABLE IF NOT EXISTS public.contract_ref_id 
(
	contract_ref_id  text,
	contract_description text	
);


CREATE TABLE IF NOT EXISTS public.SAMPLE_JOB_TITLE
(
	job_title text
);


CREATE TABLE IF NOT EXISTS public.SAMPLE_POSTAL_CODES
(
	zip_code text, 
	city text,
	state text
);

CREATE TABLE IF NOT EXISTS public.sample_street_name
(
	STREET_NAME text
 );