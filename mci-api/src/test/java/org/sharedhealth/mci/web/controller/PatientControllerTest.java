package org.sharedhealth.mci.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.Location;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.sharedhealth.mci.web.service.LocationService;
import org.sharedhealth.mci.web.service.PatientService;
import org.sharedhealth.mci.web.service.SettingService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private PatientController controller;

    private PatientDto patientDto;
    private Location location;
    private MockMvc mockMvc;
    private String nationalId = "1234567890123";
    private String birthRegistrationNumber = "12345678901234567";
    private String uid = "11111111111";
    public static final String API_END_POINT = "/api/v1/patients";
    public static final String PUT_API_END_POINT = "/api/v1/patients/{healthId}";
    public static final String GEO_CODE = "1004092001";
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

        maxLimit = 25;
    }

    @Test
    public void shouldCreatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        String healthId = "healthId-100";
        MCIResponse mciResponse = new MCIResponse(healthId, CREATED);
        when(locationService.findByGeoCode(GEO_CODE)).thenReturn(location);
        when(patientService.createOrUpdate(patientDto)).thenReturn(mciResponse);

        mockMvc.perform(post(API_END_POINT).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("healthId-100")));
        verify(patientService).createOrUpdate(patientDto);
    }

    @Test
    public void shouldFindPatientByHealthId() throws Exception {
        String healthId = "healthId-100";
        when(patientService.findByHealthId(healthId)).thenReturn(patientDto);
        mockMvc.perform(get(API_END_POINT + "/" + healthId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nid", is(nationalId)))
                .andExpect(jsonPath("$.bin_brn", is(birthRegistrationNumber)))
                .andExpect(jsonPath("$.given_name", is("Scott")))
                .andExpect(jsonPath("$.sur_name", is("Tiger")))
                .andExpect(jsonPath("$.date_of_birth", is("2014-12-01")))
                .andExpect(jsonPath("$.present_address.address_line", is("house-10")));
        verify(patientService).findByHealthId(healthId);
    }

    @Test
    public void shouldFindPatientsByNationalId() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setNid(nationalId);
        assertSearch(criteria, "?nid=" + nationalId);
    }

    @Test
    public void shouldFindPatientsByBrn() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setBrn(birthRegistrationNumber);
        assertSearch(criteria, "?brn=" + birthRegistrationNumber);
    }

    @Test
    public void shouldFindPatientsByUid() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUid(uid);
        assertSearch(criteria, "?uid=" + uid);
    }

    @Test
    public void shouldFindPatientsByAddressAndName() throws Exception {
        String divisionId = location.getDivisionId();
        String districtId = location.getDistrictId();
        String upazilaId = location.getUpazillaId();
        String givenName = patientDto.getGivenName();
        String surname = patientDto.getSurName();

        SearchCriteria criteria = new SearchCriteria();
        criteria.setDivision_id(divisionId);
        criteria.setDistrict_id(districtId);
        criteria.setUpazila_id(upazilaId);
        criteria.setGiven_name(givenName);
        criteria.setSurname(surname);

        String params = format("?division_id=%s&district_id=%s&upazila_id=%s&given_name=%s&surname=%s", divisionId, districtId, upazilaId, givenName, surname);
        assertSearch(criteria, params);
    }

    private LocalValidatorFactoryBean validator() {
        return localValidatorFactoryBean;
    }

    private void assertSearch(SearchCriteria criteria, String params) throws Exception {
        criteria.setMaximum_limit(maxLimit);

        when(patientService.getPerPageMaximumLimit()).thenReturn(maxLimit);
        when(patientService.getPerPageMaximumLimitNote()).thenReturn("There are more record for this search criteria. Please narrow down your search");
        when(patientService.findAll(criteria)).thenReturn(asList(patientDto));

        mockMvc.perform(get(API_END_POINT + params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].nid", is(nationalId)))
                .andExpect(jsonPath("$.results[0].bin_brn", is(birthRegistrationNumber)))
                .andExpect(jsonPath("$.results[0].given_name", is("Scott")))
                .andExpect(jsonPath("$.results[0].sur_name", is("Tiger")))
                .andExpect(jsonPath("$.results[0].date_of_birth", is("2014-12-01")))
                .andExpect(jsonPath("$.results[0].present_address.address_line", is("house-10")));
        verify(patientService).findAll(criteria);
    }

    @Test
    public void shouldUpdatePatientAndReturnHealthId() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        String healthId = "healthId-100";
        patientDto.setHealthId(healthId);
        when(patientService.createOrUpdate(patientDto)).thenReturn(new MCIResponse(healthId, ACCEPTED));

        mockMvc.perform(put(PUT_API_END_POINT, healthId).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isAccepted());
        verify(patientService).createOrUpdate(patientDto);

    }
}

