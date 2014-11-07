package org.sharedhealth.mci.web.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sharedhealth.mci.validation.constraints.Length;
import org.sharedhealth.mci.validation.constraints.SearchQueryConstraint;

import javax.validation.constraints.Pattern;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;


@JsonIgnoreProperties(ignoreUnknown = true)
@SearchQueryConstraint(message = "1006")
public class SearchCriteria extends PaginationQuery {
    public void setNid(String nid) {
        this.nid = nid;
    }

    public void setBrn(String brn) {
        this.brn = brn;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty("nid")
    @Pattern(regexp = "[\\d]{13}|[\\d]{17}", message = "1002")
    private String nid;

    @JsonProperty("brn")
    @Pattern(regexp = "[\\d]{17}", message = "1002")
    private String brn;

    @JsonProperty("uid")
    @Pattern(regexp = "[a-zA-Z0-9]{11}", message = "1002")
    private String uid;

    @JsonProperty("division_id")
    //@Pattern(regexp = "[\\d]{6}|[\\d]{8}|[\\d]{10}", message = "1002")
    private String divisionId;

    @JsonProperty("district_id")
    private String districtId;

    @JsonProperty("upazila_id")
    private String upazilaId;

    @JsonProperty("given_name")
    @Length(max = 100, min = 1, message = "1002")
    private String givenName;

    @JsonProperty("surname")
    @Pattern(regexp = "^(\\s*)([A-Za-z0-9]{1,25})(\\b\\s*$)", message = "1002")
    private String surname;

    public String getNid() {
        return nid;
    }

    public String getBrn() {
        return brn;
    }

    public String getUid() {
        return uid;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public boolean equals(Object rhs) {
        return reflectionEquals(this, rhs);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}
