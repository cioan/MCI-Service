package org.sharedhealth.mci.web.mapper;

import org.sharedhealth.mci.web.model.Patient;
import org.sharedhealth.mci.web.model.SearchCriteria;

public class PatientToSearchCriteriaMapper {

    public static void convertPatientToSearchCriteria(Patient patient, SearchCriteria searchCriteria) {

        searchCriteria.setBirthRegistrationNumber(patient.getBirthRegistrationNumber());
        searchCriteria.setHealthId(patient.getHealthId());
        searchCriteria.setGivenName(patient.getGivenName());
        searchCriteria.setSurName(patient.getSurName());
        searchCriteria.setDivisionId(patient.getDivisionId());
        searchCriteria.setDistrictId(patient.getDistrictId());
        searchCriteria.setUpazillaId(patient.getUpazillaId());
        searchCriteria.setCellNo(patient.getCellNo());
        searchCriteria.setNationalId(patient.getNationalId());
    }
}
