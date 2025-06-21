-- V2__alter_role_prefix_length.sql

ALTER TABLE idsequence
    MODIFY role_prefix VARCHAR(2);