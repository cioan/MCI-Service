package org.sharedhealth.mci.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sharedhealth.mci.web.config.WebMvcConfig;
import org.sharedhealth.mci.web.handler.ErrorHandler;
import org.sharedhealth.mci.web.handler.MCIError;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.PhoneNumber;
import org.sharedhealth.mci.web.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMvcConfig.class)
public class PatientRestApiTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Mock
    private LocationService locationService;

    @Autowired
    @Qualifier("MCICassandraTemplate")
    private CassandraTemplate cqlTemplate;

    private MockMvc mockMvc;
    private PatientDto patientDto;
    public static final String API_END_POINT = "/api/v1/patients";
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        patientDto = new PatientDto();
        patientDto.setGivenName("Scott");
        patientDto.setSurName("Tiger");
        patientDto.setGender("M");
        patientDto.setDateOfBirth("2014-12-01");
        patientDto.setEducationLevel("01");
        patientDto.setOccupation("02");
        patientDto.setMaritalStatus("1");

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("1716528608");
        phoneNumber.setAreaCode("880");
        phoneNumber.setExtension("02");
        phoneNumber.setAreaCode("01");

        patientDto.setPhoneNumber(phoneNumber);
        patientDto.setPrimaryContactNumber(phoneNumber);

        Address address = new Address();
        address.setAddressLine("house-12");
        address.setDivisionId("10");
        address.setDistrictId("04");
        address.setUpazillaId("09");
        address.setCityCorporationId("20");
        address.setVillage("10");
        address.setWardId("01");

        patientDto.setAddress(address);
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();
    }

    @Test
    public void shouldReturnBadRequestForInvalidRequestData() throws Exception {
        patientDto.getAddress().setAddressLine("h");
        String json = new ObjectMapper().writeValueAsString(patientDto);
        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1002,\"field\":\"present_address.address_line\",\"message\":\"invalid present_address.address_line\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestWithErrorDetailsForMultipleInvalidRequestData() throws Exception {
        patientDto.getAddress().setAddressLine("h");
        patientDto.setGender("0");
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler errorHandler = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        List<MCIError> errorInfoErrors = errorHandler.getErrors();
        Collections.sort(errorInfoErrors);

        Assert.assertEquals(2, errorInfoErrors.size());
        Assert.assertEquals(1002, errorInfoErrors.get(0).getCode());
        Assert.assertEquals(1004, errorInfoErrors.get(1).getCode());
    }

    @Test
    public void shouldReturnBadRequestForInvalidJson() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content("invalidate" + json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":2000,\"http_status\":400,\"message\":\"invalid.request\",\"errors\":[{\"code\":2001,\"message\":\"invalid.json\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfPresentAddressIsNull() throws Exception {
        patientDto.setAddress(null);
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1001,\"field\":\"present_address\",\"message\":\"invalid present_address\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestForInvalidDataProperty() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new InvalidPatient());

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":2000,\"http_status\":400,\"message\":\"invalid.request\",\"errors\":[{\"code\":2002,\"field\":\"invalid_property\",\"message\":\"Unrecognized field: 'invalid_property'\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void ShouldPassIFAddressIsValidTillUpazilaLevel() throws Exception {

        patientDto.getAddress().setWardId(null);
        patientDto.getAddress().setCityCorporationId(null);
        patientDto.getAddress().setUnionId(null);

        String json = new ObjectMapper().writeValueAsString(patientDto);

        createPatient(json);

    }

    @Test
    public void shouldReturnNotFoundResponseIfPatientNotExistForUpdate() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(put(API_END_POINT + "/health-1000").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assert.assertEquals("{\"http_status\":404,\"message\":\"patient.not.found\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnNotFoundResponseIfHIDNotMatchWithUrlHid() throws Exception {
        patientDto.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientDto);

        mockMvc.perform(put(API_END_POINT + "/health-1000").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("patient.not.found")))
                .andReturn();
    }

    @Test
    public void shouldReturnBadRequestIfOnlySurNameGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(get(API_END_POINT + "?surname=Tiger").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfOnlyGivenNameGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(get(API_END_POINT + "?given_name=Tiger").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithSurNameAndAddress() throws Exception {
        patientDto.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientDto);
        Address address = patientDto.getAddress();
        String surName = "Mazumder";
        String url = API_END_POINT + "?surname=" + surName + "&division_id=" + address.getDivisionId()
                + "&district_id=" + address.getDistrictId() + "&upazila_id=" + address.getUpazillaId();

        mockMvc.perform(get(url)
                .accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithGivenNameAndAddress() throws Exception {
        patientDto.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientDto);
        String givenName = "Raju";
        Address address = patientDto.getAddress();
        String url = API_END_POINT + "?given_name=" + givenName + "&division_id=" + address.getDivisionId()
                + "&district_id=" + address.getDistrictId() + "&upazila_id=" + address.getUpazillaId();

        MvcResult result = mockMvc.perform(get(url)
                .accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnAllTheCreatedPatientFieldAfterGetAPICall() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        Map map = new ObjectMapper().readValue(createPatient(json).getResponse().getContentAsString(), new TypeReference<HashMap<String, String>>() {
        });

        String healthId = (String) map.get("id");
        mockMvc.perform(get(API_END_POINT + "/" + healthId).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marital_status", is("1")))
                .andExpect(jsonPath("$.gender", is("M")))
                .andReturn();
    }

    @Test
    public void shouldReturnBadRequestIfOnlyExtensionOrCountryCodeOrAreaCodeGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?country_code=880&area_code=02&extension=122").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfPresentAddressNotGivenWithPhoneNumber() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?phone_number=1716528608").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfOnlyCountryCodeGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?country_code=880").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithPhoneNumber() throws Exception {
        patientDto.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientDto);
        Address address = patientDto.getAddress();
        String url = API_END_POINT + "?phone_number=123456&country_code=880&division_id=" + address.getDivisionId()
                + "&district_id=" + address.getDistrictId() + "&upazila_id=" + address.getUpazillaId();

        mockMvc.perform(get(url)
                .accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnAllTheCreatedPatientIfPhoneNumberMatchBySearch() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientDto);
        createPatient(json);
        createPatient(json);
        createPatient(json);

        Address address = patientDto.getAddress();
        String url = API_END_POINT + "?phone_number=1716528608&division_id=" + address.getDivisionId()
                + "&district_id=" + address.getDistrictId() + "&upazila_id=" + address.getUpazillaId();

        mockMvc.perform(get(url)
                .accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].phone_number.number", is("1716528608")))
                .andReturn();
    }

    private MvcResult createPatient(String json) throws Exception {
        return mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andReturn();
    }

    @After
    public void teardown() {
        cqlTemplate.execute("truncate patient");
    }

    private class InvalidPatient {

        @JsonProperty("nid")
        public String nationalId = "1234567890123";

        @JsonProperty("invalid_property")
        public String birthRegistrationNumber = "some thing";
    }
}
