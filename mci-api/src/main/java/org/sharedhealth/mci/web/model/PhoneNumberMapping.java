package org.sharedhealth.mci.web.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.HEALTH_ID;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.PHONE_NO;

@Table(value = "phone_number_mapping")
public class PhoneNumberMapping {

    @PrimaryKey("phone_number")
    private String phoneNumber;

    @Column(HEALTH_ID)
    private String healthId;

    public PhoneNumberMapping(String phoneNumber, String healthId) {
        this.phoneNumber = phoneNumber;
        this.healthId = healthId;
    }
}
