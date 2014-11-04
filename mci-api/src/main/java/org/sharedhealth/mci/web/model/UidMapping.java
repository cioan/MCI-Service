package org.sharedhealth.mci.web.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.BIN_BRN;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.HEALTH_ID;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.UID;

@Table(value = "uid_mapping")
public class UidMapping {

    @PrimaryKey(UID)
    private String uid;

    @Column(HEALTH_ID)
    private String healthId;

    public UidMapping(String uid, String healthId) {
        this.uid = uid;
        this.healthId = healthId;
    }
}
