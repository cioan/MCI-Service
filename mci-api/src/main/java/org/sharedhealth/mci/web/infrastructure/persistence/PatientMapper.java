package org.sharedhealth.mci.web.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.PhoneNumber;
import org.sharedhealth.mci.web.mapper.Relation;
import org.sharedhealth.mci.web.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {

    public Patient toEntity(PatientDto dto) {

        String relationsJson = "";
        ObjectMapper mapper = new ObjectMapper();

        Relation father = dto.getRelation("FTH");
        Relation mother = dto.getRelation("MTH");

        try {
            relationsJson = mapper.writeValueAsString(dto.getRelations());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Address address = dto.getAddress();
        Address permanentAddress = dto.getPermanentAddress();

        PhoneNumber phoneNumber = dto.getPhoneNumber();
        PhoneNumber primaryContactNumber = dto.getPrimaryContactNumber();

        Patient patient = new Patient();
        patient.setHealthId(dto.getHealthId());
        patient.setNationalId(dto.getNationalId());
        patient.setBirthRegistrationNumber(dto.getBirthRegistrationNumber());
        patient.setFullNameBangla(StringUtils.trim(dto.getNameBangla()));
        patient.setGivenName(StringUtils.trim(dto.getGivenName()));
        patient.setSurName(StringUtils.trim(dto.getSurName()));
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setOccupation(dto.getOccupation());
        patient.setEducationLevel(dto.getEducationLevel());

        if (father != null) {
            patient.setFathersNameBangla(StringUtils.trim(father.getNameBangla()));
            patient.setFathersGivenName(StringUtils.trim(father.getGivenName()));
            patient.setFathersSurName(StringUtils.trim(father.getSurName()));
            patient.setFathersBrn(father.getBirthRegistrationNumber());
            patient.setFathersNid(father.getNationalId());
            patient.setFathersUid(father.getUid());
        }

        if (mother != null) {
            patient.setMothersNameBangla(StringUtils.trim(mother.getNameBangla()));
            patient.setMothersGivenName(StringUtils.trim(mother.getGivenName()));
            patient.setMothersSurName(StringUtils.trim(mother.getSurName()));
            patient.setMothersBrn(mother.getBirthRegistrationNumber());
            patient.setMothersNid(mother.getNationalId());
            patient.setMothersUid(mother.getUid());
        }

        patient.setUid(dto.getUid());
        patient.setPlaceOfBirth(StringUtils.trim(dto.getPlaceOfBirth()));
        patient.setReligion(dto.getReligion());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setNationality(StringUtils.trim(dto.getNationality()));
        patient.setDisability(dto.getDisability());
        patient.setEthnicity(dto.getEthnicity());
        patient.setIsAlive(dto.getIsAlive());
        patient.setMaritalStatus(dto.getMaritalStatus());

        if (address != null) {
            patient.setAddressLine(address.getAddressLine());
            patient.setDivisionId(address.getDivisionId());
            patient.setDistrictId(address.getDistrictId());
            patient.setUpazillaId(address.getUpazillaId());
            patient.setUnionId(address.getUnionId());
            patient.setHoldingNumber(StringUtils.trim(address.getHoldingNumber()));
            patient.setStreet(StringUtils.trim(address.getStreet()));
            patient.setAreaMouja(StringUtils.trim(address.getAreaMouja()));
            patient.setVillage(StringUtils.trim(address.getVillage()));
            patient.setPostOffice(StringUtils.trim(address.getPostOffice()));
            patient.setPostCode(address.getPostCode());
            patient.setWardId(address.getWardId());
            patient.setThanaId(address.getThanaId());
            patient.setCityCorporationId(address.getCityCorporationId());
            patient.setCountryCode(address.getCountryCode());
        }

        if (permanentAddress != null) {
            patient.setPermanentAddressLine(permanentAddress.getAddressLine());
            patient.setPermanentDivisionId(permanentAddress.getDivisionId());
            patient.setPermanentDistrictId(permanentAddress.getDistrictId());
            patient.setPermanentUpazillaId(permanentAddress.getUpazillaId());
            patient.setPermanentUnionId(permanentAddress.getUnionId());
            patient.setPermanentHoldingNumber(StringUtils.trim(permanentAddress.getHoldingNumber()));
            patient.setPermanentStreet(StringUtils.trim(permanentAddress.getStreet()));
            patient.setPermanentAreaMouja(StringUtils.trim(permanentAddress.getAreaMouja()));
            patient.setPermanentVillage(StringUtils.trim(permanentAddress.getVillage()));
            patient.setPermanentPostOffice(StringUtils.trim(permanentAddress.getPostOffice()));
            patient.setPermanentPostCode(permanentAddress.getPostCode());
            patient.setPermanentWardId(permanentAddress.getWardId());
            patient.setPermanentThanaId(permanentAddress.getThanaId());
            patient.setPermanentCityCorporationId(permanentAddress.getCityCorporationId());
            patient.setPermanentCountryCode(permanentAddress.getCountryCode());
        }

        patient.setRelations(relationsJson);

        if (phoneNumber != null) {
            patient.setCellNo(phoneNumber.getNumber());
            patient.setPhoneNumberAreaCode(phoneNumber.getAreaCode());
            patient.setPhoneNumberCountryCode(phoneNumber.getCountryCode());
            patient.setPhoneNumberExtension(phoneNumber.getExtension());
        }

        if (primaryContactNumber != null) {
            patient.setPrimaryCellNo(primaryContactNumber.getNumber());
            patient.setPrimaryContactNumberAreaCode(primaryContactNumber.getAreaCode());
            patient.setPrimaryContactNumberCountryCode(primaryContactNumber.getCountryCode());
            patient.setPrimaryContactNumberExtension(primaryContactNumber.getExtension());
        }

        patient.setPrimaryContact(StringUtils.trim(dto.getPrimaryContact()));


        return patient;
    }

    public List<PatientDto> toDto(List<Patient> patients) {
        List<PatientDto> dtos = new ArrayList<>();
        for (Patient patient : patients) {
            dtos.add(toDto(patient));
        }
        return dtos;
    }

    public PatientDto toDto(Patient patient) {
        PatientDto patientDto = new PatientDto();

        patientDto.setHealthId(patient.getHealthId());
        patientDto.setNationalId(patient.getNationalId());
        patientDto.setUid(patient.getUid());
        patientDto.setPlaceOfBirth(patient.getPlaceOfBirth());

        patientDto.setReligion(patient.getReligion());
        patientDto.setBloodGroup(patient.getBloodGroup());
        patientDto.setNameBangla(patient.getFullNameBangla());
        patientDto.setBirthRegistrationNumber(patient.getBirthRegistrationNumber());
        patientDto.setGivenName(patient.getGivenName());
        patientDto.setSurName(patient.getSurName());
        //  patientDto.setDateOfBirth(patient.getDateOfBirth().toString());
        patientDto.setGender(patient.getGender());
        patientDto.setOccupation(patient.getOccupation());
        patientDto.setEducationLevel(patient.getEducationLevel());
        patientDto.setNationality(patient.getNationality());
        patientDto.setDisability(patient.getDisability());
        patientDto.setEthnicity(patient.getEthnicity());
        patientDto.setIsAlive(patient.getIsAlive());
        patientDto.setMaritalStatus(patient.getMaritalStatus());

        patientDto.setPrimaryContact(patient.getPrimaryContact());


        Address address = new Address();
        address.setAddressLine(patient.getAddressLine());
        address.setDivisionId(patient.getDivisionId());
        address.setDistrictId(patient.getDistrictId());
        address.setUpazillaId(patient.getUpazillaId());
        address.setUnionId(patient.getUnionId());
        address.setHoldingNumber(patient.getHoldingNumber());
        address.setStreet(patient.getStreet());
        address.setAreaMouja(patient.getAreaMouja());
        address.setVillage(patient.getVillage());
        address.setPostOffice(patient.getPostOffice());
        address.setPostCode(patient.getPostCode());
        address.setWardId(patient.getWardId());
        address.setThanaId(patient.getThanaId());
        address.setCityCorporationId(patient.getCityCorporationId());
        address.setCountryCode(patient.getCountryCode());
        patientDto.setAddress(address);

        Address permanentaddress = new Address();
        permanentaddress.setAddressLine(patient.getPermanentAddressLine());
        permanentaddress.setDivisionId(patient.getPermanentDivisionId());
        permanentaddress.setDistrictId(patient.getPermanentDistrictId());
        permanentaddress.setUpazillaId(patient.getPermanentUpazillaId());
        permanentaddress.setUnionId(patient.getPermanentUnionId());
        permanentaddress.setHoldingNumber(patient.getPermanentHoldingNumber());
        permanentaddress.setStreet(patient.getPermanentStreet());
        permanentaddress.setAreaMouja(patient.getPermanentAreaMouja());
        permanentaddress.setVillage(patient.getPermanentVillage());
        permanentaddress.setPostOffice(patient.getPostOffice());
        permanentaddress.setPostCode(patient.getPostCode());
        permanentaddress.setWardId(patient.getPermanentWardId());
        permanentaddress.setThanaId(patient.getPermanentThanaId());
        permanentaddress.setCityCorporationId(patient.getPermanentCityCorporationId());
        permanentaddress.setCountryCode(patient.getPermanentCountryCode());

        PhoneNumber phoneNumber = new PhoneNumber();
        PhoneNumber primaryContactNumber = new PhoneNumber();

        phoneNumber.setNumber(patient.getCellNo());
        phoneNumber.setAreaCode(patient.getPhoneNumberAreaCode());
        phoneNumber.setCountryCode(patient.getPhoneNumberCountryCode());
        phoneNumber.setExtension(patient.getPhoneNumberExtension());

        primaryContactNumber.setNumber(patient.getPrimaryCellNo());
        primaryContactNumber.setAreaCode(patient.getPrimaryContactNumberAreaCode());
        primaryContactNumber.setCountryCode(patient.getPrimaryContactNumberCountryCode());
        primaryContactNumber.setExtension(patient.getPrimaryContactNumberExtension());

        if (primaryContactNumber.getNumber() != null) {
            patientDto.setPhoneNumber(phoneNumber);
        }

        if (phoneNumber.getNumber() != null) {
            patientDto.setPrimaryContactNumber(primaryContactNumber);
        }

        if (permanentaddress.getCountryCode() != null) {
            if (permanentaddress.getCountryCode() == "050" && permanentaddress.getDistrictId() != null) {
                patientDto.setPermanentAddress(permanentaddress);
            }

            if (permanentaddress.getCountryCode() != "050") {
                patientDto.setPermanentAddress(permanentaddress);
            }
        }

        patientDto.setCreatedAt(patient.getCreatedAt());
        patientDto.setUpdatedAt(patient.getUpdatedAt());

        return patientDto;
    }
}
