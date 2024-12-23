-- BE
INSERT INTO public.business_entities (be_name, be_description, be_id, be_created_by, be_creation_date, be_last_updated_date, be_last_updated_by, be_status) VALUES ('Customer', 'This is a Business Entity created for the TDM GUI for free trail.', 1, 'admin', '2022-12-01 13:27:20.522', '2022-12-01 13:38:25.987', 'admin', 'Active')ON CONFLICT DO NOTHING;

-- product_logical_units
INSERT INTO public.product_logical_units (lu_name, lu_description, be_id, lu_parent_id, lu_id, product_name, lu_parent_name, product_id) VALUES ('CRM', 'This is the Parent LU.', 1, NULL, 1, 'CRM', NULL, 1) ON CONFLICT DO NOTHING;
INSERT INTO public.product_logical_units (lu_name, lu_description, be_id, lu_parent_id, lu_id, product_name, lu_parent_name, product_id) VALUES ('Billing', 'This is CRM Child LU.', 1, 1, 2, 'BILLING', 'CRM', 2) ON CONFLICT DO NOTHING;
INSERT INTO public.product_logical_units (lu_name, lu_description, be_id, lu_parent_id, lu_id, product_name, lu_parent_name, product_id) VALUES ('Asset', 'This is a CRM Child LU.', 1, 1, 3, 'ASSET', 'CRM', 3) ON CONFLICT DO NOTHING;

-- Post exe processes- BE
INSERT INTO public.tdm_be_post_exe_process (process_id, process_name, process_description, be_id, execution_order) VALUES (1, 'postTaskExePrintToLog', 'This is a Post Execution Process Flow.', 1, 1)ON CONFLICT DO NOTHING;

