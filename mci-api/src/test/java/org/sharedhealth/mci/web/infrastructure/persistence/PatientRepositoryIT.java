package org.sharedhealth.mci.web.infrastructure.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sharedhealth.mci.web.config.EnvironmentMock;
import org.sharedhealth.mci.web.config.WebMvcConfig;
import org.sharedhealth.mci.web.exception.HealthIDExistException;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(initializers = EnvironmentMock.class, classes = WebMvcConfig.class)
public class PatientRepositoryIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    @Qualifier("MCICassandraTemplate")
    private CassandraTemplate cqlTemplate;

    @Autowired
    private PatientRepository patientRepository;

    private PatientDto patientDto;
    private String nationalId = "1234567890123";
    private String birthRegistrationNumber = "12345678901234567";
    private String uid = "12345678901";


    @Before
    public void setup() throws ExecutionException, InterruptedException {
        patientDto = new PatientDto();
        patientDto.setNationalId(nationalId);
        patientDto.setBirthRegistrationNumber(birthRegistrationNumber);
        patientDto.setUid(uid);
        patientDto.setGivenName("Scott");
        patientDto.setSurName("Tiger");
        patientDto.setDateOfBirth("2014-12-01");
        patientDto.setGender("M");
        patientDto.setOccupation("salaried");
        patientDto.setEducationLevel("BA");

        Address address = new Address();
        address.setAddressLine("house-10");
        address.setDivisionId("10");
        address.setDistrictId("04");
        address.setUpazillaId("09");
        address.setCityCorporationId("20");
        address.setWardId("01");
        patientDto.setAddress(address);

    }

    @Test
    public void shouldFindPatientWithMatchingGeneratedHealthId() throws ExecutionException, InterruptedException {
        MCIResponse mciResponse = patientRepository.create(patientDto);
        PatientDto p = patientRepository.findByHealthId(mciResponse.id);
        assertNotNull(p);
        patientDto.setHealthId(mciResponse.id);
        patientDto.setCreatedAt(p.getCreatedAt());
        patientDto.setUpdatedAt(p.getUpdatedAt());
        assertEquals(patientDto.getHealthId(), p.getHealthId());
    }

    @Test(expected = HealthIDExistException.class)
    public void shouldThrowException_IfHealthIdProvidedForCreate() throws ExecutionException, InterruptedException {
        patientDto.setHealthId("12");
        patientRepository.create(patientDto);
    }

    @Test
    public void shouldFindPatientWithMatchingNationalId() throws ExecutionException, InterruptedException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setNid(nationalId);
        assertPatient(criteria);
    }

    @Test
    public void shouldFindPatientWithMatchingBirthRegistrationNumber() throws ExecutionException, InterruptedException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setBrn(birthRegistrationNumber);
        assertPatient(criteria);
    }

    @Test
    public void shouldFindPatientWithMatchingUid() throws ExecutionException, InterruptedException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUid(uid);
        assertPatient(criteria);
    }

    private void assertPatient(SearchCriteria criteria) throws ExecutionException, InterruptedException {
        MCIResponse mciResponse = patientRepository.create(patientDto);
        final List<PatientDto> p = patientRepository.findAll(criteria);
        assertNotNull(p);
        assertEquals(1, p.size());
        patientDto.setHealthId(mciResponse.id);
        patientDto.setCreatedAt(p.get(0).getCreatedAt());
        patientDto.setUpdatedAt(p.get(0).getUpdatedAt());
        assertEquals(patientDto.getNationalId(), p.get(0).getNationalId());
        assertEquals(patientDto.getBirthRegistrationNumber(), p.get(0).getBirthRegistrationNumber());
        assertEquals(patientDto.getUid(), p.get(0).getUid());
    }

    @After
    public void teardown() {
        cqlTemplate.execute("truncate patient");
    }
}