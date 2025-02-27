CREATE SEQUENCE IF NOT EXISTS activity_record_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE activity_record
(
    id                BIGINT NOT NULL,
    employee_name     VARCHAR(255),
    project           VARCHAR(255),
    date_of_proof     date,
    location          VARCHAR(255),
    work_package      VARCHAR(255),
    description       VARCHAR(255),
    workload          time WITHOUT TIME ZONE,
    customer_billable BOOLEAN,
    CONSTRAINT pk_activity_record PRIMARY KEY (id)
);