ALTER TABLE patient ADD lower_sur_name varchar;
ALTER TABLE patient ADD lower_given_name varchar;

CREATE TABLE IF NOT EXISTS nid_mapping (
    national_id varchar,
    health_id varchar,
    PRIMARY KEY (national_id)
);

CREATE TABLE IF NOT EXISTS brn_mapping (
    bin_brn varchar,
    health_id varchar,
    PRIMARY KEY (bin_brn)
);

CREATE TABLE IF NOT EXISTS uid_mapping (
    uid varchar,
    health_id varchar,
    PRIMARY KEY (uid, health_id)
);


CREATE TABLE IF NOT EXISTS address_mapping (
    division_id varchar,
    district_id varchar,
    upazilla_id varchar,
    given_name varchar,
    surname varchar,
    creation_time timeuuid,
    health_id varchar,
    PRIMARY KEY ((division_id, district_id, upazilla_id), given_name, surname, creation_time)
);
