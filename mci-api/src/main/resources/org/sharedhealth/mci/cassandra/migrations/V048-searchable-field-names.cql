ALTER TABLE patient ADD lower_sur_name varchar;
ALTER TABLE patient ADD lower_given_name varchar;

CREATE TABLE IF NOT EXISTS nid_mapping (
    national_id varchar,
    health_id varchar,
    PRIMARY KEY (national_id, health_id)
);

CREATE TABLE IF NOT EXISTS brn_mapping (
    bin_brn varchar,
    health_id varchar,
    PRIMARY KEY (bin_brn, health_id)
);

CREATE TABLE IF NOT EXISTS uid_mapping (
    uid varchar,
    health_id varchar,
    PRIMARY KEY (uid, health_id)
);

CREATE TABLE IF NOT EXISTS phone_number_mapping (
    phone_number varchar,
    area_code varchar,
    health_id varchar,
    PRIMARY KEY (phone_number, health_id)
);

CREATE TABLE IF NOT EXISTS name_mapping (
    division_id varchar,
    district_id varchar,
    upazila_id varchar,
    city_corp_id varchar,
    ward_id varchar,
    rural_ward_id varchar,
    given_name varchar,
    surname varchar,
    health_id varchar,
    PRIMARY KEY ((division_id, district_id, upazila_id), given_name, surname, health_id)
);
