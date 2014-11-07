package org.sharedhealth.mci.web.mapper;

import org.sharedhealth.mci.web.model.Patient;

import java.util.ArrayList;

public class PatientToSearchCriteriaMapper {

    private ArrayList<Patient> patientArrayList;
    private ArrayList<SearchCriteria> searchCriterias;

    public PatientToSearchCriteriaMapper(ArrayList<Patient> patientArrayList) {
        this.patientArrayList = patientArrayList;
    }

    public  ArrayList<SearchCriteria> convertSearchResult(){
        for (Patient patient : patientArrayList) {
            SearchCriteria searchCriteria = new SearchCriteria();
            convertPatientToSearchCriteria(patient,searchCriteria);
            searchCriterias.add(searchCriteria);

        }
        return searchCriterias;
    }


    public static void convertPatientToSearchCriteria(Patient patient, SearchCriteria searchCriteria) {
//
//        searchCriteria.setBirthRegistrationNumber(patient.getBirthRegistrationNumber());
//        searchCriteria.setHealthId(patient.getHealthId());
//        searchCriteria.setGivenName(patient.getGivenName());
//        searchCriteria.setSurName(patient.getSurName());
//        searchCriteria.setDivisionId(patient.getDivisionId());
//        searchCriteria.setDistrictId(patient.getDistrictId());
//        searchCriteria.setUpazillaId(patient.getUpazillaId());
//        searchCriteria.setCellNo(patient.getCellNo());
//        searchCriteria.setNationalId(patient.getNationalId());
   }
}
