package org.sharedhealth.mci.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sharedhealth.mci.web.infrastructure.persistence.PatientRepository;
import org.sharedhealth.mci.web.mapper.PatientDto;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientDto dto;
    private String nid = "1234567890123";
    private String brn = "12345678901234567";
    private String uid = "12345678901";
    private PatientService patientService;


    @Before
    public void setup() throws ExecutionException, InterruptedException {
        dto = new PatientDto();
        dto.setNationalId(nid);
        dto.setBirthRegistrationNumber(brn);
        dto.setUid(uid);
        initMocks(this);

        patientService = new PatientService(patientRepository, null, null);
    }

    @Test
    public void shouldCreatePatient_WhenNoMatchingIdInDB() throws ExecutionException, InterruptedException {
        when(patientRepository.findHealthId(nid, brn, uid)).thenReturn("");
        patientService.createOrUpdate(dto);

        verify(patientRepository).findHealthId(nid, brn, uid);
        verify(patientRepository).create(dto);
    }

    @Test
    public void shouldUpdatePatient_WhenMatchingIdInDB() throws ExecutionException, InterruptedException {
        String hid = "h0000";
        when(patientRepository.findHealthId(nid, brn, uid)).thenReturn(hid);
        patientService.createOrUpdate(dto);

        verify(patientRepository).findHealthId(nid, brn, uid);
        verify(patientRepository).update(dto, hid);
    }
}