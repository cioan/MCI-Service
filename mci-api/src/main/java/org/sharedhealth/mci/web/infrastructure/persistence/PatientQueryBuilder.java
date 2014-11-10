package org.sharedhealth.mci.web.infrastructure.persistence;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.Select.Where;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PatientQueryBuilder {

    public static final String HEALTH_ID = "health_id";
    public static final String NATIONAL_ID = "national_id";
    public static final String FULL_NAME_BANGLA = "full_name_bangla";
    public static final String GIVEN_NAME = "given_name";
    public static final String SUR_NAME = "sur_name";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String GENDER = "gender";
    public static final String OCCUPATION = "occupation";
    public static final String EDU_LEVEL = "edu_level";
    public static final String ADDRESS_LINE = "address_line";
    public static final String DIVISION_ID = "division_id";
    public static final String DISTRICT_ID = "district_id";
    public static final String UPAZILLA_ID = "upazilla_id";
    public static final String UNION_ID = "union_id";
    public static final String BIN_BRN = "bin_brn";
    public static final String UID = "uid";
    public static final String FATHERS_NAME_BANGLA = "fathers_name_bangla";
    public static final String FATHERS_GIVEN_NAME = "fathers_given_name";
    public static final String FATHERS_SUR_NAME = "fathers_sur_name";
    public static final String FATHERS_UID = "fathers_uid";
    public static final String FATHERS_NID = "fathers_nid";
    public static final String FATHERS_BRN = "fathers_brn";
    public static final String MOTHERS_NAME_BANGLA = "mothers_name_bangla";
    public static final String MOTHERS_GIVEN_NAME = "mothers_given_name";
    public static final String MOTHERS_SUR_NAME = "mothers_sur_name";
    public static final String MOTHERS_UID = "mothers_uid";
    public static final String MOTHERS_NID = "mothers_nid";
    public static final String MOTHERS_BRN = "mothers_brn";
    public static final String PLACE_OF_BIRTH = "place_of_birth";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String MARRIAGE_ID = "marriage_id";
    public static final String SPOUSE_NAME_BANGLA = "spouse_name_bangla";
    public static final String SPOUSE_NAME = "spouse_name";
    public static final String SPOUSE_UID_NID = "spouse_uid_nid";
    public static final String RELIGION = "religion";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String NATIONALITY = "nationality";
    public static final String DISABILITY = "disability";
    public static final String ETHNICITY = "ethnicity";
    public static final String HOLDING_NUMBER = "holding_number";
    public static final String STREET = "street";
    public static final String AREA_MOUJA = "area_mouja";
    public static final String VILLAGE = "village";
    public static final String POST_OFFICE = "post_office";
    public static final String POST_CODE = "post_code";
    public static final String WARD = "ward_id";
    public static final String THANA = "thana_id";
    public static final String CITY_CORPORATION = "city_corporation_id";
    public static final String COUNTRY = "country_code";
    public static final String PERMANENT_ADDRESS_LINE = "permanent_address_line";
    public static final String PERMANENT_DIVISION_ID = "permanent_division_id";
    public static final String PERMANENT_DISTRICT_ID = "permanent_district_id";
    public static final String PERMANENT_UPAZILLA_ID = "permanent_upazilla_id";
    public static final String PERMANENT_UNION_ID = "permanent_union_id";
    public static final String PERMANENT_HOLDING_NUMBER = "permanent_holding_number";
    public static final String PERMANENT_STREET = "permanent_street";
    public static final String PERMANENT_AREA_MOUJA = "permanent_area_mouja";
    public static final String PERMANENT_VILLAGE = "permanent_village";
    public static final String PERMANENT_POST_OFFICE = "permanent_post_office";
    public static final String PERMANENT_POST_CODE = "permanent_post_code";
    public static final String PERMANENT_WARD = "permanent_ward_id";
    public static final String PERMANENT_THANA = "permanent_thana_id";
    public static final String PERMANENT_CITY_CORPORATION = "permanent_city_corporation_id";
    public static final String PERMANENT_COUNTRY = "permanent_country_code";
    public static final String FULL_NAME = "full_name";
    public static final String IS_ALIVE = "is_alive";
    public static final String RELATIONS = "relations";
    public static final String PRIMARY_CONTACT = "primary_contact";
    public static final String PHONE_NO = "phone_no";
    public static final String PRIMARY_CONTACT_NO = "primary_contact_no";

    public static final String PHONE_NUMBER_COUNTRY_CODE = "phone_number_country_code";
    public static final String PHONE_NUMBER_AREA_CODE = "phone_number_area_code";
    public static final String PHONE_NUMBER_EXTENSION = "phone_number_extension";

    public static final String PRIMARY_CONTACT_NUMBER_COUNTRY_CODE = "primary_contact_number_country_code";
    public static final String PRIMARY_CONTACT_NUMBER_AREA_CODE = "primary_contact_number_area_code";
    public static final String PRIMARY_CONTACT_NUMBER_EXTENSION = "primary_contact_number_extension";


    public static String buildFindByHidQuery(String hid) {
        return select().from("patient").where(eq(HEALTH_ID, hid)).toString();
    }

    public static String buildFindHidByNidQuery(String nid) {
        return select(HEALTH_ID).from("nid_mapping").where(eq(NATIONAL_ID, nid)).toString();
    }

    public static String buildFindHidByBrnQuery(String brn) {
        return select(HEALTH_ID).from("brn_mapping").where(eq(BIN_BRN, brn)).toString();
    }

    public static String buildFindHidByUidQuery(String uid) {
        return select(HEALTH_ID).from("uid_mapping").where(eq(UID, uid)).toString();
    }

    public static String buildFindHidByPhoneNumberQuery(String phoneNumber) {
        return select(HEALTH_ID).from("phone_number_mapping").where(eq("phone_number", phoneNumber)).toString();
    }

    public static String buildFindHidByAddressAndNameQuery(String divisionId, String districtId, String upazilaId, String givenName, String surname) {
        Where where = select(HEALTH_ID).from("name_mapping")
                .where(eq("division_id", divisionId))
                .and(eq("district_id", districtId))
                .and(eq("upazila_id", upazilaId))
                .and(eq("given_name", givenName));

        if (isNotBlank(surname)) {
            where = where.and(eq("surname", surname));
        }
        return where.toString();
    }
}
