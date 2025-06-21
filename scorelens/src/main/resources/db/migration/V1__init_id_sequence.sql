-- V1__init_id_sequence.sql

CREATE TABLE IF NOT EXISTS IDSequence (
    rolePrefix VARCHAR(2) PRIMARY KEY,
    lastNumber BIGINT NOT NULL
    );

INSERT INTO IDSequence (rolePrefix, lastNumber) VALUES
                                                    ('S', 0),
                                                    ('M', 0),
                                                    ('A', 0)
    ON DUPLICATE KEY UPDATE lastNumber = lastNumber;
