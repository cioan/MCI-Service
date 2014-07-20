package org.sharedhealth.mci.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sharedhealth.mci.web.config.EnvironmentMock;
import org.sharedhealth.mci.web.config.WebMvcConfig;
import org.sharedhealth.mci.web.model.Address;
import org.sharedhealth.mci.web.model.Patient;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(initializers = EnvironmentMock.class, classes = WebMvcConfig.class)
public class PatientRestApiTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Patient patient;
    public static final String API_END_POINT = "/api/v1/patients";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        patient = new Patient();
        patient.setFirstName("Scott");
        patient.setLastName("Tiger");
        patient.setGender("1");
        patient.setDateOfBirth("2014-12-01");
        patient.setEducationLevel("01");
        patient.setOccupation("02");
        patient.setFathersFirstName("Bob");

        Address address = new Address();
        address.setAddressLine("house-10");
        address.setDivisionId("10");
        address.setDistrictId("1020");
        address.setUpazillaId("102030");
        address.setUnionId("10203040");
        address.setVillage("10");
        address.setWard("10");
        address.setCountry("103");
        patient.setAddress(address);
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        String json = new ObjectMapper().writeValueAsString(patient);

        MvcResult result = mockMvc.perform(post(API_END_POINT).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnBadRequestForInvalidRequestData() throws Exception {
        patient.getAddress().setAddressLine("h");
        String json = new ObjectMapper().writeValueAsString(patient);

        MvcResult result = mockMvc.perform(post(API_END_POINT).accept(APPLICATION_JSON).content(json).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assert.assertEquals("{\"code\":400,\"message\":\"invalid.request\",\"errors\":[{\"code\":2002,\"message\":\"Invalid address.addressLine\"}]}", result.getResponse().getContentAsString());
    }
}
