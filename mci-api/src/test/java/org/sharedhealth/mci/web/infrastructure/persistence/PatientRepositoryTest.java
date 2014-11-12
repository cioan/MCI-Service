package org.sharedhealth.mci.web.infrastructure.persistence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.sharedhealth.mci.web.model.Patient;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.*;

public class PatientRepositoryTest {

    @Mock
    private CassandraTemplate template;
    private PatientRepository patientRepository;
    private String nid = "1234567890123";
    private String brn = "12345678901234567";
    private String uid = "12345678901";

    @Before
    public void setup() {
        initMocks(this);
        patientRepository = new PatientRepository(template, new PatientMapper(), null);
    }

    @Test
    public void shouldReturnHealthId_ByComparingNidAndBrn() {
        when(template.queryForList(buildFindHidByNidQuery(nid), String.class)).thenReturn(asList("h1", "h2", "h3"));
        when(template.queryForList(buildFindHidByBrnQuery(brn), String.class)).thenReturn(asList("h2", "h3", "h4"));

        String hid = patientRepository.findHealthId(nid, brn, uid);
        verify(template, never()).queryForList(buildFindHidByUidQuery(uid), String.class);
        assertTrue(asList("h2", "h3").contains(hid));
    }

    @Test
    public void shouldReturnHealthId_ByComparingNidBrnAndUid() {
        when(template.queryForList(buildFindHidByNidQuery(nid), String.class)).thenReturn(asList("h1"));
        when(template.queryForList(buildFindHidByBrnQuery(brn), String.class)).thenReturn(asList("h2"));
        when(template.queryForList(buildFindHidByUidQuery(uid), String.class)).thenReturn(asList("h2"));

        String hid = patientRepository.findHealthId(nid, brn, uid);
        assertTrue("h2".equals(hid));
    }

    @Test
    public void shouldReturnListOfPatientsBySearchCriteria() {

        SearchCriteria search = new SearchCriteria();


    }

    @Test
    public void shouldReturnListOfHid_ByNidInSearchCriteria() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setNid(nid);
        when(template.queryForList(buildFindHidByNidQuery(nid), String.class)).thenReturn(asList("h1"));
        List<String> hids = patientRepository.findHealthIds(criteria);
        verify(template).queryForList(buildFindHidByNidQuery(nid), String.class);
        assertEquals(hids, asList("h1"));
    }

    @Test
    public void shouldReturnListOfHid_ByBrnInSearchCriteria() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setBrn(brn);
        when(template.queryForList(buildFindHidByBrnQuery(brn), String.class)).thenReturn(asList("h1", "h2"));
        List<String> hids = patientRepository.findHealthIds(criteria);
        verify(template).queryForList(buildFindHidByBrnQuery(brn), String.class);
        assertEquals(hids, asList("h1", "h2"));
    }

    @Test
    public void shouldReturnListOfHid_ByUidInSearchCriteria() {

        SearchCriteria criteria = new SearchCriteria();
        criteria.setUid(uid);
        when(template.queryForList(buildFindHidByUidQuery(uid), String.class)).thenReturn(asList("h2"));
        List<String> hids = patientRepository.findHealthIds(criteria);
        verify(template).queryForList(buildFindHidByUidQuery(uid), String.class);
        assertEquals(hids, asList("h2"));
    }

    @Test
    public void shouldReturnListOfHid_ByAddressAndNameInSearchCriteria() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setDivision_id("10");
        criteria.setDistrict_id("20");
        criteria.setUpazila_id("30");
        criteria.setGiven_name("Test");
        String query = buildFindHidByAddressAndNameQuery(criteria.getDivision_id(), criteria.getDistrict_id(),
                criteria.getUpazila_id(), criteria.getGiven_name(), criteria.getSurname());

        when(template.queryForList(query, String.class)).thenReturn(asList("h2"));
        List<String> hids = patientRepository.findHealthIds(criteria);
        verify(template).queryForList(query, String.class);
        assertEquals(hids, asList("h2"));
    }

    @Test
    public void shouldFilterPatientUsingSearchCriteria() {
        Patient patient = new Patient();
        patient.setNationalId(nid);
        patient.setUid(uid);
        patient.setBirthRegistrationNumber(brn);
        patient.setDivisionId("10");
        patient.setDistrictId("20");
        patient.setUpazillaId("30");
        patient.setGivenName("fname");
        patient.setSurName("lname");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setNid(nid);
        criteria.setBrn(brn);
        criteria.setGiven_name("fname");

        List<Patient> patients = new ArrayList<>();
        patients.add(patient);

        List<PatientDto> patientDtos = patientRepository.filterPatients(patients, criteria);
        assertEquals(1, patientDtos.size());
        assertEquals(nid, patientDtos.get(0).getNationalId());
        assertEquals(brn, patientDtos.get(0).getBirthRegistrationNumber());
        assertEquals(uid, patientDtos.get(0).getUid());
    }
}