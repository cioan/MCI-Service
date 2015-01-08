package org.sharedhealth.mci.web.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.sharedhealth.mci.utils.DateUtil;
import org.sharedhealth.mci.validation.constraints.*;
import org.sharedhealth.mci.validation.group.RequiredGroup;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sharedhealth.mci.web.utils.ErrorConstants.*;
import static org.sharedhealth.mci.web.utils.JsonConstants.*;
import static org.sharedhealth.mci.web.utils.PatientDataConstants.*;

@MaritalRelation(message = ERROR_CODE_DEPENDENT, field = "maritalStatus")
@PatientStatus(message = ERROR_CODE_DEPENDENT)
@JsonIgnoreProperties({"created_at"})
public class PatientData {

    private static final String INVALID_CATCHMENT = "invalid.catchment";

    @JsonProperty(HID)
    private String healthId;

    @JsonProperty(NID)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{13}|[\\d]{17}", message = ERROR_CODE_PATTERN)
    private String nationalId;

    @JsonProperty(BIN_BRN)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[\\d]{17}", message = ERROR_CODE_PATTERN)
    private String birthRegistrationNumber;

    @JsonProperty(NAME_BANGLA)
    @JsonInclude(NON_EMPTY)
    @Length(max = 125, message = ERROR_CODE_PATTERN)
    private String nameBangla;

    @JsonProperty(GIVEN_NAME)
    @NotNull(message = ERROR_CODE_REQUIRED, groups = RequiredGroup.class)
    @Length(max = 100, min = 1, message = ERROR_CODE_PATTERN)
    private String givenName;

    @JsonProperty(SUR_NAME)
    @NotNull(message = ERROR_CODE_REQUIRED, groups = RequiredGroup.class)
    @Pattern(regexp = "^(\\s*)([A-Za-z0-9]{1,25})(\\b\\s*$)", message = ERROR_CODE_PATTERN)
    private String surName;

    @JsonProperty(DATE_OF_BIRTH)
    @NotNull(message = ERROR_CODE_REQUIRED, groups = RequiredGroup.class)
    @Date(format = "yyyy-MM-dd", message = ERROR_CODE_PATTERN)
    @Length(min = 1, max = 10, message = ERROR_CODE_PATTERN)
    private String dateOfBirth;

    @JsonProperty(GENDER)
    @NotBlank(message = ERROR_CODE_REQUIRED, groups = RequiredGroup.class)
    @Code(type = GENDER, regexp = "[A-Z]{1}", message = ERROR_CODE_INVALID)
    private String gender;

    @JsonProperty(OCCUPATION)
    @JsonInclude(NON_EMPTY)
    @Code(type = OCCUPATION, regexp = "[\\d]{2}", message = ERROR_CODE_INVALID)
    private String occupation;

    @JsonProperty(EDU_LEVEL)
    @JsonInclude(NON_EMPTY)
    @Code(type = EDUCATION_LEVEL, regexp = "[\\d]{2}", message = ERROR_CODE_INVALID)
    private String educationLevel;

    @JsonProperty(RELATIONS)
    @JsonInclude(NON_EMPTY)
    @Valid
    private List<Relation> relations;

    @JsonProperty(UID)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[a-zA-Z0-9]{11}", message = ERROR_CODE_PATTERN)
    private String uid;

    @JsonInclude(NON_EMPTY)
    @JsonProperty(PLACE_OF_BIRTH)
    @Pattern(regexp = "^[a-zA-Z0-9]{0,20}$", message = ERROR_CODE_PATTERN)
    private String placeOfBirth;

    @JsonProperty(RELIGION)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[1|2|3|4|8|9|0]{1}", message = ERROR_CODE_INVALID)
    private String religion;

    @JsonProperty(BLOOD_GROUP)
    @JsonInclude(NON_EMPTY)
    @Code(type = "blood_group", regexp = "[\\d]{1}", message = ERROR_CODE_INVALID)
    private String bloodGroup;

    @JsonProperty(NATIONALITY)
    @JsonInclude(NON_EMPTY)
    @Length(max = 50, message = ERROR_CODE_PATTERN)
    private String nationality;

    @JsonProperty(DISABILITY)
    @JsonInclude(NON_EMPTY)
    @Code(type = DISABILITY, regexp = "[\\d]{1}", message = ERROR_CODE_INVALID)
    private String disability;

