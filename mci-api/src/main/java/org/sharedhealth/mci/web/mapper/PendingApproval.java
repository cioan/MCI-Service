package org.sharedhealth.mci.web.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.UUID;

import static com.datastax.driver.core.utils.UUIDs.unixTimestamp;
import static org.sharedhealth.mci.web.utils.JsonConstants.*;
import static org.sharedhealth.mci.web.utils.JsonMapper.convertValue;

public class PendingApproval implements Comparable<PendingApproval> {

    @JsonProperty(FIELD_NAME)
    private String name;

    @JsonProperty(CURRENT_VALUE)
    private Object currentValue;

    @JsonProperty(FIELD_DETAILS)
    TreeMap<UUID, PendingApprovalFieldDetails> fieldDetails;

    public PendingApproval() {
        fieldDetails = new TreeMap<>(new Comparator<UUID>() {
            @Override
            public int compare(UUID u1, UUID u2) {
                Long t1 = unixTimestamp(u1);
                Long t2 = unixTimestamp(u2);
                int result = t2.compareTo(t1);
                if (result == 0) {
                    return u2.compareTo(u1);
                }
                return result;

            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public TreeMap<UUID, PendingApprovalFieldDetails> getFieldDetails() {
        Class type;
        if (PRESENT_ADDRESS.equals(this.name) || PERMANENT_ADDRESS.equals(this.name)) {
            type = Address.class;
        } else if (PHONE_NUMBER.equals(this.name) || PRIMARY_CONTACT_NUMBER.equals(this.name)) {
            type = PhoneNumber.class;
        } else if (STATUS.equals(this.name)) {
            type = PatientStatus.class;
        } else {
            return this.fieldDetails;
        }
        for (PendingApprovalFieldDetails details : fieldDetails.values()) {
            details.setValue(convertValue(details.getValue(), type));

        }
        return fieldDetails;
    }

    public void setFieldDetails(TreeMap<UUID, PendingApprovalFieldDetails> fieldDetails) {
        this.fieldDetails.putAll(fieldDetails);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingApproval)) return false;

        PendingApproval that = (PendingApproval) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(PendingApproval o) {
        return this.getName().compareTo(o.getName());
    }

    public boolean contains(Object value) {
        if (value == null) {
            return false;
        }
        for (PendingApprovalFieldDetails fieldDetails : this.getFieldDetails().values()) {
            if (value.equals(fieldDetails.getValue())) {
                return true;
            }
        }
        return false;
    }
}
