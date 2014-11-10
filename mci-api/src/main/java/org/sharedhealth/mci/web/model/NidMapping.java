package org.sharedhealth.mci.web.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.HEALTH_ID;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.NATIONAL_ID;

@Table(value = "nid_mapping")
public class NidMapping {

    @PrimaryKey(NATIONAL_ID)
    private String nationalId;

    @Column(HEALTH_ID)
    private String healthId;

    public NidMapping(String nationalId, String healthId) {
        this.nationalId = nationalId;
        this.healthId = healthId;
    }
}
