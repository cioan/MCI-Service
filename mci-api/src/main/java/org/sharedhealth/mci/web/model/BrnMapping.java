package org.sharedhealth.mci.web.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.BIN_BRN;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.HEALTH_ID;

@Table(value = "brn_mapping")
public class BrnMapping {

    @PrimaryKey(BIN_BRN)
    private String bin_brn;

    @Column(HEALTH_ID)
    private String healthId;

    public BrnMapping(String bin_brn, String healthId) {
        this.bin_brn = bin_brn;
        this.healthId = healthId;
    }
}
