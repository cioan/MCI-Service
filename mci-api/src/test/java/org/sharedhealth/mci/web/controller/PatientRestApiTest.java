package org.sharedhealth.mci.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sharedhealth.mci.web.config.EnvironmentMock;
import org.sharedhealth.mci.web.config.WebMvcConfig;
import org.sharedhealth.mci.web.handler.ErrorHandler;
import org.sharedhealth.mci.web.handler.MCIError;
import org.sharedhealth.mci.web.handler.MCIMultiResponse;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.PatientMapper;
import org.sharedhealth.mci.web.mapper.PhoneNumber;
import org.sharedhealth.mci.web.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(initializers = EnvironmentMock.class, classes = WebMvcConfig.class)
public class PatientRestApiTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Mock
    private LocationService locationService;

    @Autowired
    @Qualifier("MCICassandraTemplate")
    private CassandraTemplate cqlTemplate;

    private MockMvc mockMvc;
    private PatientMapper patientMapper;
    public static final String API_END_POINT = "/api/v1/patients";
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        patientMapper = new PatientMapper();
        patientMapper.setGivenName("Scott");
        patientMapper.setSurName("Tiger");
        patientMapper.setGender("M");
        patientMapper.setDateOfBirth("2014-12-01");
        patientMapper.setEducationLevel("01");
        patientMapper.setOccupation("02");
        patientMapper.setMaritalStatus("1");

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("1716528608");
        phoneNumber.setCountryCode("880");
        phoneNumber.setExtension("02");
        phoneNumber.setAreaCode("01");

        patientMapper.setPhoneNumber(phoneNumber);
        patientMapper.setPrimaryContactNumber(phoneNumber);

        Address address = new Address();
        address.setAddressLine("house-12");
        address.setDivisionId("10");
        address.setDistrictId("04");
        address.setUpazillaId("09");
        address.setCityCorporationId("20");
        address.setVillage("10");
        address.setWardId("01");
        address.setCountryCode("050");

        patientMapper.setAddress(address);
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = createPatient(json);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnBadRequestForInvalidRequestData() throws Exception {
        patientMapper.getAddress().setAddressLine("h");
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1002,\"field\":\"present_address.address_line\",\"message\":\"invalid present_address.address_line\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestWithErrorDetailsForMultipleInvalidRequestData() throws Exception {
        patientMapper.getAddress().setAddressLine("h");
        patientMapper.setGender("0");
        String json = new ObjectMapper().writeValueAsString(patientMapper);

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
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content("invalidate" + json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":2000,\"http_status\":400,\"message\":\"invalid.request\",\"errors\":[{\"code\":2001,\"message\":\"invalid.json\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfPresentAddressIsNull() throws Exception {
        patientMapper.setAddress(null);
        String json = new ObjectMapper().writeValueAsString(patientMapper);

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

        patientMapper.getAddress().setWardId(null);
        patientMapper.getAddress().setCityCorporationId(null);
        patientMapper.getAddress().setUnionId(null);

        String json = new ObjectMapper().writeValueAsString(patientMapper);

        createPatient(json);

    }

    @Test
    public void shouldReturnNotFoundResponseIfPatientNotExistForUpdate() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(put(API_END_POINT + "/health-1000").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assert.assertEquals("{\"http_status\":404,\"message\":\"patient.not.found\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnNotFoundResponseIfHIDNotMatchWithUrlHid() throws Exception {
        patientMapper.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(put(API_END_POINT + "/health-1000").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1004,\"field\":\"hid\",\"message\":\"invalid hid\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfOnlySurNameGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(get(API_END_POINT + "?sur_name=Tiger").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfOnlyGivenNameGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(get(API_END_POINT + "?given_name=Tiger").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithSurNameAndAddress() throws Exception {
        patientMapper.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String present_address = patientMapper.getAddress().getDivisionId() +
                patientMapper.getAddress().getDistrictId() + patientMapper.getAddress().getUpazillaId();
        String surName = "Mazumder";
        MvcResult result = mockMvc.perform(get(API_END_POINT + "?sur_name=" + surName + "&present_address=" + present_address).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final MCIMultiResponse body = getMciMultiResponse(result);
        Assert.assertEquals("[]", body.getResults().toString());
        Assert.assertEquals(200, body.getHttpStatus());
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithGivenNameAndAddress() throws Exception {
        patientMapper.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String present_address = patientMapper.getAddress().getDivisionId() +
                patientMapper.getAddress().getDistrictId() + patientMapper.getAddress().getUpazillaId();
        String surName = "Raju";
        MvcResult result = mockMvc.perform(get(API_END_POINT + "?given_name=" + surName + "&present_address=" + present_address).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final MCIMultiResponse body = getMciMultiResponse(result);
        Assert.assertEquals("[]", body.getResults().toString());
        Assert.assertEquals(200, body.getHttpStatus());
    }

    @Test
    public void shouldReturnAllTheCreatedPatientFieldAfterGetAPICall() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = createPatient(json);

        final MCIResponse body = getMciResponse(result);
        String healthId = body.getId();

        MvcResult getResult = mockMvc.perform(get(API_END_POINT + "/" + healthId).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final ResponseEntity asyncResult = (ResponseEntity<PatientMapper>) getResult.getAsyncResult();
        final PatientMapper getBody = (PatientMapper) asyncResult.getBody();

        Assert.assertEquals("1", getBody.getMaritalStatus());
        Assert.assertEquals("M", getBody.getGender());
    }

    @Test
    public void shouldReturnBadRequestIfOnlyExtensionOrCountryCodeOrAreaCodeGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?country_code=880&area_code=02&extension=122").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfPresentAddressNotGivenWithPhoneNumber() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?phone_number=1716528608").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnBadRequestIfOnlyCountryCodeGiven() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?country_code=880").accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"error_code\":1000,\"http_status\":400,\"message\":\"validation error\",\"errors\":[{\"code\":1006,\"message\":\"Invalid search parameter\"}]}", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnOkResponseIfPatientNotExistWithPhoneNumber() throws Exception {
        patientMapper.setHealthId("health-100");
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String present_address = patientMapper.getAddress().getDivisionId() +
                patientMapper.getAddress().getDistrictId() + patientMapper.getAddress().getUpazillaId();
        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?phone_no=123456&country_code=880&present_address="+present_address).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final MCIMultiResponse body = getMciMultiResponse(result);
        Assert.assertEquals("[]", body.getResults().toString());
        Assert.assertEquals(200, body.getHttpStatus());
    }

    @Test
    public void shouldReturnAllTheCreatedPatientIfPhoneNumberMatchBySearch() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patientMapper);
        String present_address = patientMapper.getAddress().getDivisionId() +
                patientMapper.getAddress().getDistrictId() + patientMapper.getAddress().getUpazillaId();
        createPatient(json);
        createPatient(json);
        createPatient(json);

        MvcResult result = mockMvc.perform(get(API_END_POINT +
                "?phone_no=1716528608&country_code=880&present_address="+present_address).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final MCIMultiResponse body = getMciMultiResponse(result);
        PatientMapper patientMapper1 = (PatientMapper)body.getResults().get(0);
        Assert.assertEquals("1716528608", patientMapper1.getPhoneNumber().getNumber());
        Assert.assertEquals(200, body.getHttpStatus());
    }

    private MvcResult createPatient(String json) throws Exception {
        return mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MCIMultiResponse getMciMultiResponse(MvcResult result) {
        final ResponseEntity asyncResult = (ResponseEntity<MCIMultiResponse>) result.getAsyncResult();

        return (MCIMultiResponse) asyncResult.getBody();
    }

    private MCIResponse getMciResponse(MvcResult result) {
        final ResponseEntity asyncResult = (ResponseEntity<MCIResponse>) result.getAsyncResult();

        return (MCIResponse) asyncResult.getBody();
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
