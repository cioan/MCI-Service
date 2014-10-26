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
import org.sharedhealth.mci.web.mapper.PatientMapper;
import org.sharedhealth.mci.web.mapper.SearchQuery;
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

    private PatientMapper patientMapper;
    private Location location;
    private MockMvc mockMvc;
    private String nationalId = "1234567890123";
    private String birthRegistrationNumber = "12345678901234567";
    private String fullname = "Scott Tiger";
    private String uid = "11111111111";
    public static final String API_END_POINT = "/api/v1/patients";
    public static final String PUT_API_END_POINT = "/api/v1/patients/{healthId}";
    public static final String GEO_CODE = "1004092001";
    private SearchQuery searchQuery;
    private StringBuilder stringBuilder;
    private List<PatientMapper> patientMappers;
    private int maxLimit;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PatientController(patientService))
                .setValidator(validator())
                .build();

        patientMapper = new PatientMapper();
        patientMapper.setNationalId(nationalId);
        patientMapper.setBirthRegistrationNumber(birthRegistrationNumber);
        patientMapper.setGivenName("Scott");
        patientMapper.setSurName("Tiger");
        patientMapper.setGender("M");
        patientMapper.setDateOfBirth("2014-12-01");

        Address address = new Address();
        address.setAddressLine("house-10");
        address.setDivisionId("10");
        address.setDistrictId("04");
        address.setUpazillaId("09");
        address.setCityCorporationId("20");
        address.setVillage("10");
        address.setWardId("01");
        address.setCountryCode("050");

        patientMapper.setAddress(address);

        location = new Location();

        location.setGeoCode(GEO_CODE);
        location.setDivisionId("10");
        location.setDistrictId("04");
        location.setUpazillaId("09");
        location.setPaurashavaId("20");
        location.setUnionId("01");

        searchQuery = new SearchQuery();
        stringBuilder = new StringBuilder(200);
        patientMappers = new ArrayList<>();
        maxLimit = 25;
    }

    @Test
    public void shouldCreatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String healthId = "healthId-100";
        MCIResponse mciResponse = new MCIResponse(healthId, CREATED);
        when(locationService.findByGeoCode(GEO_CODE)).thenReturn(new PreResolvedListenableFuture<>(location));
        when(patientService.create(patientMapper)).thenReturn(new PreResolvedListenableFuture<>(mciResponse));

        mockMvc.perform(post(API_END_POINT).content(json).contentType(APPLICATION_JSON))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciResponse, CREATED)));
        verify(patientService).create(patientMapper);
    }

    @Test
    public void shouldFindPatientByHealthId() throws Exception {
        String healthId = "healthId-100";
        when(patientService.findByHealthId(healthId)).thenReturn(new PreResolvedListenableFuture<>(patientMapper));
        mockMvc.perform(get(API_END_POINT + "/" + healthId))
                .andExpect(request().asyncResult(new ResponseEntity<>(patientMapper, OK)));
        verify(patientService).findByHealthId(healthId);
    }

    @Test
    public void shouldFindPatientsByNationalId() throws Exception {
        searchQuery.setNid(nationalId);
        stringBuilder.append("nid=" + nationalId);
        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByBirthRegistrationNumber() throws Exception {
        searchQuery.setBin_brn(birthRegistrationNumber);
        stringBuilder.append("bin_brn=" + birthRegistrationNumber);
        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByUid() throws Exception {
        searchQuery.setUid(uid);
        stringBuilder.append("uid=" + uid);
        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByName() throws Exception {
        searchQuery.setFull_name(fullname);
        stringBuilder.append("full_name=" + fullname);
        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddress() throws Exception {
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchQuery.setPresent_address(address);
        stringBuilder.append("present_address=" + address);
        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndUid() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchQuery.setPresent_address(address);
        searchQuery.setUid(uid);
        stringBuilder.append("uid=" + uid);
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndShowNoteForMoreRecord() throws Exception {

        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchQuery.setPresent_address(address);
        stringBuilder.append("present_address=" + address);

        patientMappers.add(patientMapper);
        patientMappers.add(patientMapper);
        patientMappers.add(patientMapper);
        maxLimit = 4;

        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    private void assertFindAllBy(SearchQuery searchQuery, String queryString) throws Exception {
        patientMappers.add(patientMapper);

        searchQuery.setMaximum_limit(maxLimit);

        when(patientService.getPerPageMaximumLimit()).thenReturn(maxLimit);
        when(patientService.getPerPageMaximumLimitNote()).thenReturn("There are more record for this search criteria. Please narrow down your search");

        final int limit = patientService.getPerPageMaximumLimit();
        final String note = patientService.getPerPageMaximumLimitNote();
        HashMap<String, String> additionalInfo = new HashMap<>();
        if (patientMappers.size() > limit) {
            patientMappers.remove(limit);
            additionalInfo.put("note", note);
        }

        when(patientService.findAllByQuery(searchQuery)).thenReturn(new PreResolvedListenableFuture<>(patientMappers));
        MCIMultiResponse mciMultiResponse = new MCIMultiResponse<>(patientMappers, additionalInfo, OK);

        mockMvc.perform(get(API_END_POINT + "?" + queryString))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject)));

        verify(patientService).findAllByQuery(searchQuery);
    }

    @Test
    public void shouldUpdatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String healthId = "healthId-100";
        MCIResponse mciResponse = new MCIResponse(healthId, ACCEPTED);
        when(locationService.findByGeoCode(GEO_CODE)).thenReturn(new PreResolvedListenableFuture<>(location));
        when(patientService.update(patientMapper, healthId)).thenReturn(new PreResolvedListenableFuture<>(mciResponse));

        mockMvc.perform(put(PUT_API_END_POINT, healthId).content(json).contentType(APPLICATION_JSON))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciResponse, ACCEPTED)));
        verify(patientService).update(patientMapper, healthId);

    }

    @Test
    public void shouldFindPatientsByAddressAndSurName() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchQuery.setPresent_address(address);
        searchQuery.setSur_name(patientMapper.getSurName());
        stringBuilder.append("sur_name=" + patientMapper.getSurName());
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    @Test
    public void shouldFindPatientsByAddressAndGivenName() throws Exception {
        StringBuilder stringBuilder = new StringBuilder(200);
        String address = location.getDivisionId() + location.getDistrictId() + location.getUpazillaId();
        searchQuery.setPresent_address(address);
        searchQuery.setGiven_name(patientMapper.getGivenName());
        stringBuilder.append("given_name=" + patientMapper.getGivenName());
        stringBuilder.append("&present_address=" + address);

        assertFindAllBy(searchQuery, stringBuilder.toString());
    }

    private LocalValidatorFactoryBean validator() {
        return localValidatorFactoryBean;
    }
}