-- Products (systems)
INSERT INTO public.products (product_name, product_description,  product_versions, product_id, product_created_by, product_creation_date, product_last_updated_date, product_last_updated_by, product_status) VALUES ('CRM', 'CRM Application', '1', 1, 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.products (product_name, product_description,  product_versions, product_id, product_created_by, product_creation_date, product_last_updated_date, product_last_updated_by, product_status) VALUES ('BILLING', 'BILLING Application', 'DEV, PROD', 2, 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.products (product_name, product_description,  product_versions, product_id, product_created_by, product_creation_date, product_last_updated_date, product_last_updated_by, product_status) VALUES ('ASSET', 'ASSET Application', '1', 3, 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;

-- environments
INSERT INTO public.environments (environment_name, environment_description, environment_expiration_date, environment_point_of_contact_first_name, environment_point_of_contact_last_name, environment_point_of_contact_phone1, environment_point_of_contact_phone2, environment_point_of_contact_email, environment_id, environment_created_by, environment_creation_date, environment_last_updated_date, environment_last_updated_by, environment_status, allow_write, allow_read, sync_mode) VALUES ('Production', 'This is the Source environment.', NULL, NULL, NULL, NULL, NULL, NULL, 1, 'admin', '2022-12-01 13:40:14.578', '2022-12-01 13:41:58.95', 'admin', 'Active', false, true, 'ON')ON CONFLICT DO NOTHING;
INSERT INTO public.environments (environment_name, environment_description, environment_expiration_date, environment_point_of_contact_first_name, environment_point_of_contact_last_name, environment_point_of_contact_phone1, environment_point_of_contact_phone2, environment_point_of_contact_email, environment_id, environment_created_by, environment_creation_date, environment_last_updated_date, environment_last_updated_by, environment_status, allow_write, allow_read, sync_mode) VALUES ('UAT1', 'This is a Target environment.', NULL, NULL, NULL, NULL, NULL, NULL, 2, 'admin', '2022-12-01 13:42:33.739', '2022-12-01 13:43:23.54', 'admin', 'Active', true, true, 'ON')ON CONFLICT DO NOTHING;
INSERT INTO public.environments (environment_name, environment_description, environment_expiration_date, environment_point_of_contact_first_name, environment_point_of_contact_last_name, environment_point_of_contact_phone1, environment_point_of_contact_phone2, environment_point_of_contact_email, environment_id, environment_created_by, environment_creation_date, environment_last_updated_date, environment_last_updated_by, environment_status, allow_write, allow_read, sync_mode) VALUES ('UAT2', 'This is a Target environment.', NULL, NULL, NULL, NULL, NULL, NULL, 3, 'admin', '2022-12-01 13:42:33.739', '2022-12-01 13:43:23.54', 'admin', 'Active', true, true, 'ON')ON CONFLICT DO NOTHING;
INSERT INTO public.environments (environment_name, environment_description, environment_expiration_date, environment_point_of_contact_first_name, environment_point_of_contact_last_name, environment_point_of_contact_phone1, environment_point_of_contact_phone2, environment_point_of_contact_email, environment_id, environment_created_by, environment_creation_date, environment_last_updated_date, environment_last_updated_by, environment_status, allow_write, allow_read, sync_mode) VALUES ('Synthetic', 'This is the Synthetic environment.', NULL, NULL, NULL, NULL, NULL, NULL, 11, 'admin', '2022-12-01 13:40:14.578', '2022-12-01 13:41:58.95', 'admin', 'Active', false, true, 'OFF')ON CONFLICT DO NOTHING;

-- Source Env
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (1, 1, 1, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (2, 1, 2, 'PROD', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (3, 1, 3, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;

-- Synthetic Env
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (-1, -1, 1, 'Synthetic', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (-2, -1, 2, 'Synthetic', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (-3, -1, 3, 'Synthetic', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;

-- Target Env
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (4, 2, 1, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (5, 2, 2, 'DEV', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (6, 2, 3, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;

-- Target Env
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (7, 3, 1, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (8, 3, 2, 'DEV', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_products (environment_product_id, environment_id, product_id, product_version, created_by, creation_date, last_updated_date, last_updated_by, status) VALUES (9, 3, 3, '1', 'admin', now(), now(), 'admin', 'Active')ON CONFLICT DO NOTHING;

-- environment_role_users
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (1, 1, 'ID', 'ALL', '-1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (1, 1, 'GROUP', 'Testersgrp1', 'Testersgrp1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (2, 2, 'ID', 'ALL', '-1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (2, 2, 'GROUP', 'Testersgrp1', 'Testersgrp1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (3, 3, 'ID', 'ALL', '-1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (3, 3, 'GROUP', 'Testersgrp1', 'Testersgrp1')ON CONFLICT DO NOTHING;
INSERT INTO public.environment_role_users (environment_id, role_id, user_type, username, user_id) VALUES (-1, -1, 'ID', 'ALL', '-1')ON CONFLICT DO NOTHING;

-- environment_roles
INSERT INTO public.environment_roles (environment_id, role_name, role_description, allowed_delete_before_load, allowed_creation_of_synthetic_data, allowed_random_entity_selection, allowed_request_of_fresh_data, allowed_task_scheduling, allowed_number_of_entities_to_copy, role_id, role_created_by, role_creation_date, role_last_updated_date, role_expiration_date, role_last_updated_by, role_status, allowed_refresh_reference_data, allowed_replace_sequences, allow_read, allow_write, allowed_number_of_entities_to_read, allowed_entity_versioning, allowed_test_conn_failure, allowed_number_of_reserved_entities) VALUES (1, 'set1', '', false, false, false, true, true, 0, 1, 'admin', '2022-12-01 13:41:54.008', '2022-12-01 13:41:54.008', NULL, 'admin', 'Active', true, false, true, false, 150, true, true, 0)ON CONFLICT DO NOTHING;
INSERT INTO public.environment_roles (environment_id, role_name, role_description, allowed_delete_before_load, allowed_creation_of_synthetic_data, allowed_random_entity_selection, allowed_request_of_fresh_data, allowed_task_scheduling, allowed_number_of_entities_to_copy, role_id, role_created_by, role_creation_date, role_last_updated_date, role_expiration_date, role_last_updated_by, role_status, allowed_refresh_reference_data, allowed_replace_sequences, allow_read, allow_write, allowed_number_of_entities_to_read, allowed_entity_versioning, allowed_test_conn_failure, allowed_number_of_reserved_entities) VALUES (2, 'set2', '', true, true, true, true, true, 1000, 2, 'admin', '2022-12-01 13:43:19.183', '2022-12-01 13:43:19.183', NULL, 'admin', 'Active', true, true, true, true, 5, true, true, 1000)ON CONFLICT DO NOTHING;
INSERT INTO public.environment_roles (environment_id, role_name, role_description, allowed_delete_before_load, allowed_creation_of_synthetic_data, allowed_random_entity_selection, allowed_request_of_fresh_data, allowed_task_scheduling, allowed_number_of_entities_to_copy, role_id, role_created_by, role_creation_date, role_last_updated_date, role_expiration_date, role_last_updated_by, role_status, allowed_refresh_reference_data, allowed_replace_sequences, allow_read, allow_write, allowed_number_of_entities_to_read, allowed_entity_versioning, allowed_test_conn_failure, allowed_number_of_reserved_entities) VALUES (3, 'set2', '', true, true, true, true, true, 1000, 3, 'admin', '2022-12-01 13:43:19.183', '2022-12-01 13:43:19.183', NULL, 'admin', 'Active', true, true, true, true, 5, true, true, 1000)ON CONFLICT DO NOTHING;
INSERT INTO public.environment_roles (environment_id, role_name, role_description, allowed_delete_before_load, allowed_creation_of_synthetic_data, allowed_random_entity_selection, allowed_request_of_fresh_data, allowed_task_scheduling, allowed_number_of_entities_to_copy, role_id, role_created_by, role_creation_date, role_last_updated_date, role_expiration_date, role_last_updated_by, role_status, allowed_refresh_reference_data, allowed_replace_sequences, allow_read, allow_write, allowed_number_of_entities_to_read, allowed_entity_versioning, allowed_test_conn_failure, allowed_number_of_reserved_entities) VALUES (-1, 'set2', '', false, false, false, true, true, 0, -1, 'admin', '2022-12-01 13:43:19.183', '2022-12-01 13:43:19.183', NULL, 'admin', 'Active', true, true, true, true, 5, true, true, 0)ON CONFLICT DO NOTHING;


-- permission group mapping
INSERT INTO public.permission_groups_mapping (description, fabric_role, permission_group, created_by, updated_by, creation_date, update_date) VALUES ('Maps Tester Permission Group to Testersgrp1 Fabric Role.', 'Testersgrp1', 'tester', 'admin', 'admin', '2022-12-01 13:25:30.980407', '2022-12-01 13:25:30.980407')ON CONFLICT DO NOTHING;

SELECT pg_catalog.setval('public.business_entities_be_id_seq', 10, true);
SELECT pg_catalog.setval('public.environment_product_id_seq', 10, true);
SELECT pg_catalog.setval('public.environment_roles_role_id_seq', 10, true);
SELECT pg_catalog.setval('public.environments_environment_id_seq', 10, true);
SELECT pg_catalog.setval('public.post_exe_process_id_seq', 10, true);
SELECT pg_catalog.setval('public.product_logical_units_lu_id_seq', 10, true);
SELECT pg_catalog.setval('public.products_product_id_seq', 10, true);
SELECT pg_catalog.setval('public.tasks_ref_table_id_seq', 10, false);
SELECT pg_catalog.setval('public.tasks_task_execution_id_seq', 1, false);
SELECT pg_catalog.setval('public.tasks_task_id_seq', 1, false);
SELECT pg_catalog.setval('public.tdm_be_env_exclusion_list_be_env_exclusion_list_id_seq', 10, false);