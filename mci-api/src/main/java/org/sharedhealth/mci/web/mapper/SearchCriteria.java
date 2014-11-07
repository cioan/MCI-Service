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
    @JsonProperty("nid")
    @Pattern(regexp = "[\\d]{13}|[\\d]{17}", message = "1002")
    private String nid;

    @JsonProperty("brn")
    @Pattern(regexp = "[\\d]{17}", message = "1002")
    private String brn;

    @JsonProperty("uid")
    @Pattern(regexp = "[a-zA-Z0-9]{11}", message = "1002")
    private String uid;

    private String areaCode;

    private String phoneNumber;

    @JsonProperty("division_id")
    private String divisionId;

    @JsonProperty("district_id")
    private String districtId;

    @JsonProperty("upazila_id")
    private String upazilaId;

    private String cityCorpId;

    private String wardId;

    private String ruralWardId;

    @JsonProperty("given_name")
    @Length(max = 100, min = 1, message = "1002")
    private String givenName;

    @JsonProperty("surname")
    @Pattern(regexp = "^(\\s*)([A-Za-z0-9]{1,25})(\\b\\s*$)", message = "1002")
    private String surname;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getBrn() {
        return brn;
    }

    public void setBrn(String brn) {
        this.brn = brn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public String getCityCorpId() {
        return cityCorpId;
    }

    public void setCityCorpId(String cityCorpId) {
        this.cityCorpId = cityCorpId;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getRuralWardId() {
        return ruralWardId;
    }

    public void setRuralWardId(String ruralWardId) {
        this.ruralWardId = ruralWardId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
