package org.sharedhealth.mci.web.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.DIVISION_ID;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.HEALTH_ID;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.PHONE_NO;
import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

@Table(value = "phone_number_mapping")
public class PhoneNumberMapping {

    @PrimaryKeyColumn(name = "phone_number", ordinal = 0, type = PARTITIONED)
    private String phoneNumber;

    @PrimaryKeyColumn(name = "area_code", ordinal = 1, type = CLUSTERED)
    private String areaCode;

    @PrimaryKeyColumn(name = HEALTH_ID, ordinal = 2, type = CLUSTERED)
    private String healthId;

    public PhoneNumberMapping(String areaCode, String phoneNumber, String healthId) {
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.healthId = healthId;
    }
}
