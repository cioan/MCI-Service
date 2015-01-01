package org.sharedhealth.mci.web.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.UUID;

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
            public int compare(UUID o1, UUID o2) {
                return o2.compareTo(o1);
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
            Object obj = convertFieldDetails(value.getClass(), fieldDetails.getValue());
            if (value.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    private Object convertFieldDetails(Class type, Object value) {
        if (PhoneNumber.class.equals(type)) {
            return convertValue(value, PhoneNumber.class);
        }
        if (Address.class.equals(type)) {
            return convertValue(value, Address.class);
        }
        return value;
    }
}