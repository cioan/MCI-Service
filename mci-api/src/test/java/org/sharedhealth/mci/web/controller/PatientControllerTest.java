package org.sharedhealth.mci.web.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sharedhealth.mci.web.handler.MCIMultiResponse;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.Location;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.sharedhealth.mci.web.service.LocationService;
import org.sharedhealth.mci.web.service.PatientService;
import org.sharedhealth.mci.web.service.SettingService;
import org.sharedhealth.mci.web.utils.concurrent.PreResolvedListenableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@RunWith(MockitoJUnitRunner.class)
public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @Mock
    private SettingService settingService;

    @Mock
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    private PatientDto patientDto;
    private Location location;
    private MockMvc mockMvc;
    private String nationalId = "1234567890123";
    private String birthRegistrationNumber = "12345678901234567";
    private String fullname = "Scott Tiger";
    private String uid = "11111111111";
    public static final String API_END_POINT = "/api/v1/patients";
    public static final String PUT_API_END_POINT = "/api/v1/patients/{healthId}";
    public static final String GEO_CODE = "1004092001";
    private SearchCriteria searchCriteria;
    private StringBuilder stringBuilder;
    private List<PatientDto> patientDtos;
    private int maxLimit;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PatientController(patientService))
                .setValidator(validator())
                .build();

        patientDto = new PatientDto();
        patientDto.setNationalId(nationalId);
        patientDto.setBirthRegistrationNumber(birthRegistrationNumber);
        patientDto.setGivenName("Scott");
        patientDto.setSurName("Tiger");
        patientDto.setGender("M");
        patientDto.setDateOfBirth("2014-12-01");

        Address address = new Address();
        address.setAddressLine("house-10");
        address.setDivisionId("10");
        address.setDistrictId("04");
        address.setUpazillaId("09");
        address.setCityCorporationId("20");
        address.setVillage("10");
        address.setWardId("01");
        address.setCountryCode("050");

        patientDto.setAddress(address);

        location = new Location();

        location.setGeoCode(GEO_CODE);
        location.setDivisionId("10");
        location.setDistrictId("04");
        location.setUpazillaId("09");
        location.setPaurashavaId("20");
        location.setUnionId("01");

        searchCriteria = new SearchCriteria();
        stringBuilder = new StringBuilder(200);
        patientDtos = new ArrayList<>();
        maxLimit = 25;
    }

    @Test
    public void shouldCreatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        String healthId = "healthId-100";
        MCIResponse mciResponse = new MCIResponse(healthId, CREATED);
        when(locationService.findByGeoCode(GEO_CODE)).thenReturn(new PreResolvedListenableFuture<>(location));
        when(patientService.createOrUpdate(patientDto)).thenReturn(mciResponse);

        mockMvc.perform(post(API_END_POINT).content(json).contentType(APPLICATION_JSON))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciResponse, CREATED)));
        verify(patientService).createOrUpdate(patientDto);
    }

    @Test
    public void shouldFindPatientByHealthId() throws Exception {
        String healthId = "healthId-100";
        when(patientService.findByHealthId(healthId)).thenReturn(new PreResolvedListenableFuture<>(patientDto));
        mockMvc.perform(get(API_END_POINT + "/" + healthId))
                .andExpect(request().asyncResult(new ResponseEntity<>(patientDto, OK)));
        verify(patientService).findByHealthId(healthId);
    }

    @Test
    public void shouldFindPatientsByNationalId() throws Exception {
        searchCriteria.setNid(nationalId);
        stringBuilder.append("nid=" + nationalId);
        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByBirthRegistrationNumber() throws Exception {
        searchCriteria.setBin_brn(birthRegistrationNumber);
        stringBuilder.append("bin_brn=" + birthRegistrationNumber);
        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByUid() throws Exception {
        searchCriteria.setUid(uid);
        stringBuilder.append("uid=" + uid);
        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByName() throws Exception {
        searchCriteria.setFull_name(fullname);
        stringBuilder.append("full_name=" + fullname);
        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddress() throws Exception {
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        stringBuilder.append("present_address=" + address);
        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndUid() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        searchCriteria.setUid(uid);
        stringBuilder.append("uid=" + uid);
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndShowNoteForMoreRecord() throws Exception {

        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        stringBuilder.append("present_address=" + address);

        patientDtos.add(patientDto);
        patientDtos.add(patientDto);
        patientDtos.add(patientDto);
        maxLimit = 4;

        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    private void assertFindAllBy(SearchCriteria searchCriteria, String queryString) throws Exception {
        patientDtos.add(patientDto);

        searchCriteria.setMaximum_limit(maxLimit);

        when(patientService.getPerPageMaximumLimit()).thenReturn(maxLimit);
        when(patientService.getPerPageMaximumLimitNote()).thenReturn("There are more record for this search criteria. Please narrow down your search");

        final int limit = patientService.getPerPageMaximumLimit();
        final String note = patientService.getPerPageMaximumLimitNote();
        HashMap<String, String> additionalInfo = new HashMap<>();
        if (patientDtos.size() > limit) {
            patientDtos.remove(limit);
            additionalInfo.put("note", note);
        }

        when(patientService.findAll(searchCriteria)).thenReturn(new PreResolvedListenableFuture<>(patientDtos));
        MCIMultiResponse mciMultiResponse = new MCIMultiResponse<>(patientDtos, additionalInfo, OK);

        mockMvc.perform(get(API_END_POINT + "?" + queryString))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject)));

        verify(patientService).findAll(searchCriteria);
    }

    @Test
    public void shouldUpdatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        String healthId = "healthId-100";
        MCIResponse mciResponse = new MCIResponse(healthId, ACCEPTED);
        when(locationService.findByGeoCode(GEO_CODE)).thenReturn(new PreResolvedListenableFuture<>(location));
        when(patientService.createOrUpdate(patientDto)).thenReturn(mciResponse);

        mockMvc.perform(put(PUT_API_END_POINT, healthId).content(json).contentType(APPLICATION_JSON))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciResponse, ACCEPTED)));
        verify(patientService).createOrUpdate(patientDto);

    }

    @Test
    public void shouldFindPatientsByAddressAndSurName() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        searchCriteria.setSur_name(patientDto.getSurName());
        stringBuilder.append("sur_name=" + patientDto.getSurName());
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndGivenName() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        searchCriteria.setGiven_name(patientDto.getGivenName());
        stringBuilder.append("given_name=" + patientDto.getGivenName());
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndSurNameAndShowNoteForMoreRecord() throws Exception {

        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchCriteria.setPresent_address(address);
        stringBuilder.append("present_address=" + address);
        stringBuilder.append("&sur_name=" + patientDto.getSurName());
        searchCriteria.setSur_name(patientDto.getSurName());

        patientDtos.add(patientDto);
        patientDtos.add(patientDto);
        patientDtos.add(patientDto);
        maxLimit = 4;

        assertFindAllBy(searchCriteria, stringBuilder.toString());
    }

    private LocalValidatorFactoryBean validator() {
        return localValidatorFactoryBean;
    }
}