    @JsonProperty(ETHNICITY)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "[0-9]{2}", message = ERROR_CODE_INVALID)
    private String ethnicity;

    @JsonProperty(PRESENT_ADDRESS)
    @NotNull(message = ERROR_CODE_REQUIRED, groups = RequiredGroup.class)
    @Valid
    @Location(message = ERROR_CODE_INVALID, country_code = COUNTRY_CODE_BANGLADESH)
    private Address address;

    @JsonProperty(PRIMARY_CONTACT)
    @JsonInclude(NON_EMPTY)
    @Length(max = 100, message = ERROR_CODE_PATTERN)
    private String primaryContact;

    @JsonProperty(PHONE_NUMBER)
    @Valid
    @JsonInclude(NON_EMPTY)
    private PhoneNumber phoneNumber;

    @JsonProperty(PRIMARY_CONTACT_NUMBER)
    @Valid
    @JsonInclude(NON_EMPTY)
    private PhoneNumber primaryContactNumber;

    @JsonProperty(PERMANENT_ADDRESS)
    @Valid
    @JsonInclude(NON_EMPTY)
    @Location(message = ERROR_CODE_INVALID)
    private Address permanentAddress;

    @JsonProperty(MARITAL_STATUS)
    @JsonInclude(NON_EMPTY)
    @Code(type = MARITAL_STATUS, regexp = "[\\d]{1}", message = ERROR_CODE_INVALID)
    private String maritalStatus;

    @JsonProperty(FULL_NAME)
    @JsonInclude(NON_EMPTY)
    private String fullName;

    @JsonProperty(PATIENT_STATUS)
    @JsonInclude(NON_EMPTY)
    @Code(type = PATIENT_STATUS, regexp = "[\\d]{1}", message = ERROR_CODE_INVALID)
    private String status;

    @JsonProperty(CONFIDENTIAL)
    @JsonInclude(NON_EMPTY)
    @Pattern(regexp = "^(yes|no)$", message = ERROR_CODE_INVALID, flags = Flag.CASE_INSENSITIVE)
    private String confidential;

    @JsonProperty(DATE_OF_DEATH)
    @JsonInclude(NON_EMPTY)
    @Date(format = DateUtil.DEFAULT_DATE_FORMAT, message = ERROR_CODE_PATTERN)
    private String dateOfDeath;

    @JsonProperty(CREATED)
    @JsonInclude(NON_EMPTY)
    private String createdAt;

    @JsonProperty(MODIFIED)
    @JsonInclude(NON_EMPTY)
    private String updatedAt;

    @JsonIgnore
    private TreeSet<PendingApproval> pendingApprovals;

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getBirthRegistrationNumber() {
        return birthRegistrationNumber;
    }

    public void setBirthRegistrationNumber(String birthRegistrationNumber) {
        this.birthRegistrationNumber = birthRegistrationNumber;
    }

    public String getNameBangla() {
        return nameBangla;
    }

    public void setNameBangla(String nameBangla) {
        this.nameBangla = nameBangla;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }


    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Address getPermanentAddress() {
        return permanentAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPermanentAddress(Address permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public Relation getRelationOfType(String relationType) {

        if (this.relations == null) {
            return null;
        }

        for (Relation relation : this.relations) {

            if (relation.getType() != null && relation.getType().equals(relationType)) {
                return relation;
            }
        }

        return null;
    }

    public Relation getRelationById(String id) {

        if (this.relations == null) {
            return null;
        }

        for (Relation relation : this.relations) {

            if (relation.getId() != null && relation.getId().equals(id)) {
                return relation;
            }
        }

        return null;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    @JsonIgnore
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = (createdAt == null) ? null : DateFormatUtils.ISO_DATETIME_FORMAT.format(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    @JsonIgnore
    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = (updatedAt == null) ? null : DateFormatUtils.ISO_DATETIME_FORMAT.format(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(PhoneNumber primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public TreeSet<PendingApproval> getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(TreeSet<PendingApproval> pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public Object getValue(String jsonKey) {
        for (Field field : PatientData.class.getDeclaredFields()) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                String value = jsonProperty.value();
                if (value != null && value.equals(jsonKey)) {
                    try {
                        return field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

    public List<String> findNonEmptyFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : PatientData.class.getDeclaredFields()) {
            try {
                Object value = field.get(this);
                if (value != null) {
                    if (value instanceof String && StringUtils.isBlank(valueOf(value))) {
                        continue;
                    }
                    JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                    if (jsonProperty != null) {
                        fieldNames.add(jsonProperty.value());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return fieldNames;
    }

    public boolean belongsTo(Catchment catchment) {
        Address address = this.getAddress();

        if (catchment == null || address == null) {
            throw new IllegalArgumentException(INVALID_CATCHMENT);
        }

        String catchmentId = defaultString(catchment.getDivisionId()) + defaultString(catchment.getDistrictId())
                + defaultString(catchment.getUpazilaId()) + defaultString(catchment.getCityCorpId())
                + defaultString(catchment.getUnionOrUrbanWardId()) + defaultString(catchment.getRuralWardId());

        String addressId = defaultString(address.getDivisionId()) + defaultString(address.getDistrictId())
                + defaultString(address.getUpazilaId()) + defaultString(address.getCityCorporationId())
                + defaultString(address.getUnionOrUrbanWardId()) + defaultString(address.getRuralWardId());

        return addressId.startsWith(catchmentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientData)) return false;

        PatientData that = (PatientData) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (birthRegistrationNumber != null ? !birthRegistrationNumber.equals(that.birthRegistrationNumber) : that.birthRegistrationNumber != null)
            return false;
        if (bloodGroup != null ? !bloodGroup.equals(that.bloodGroup) : that.bloodGroup != null) return false;
        if (confidential != null ? !confidential.equals(that.confidential) : that.confidential != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(that.dateOfBirth) : that.dateOfBirth != null) return false;
        if (dateOfDeath != null ? !dateOfDeath.equals(that.dateOfDeath) : that.dateOfDeath != null) return false;
        if (disability != null ? !disability.equals(that.disability) : that.disability != null) return false;
        if (educationLevel != null ? !educationLevel.equals(that.educationLevel) : that.educationLevel != null)
            return false;
        if (ethnicity != null ? !ethnicity.equals(that.ethnicity) : that.ethnicity != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (givenName != null ? !givenName.equals(that.givenName) : that.givenName != null) return false;
        if (healthId != null ? !healthId.equals(that.healthId) : that.healthId != null) return false;
        if (maritalStatus != null ? !maritalStatus.equals(that.maritalStatus) : that.maritalStatus != null)
            return false;
        if (nameBangla != null ? !nameBangla.equals(that.nameBangla) : that.nameBangla != null) return false;
        if (nationalId != null ? !nationalId.equals(that.nationalId) : that.nationalId != null) return false;
        if (nationality != null ? !nationality.equals(that.nationality) : that.nationality != null) return false;
        if (occupation != null ? !occupation.equals(that.occupation) : that.occupation != null) return false;
        if (pendingApprovals != null ? !pendingApprovals.equals(that.pendingApprovals) : that.pendingApprovals != null)
            return false;
        if (permanentAddress != null ? !permanentAddress.equals(that.permanentAddress) : that.permanentAddress != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;
        if (placeOfBirth != null ? !placeOfBirth.equals(that.placeOfBirth) : that.placeOfBirth != null) return false;
        if (primaryContact != null ? !primaryContact.equals(that.primaryContact) : that.primaryContact != null)
            return false;
        if (primaryContactNumber != null ? !primaryContactNumber.equals(that.primaryContactNumber) : that.primaryContactNumber != null)
            return false;
        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        if (religion != null ? !religion.equals(that.religion) : that.religion != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (surName != null ? !surName.equals(that.surName) : that.surName != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (updatedAt != null ? !updatedAt.equals(that.updatedAt) : that.updatedAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = healthId != null ? healthId.hashCode() : 0;
        result = 31 * result + (nationalId != null ? nationalId.hashCode() : 0);
        result = 31 * result + (birthRegistrationNumber != null ? birthRegistrationNumber.hashCode() : 0);
        result = 31 * result + (nameBangla != null ? nameBangla.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (surName != null ? surName.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (occupation != null ? occupation.hashCode() : 0);
        result = 31 * result + (educationLevel != null ? educationLevel.hashCode() : 0);
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (placeOfBirth != null ? placeOfBirth.hashCode() : 0);
        result = 31 * result + (religion != null ? religion.hashCode() : 0);
        result = 31 * result + (bloodGroup != null ? bloodGroup.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (disability != null ? disability.hashCode() : 0);
        result = 31 * result + (ethnicity != null ? ethnicity.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (primaryContact != null ? primaryContact.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (primaryContactNumber != null ? primaryContactNumber.hashCode() : 0);
        result = 31 * result + (permanentAddress != null ? permanentAddress.hashCode() : 0);
        result = 31 * result + (maritalStatus != null ? maritalStatus.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (confidential != null ? confidential.hashCode() : 0);
        result = 31 * result + (dateOfDeath != null ? dateOfDeath.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (pendingApprovals != null ? pendingApprovals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PatientData{" +
                "healthId='" + healthId + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", birthRegistrationNumber='" + birthRegistrationNumber + '\'' +
                ", nameBangla='" + nameBangla + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", occupation='" + occupation + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", relations=" + relations +
                ", uid='" + uid + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", religion='" + religion + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", nationality='" + nationality + '\'' +
                ", disability='" + disability + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", address=" + address +
                ", primaryContact='" + primaryContact + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", primaryContactNumber=" + primaryContactNumber +
                ", permanentAddress=" + permanentAddress +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status='" + status + '\'' +
                ", dateOfDeath='" + dateOfDeath + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", pendingApprovals=" + pendingApprovals +
                '}';
    }

    public String getDateOfDeath() {
        return this.dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getConfidential() {
        return confidential;
    }

    public void setConfidential(String confidential) {
        this.confidential = confidential;
    }

    @JsonIgnore
    public Catchment getCatchment() {
        Address address = this.getAddress();
        Catchment catchment = new Catchment(address.getDivisionId(), address.getDistrictId());
        String upazilaId = address.getUpazilaId();

        if (isNotBlank(upazilaId)) {
            catchment.setUpazilaId(upazilaId);
            String cityCorporationId = address.getCityCorporationId();

            if (isNotBlank(cityCorporationId)) {
                catchment.setCityCorpId(cityCorporationId);
                String unionOrUrbanWardId = address.getUnionOrUrbanWardId();

                if (isNotBlank(unionOrUrbanWardId)) {
                    catchment.setUnionOrUrbanWardId(unionOrUrbanWardId);
                    String ruralWardId = address.getRuralWardId();

                    if (isNotBlank(ruralWardId)) {
                        catchment.setRuralWardId(ruralWardId);
                    }
                }
            }
        }
        return catchment;
    }
}
