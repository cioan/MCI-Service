package org.sharedhealth.mci.web.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sharedhealth.mci.validation.constraints.Length;
import org.sharedhealth.mci.validation.constraints.SearchCriteriaConstraint;
import org.sharedhealth.mci.validation.group.RequiredOnUpdateGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;


@JsonIgnoreProperties(ignoreUnknown = true)
@SearchCriteriaConstraint(message = "1006")
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

    @JsonProperty("area_code")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[0-9]*$", message = "1002")
    private String area_code;

    @JsonProperty("phone_number")
    @JsonInclude(NON_EMPTY)
    @NotNull(message = "1001", groups = RequiredOnUpdateGroup.class)
    @Pattern(regexp = "[0-9]{1,12}$", message = "1002")
    private String phone_number;

    @JsonProperty("division_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String division_id;

    @JsonProperty("district_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String district_id;

    @JsonProperty("upazila_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String upazila_id;

    @JsonProperty("city_corp_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String city_corp_id;

    @JsonProperty("ward_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String ward_id;

    @JsonProperty("rural_ward_id")
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{2}", message = "1002")
    private String rural_ward_id;

    @JsonProperty("given_name")
    @Length(max = 100, min = 1, message = "1002")
    private String given_name;

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

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDivision_id() {
        return division_id;
    }

    public void setDivision_id(String division_id) {
        this.division_id = division_id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getUpazila_id() {
        return upazila_id;
    }

    public void setUpazila_id(String upazila_id) {
        this.upazila_id = upazila_id;
    }

    public String getCity_corp_id() {
        return city_corp_id;
    }

    public void setCity_corp_id(String city_corp_id) {
        this.city_corp_id = city_corp_id;
    }

    public String getWard_id() {
        return ward_id;
    }

    public void setWard_id(String ward_id) {
        this.ward_id = ward_id;
    }

    public String getRural_ward_id() {
        return rural_ward_id;
    }

    public void setRural_ward_id(String rural_ward_id) {
        this.rural_ward_id = rural_ward_id;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
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
