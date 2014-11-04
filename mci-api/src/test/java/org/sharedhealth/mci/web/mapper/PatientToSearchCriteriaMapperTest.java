package org.sharedhealth.mci.web.mapper;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sharedhealth.mci.web.controller.PatientController;
import org.sharedhealth.mci.web.model.Patient;
import org.sharedhealth.mci.web.model.SearchCriteria;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientToSearchCriteriaMapperTest {



    public Patient patient = new Patient();
    public SearchCriteria searchCriteria = new SearchCriteria();
    private String nationalId = "1234567890123";
    private String birthRegistrationNumber = "12345678901234567";
    private String fullname = "Scott Tiger";
    private String uid = "11111111111";


    @Before
    public void setup() {
        patient.setNationalId(nationalId);
        patient.setBirthRegistrationNumber(birthRegistrationNumber);
        patient.setGivenName("Scott");
        patient.setSurName("Tiger");
        patient.setDateOfBirth("2014-12-01");
        patient.setDivisionId("10");
        patient.setDistrictId("04");
        patient.setUpazillaId("09");
        patient.setCityCorporationId("20");
        patient.setVillage("10");
        patient.setWardId("01");
    }

    @Test
    public void shouldConvertPatientToSearchCriteria(){

        String healthId = "healthId-100";
        patient.setHealthId(healthId);

        PatientToSearchCriteriaMapper.convertPatientToSearchCriteria(patient, searchCriteria);
        assertEquals(patient.getHealthId(),searchCriteria.getHealthId());
        assertEquals("Scott",searchCriteria.getGivenName());
    }



}