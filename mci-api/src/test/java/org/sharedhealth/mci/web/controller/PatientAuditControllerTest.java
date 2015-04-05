package org.sharedhealth.mci.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sharedhealth.mci.web.infrastructure.security.TokenAuthentication;
import org.sharedhealth.mci.web.infrastructure.security.UserInfo;
import org.sharedhealth.mci.web.infrastructure.security.UserProfile;
import org.sharedhealth.mci.web.mapper.PatientAuditLogData;
import org.sharedhealth.mci.web.mapper.PatientData;
import org.sharedhealth.mci.web.mapper.Requester;
import org.sharedhealth.mci.web.service.PatientAuditService;
import org.sharedhealth.mci.web.service.PatientService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.text.ParseException;
import java.util.*;

import static com.datastax.driver.core.utils.UUIDs.timeBased;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.sharedhealth.mci.web.utils.JsonConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PatientAuditControllerTest {

    private static final String API_END_POINT = "/api/v1/audit/patients/{healthId}";

    @Mock
    private PatientService patientService;
    @Mock
    private PatientAuditService auditService;
    @Mock
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    private MockMvc mockMvc;

    @Before
    public void setup() throws ParseException {
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PatientAuditController(patientService, auditService))
                .setValidator(localValidatorFactoryBean)
                .build();

        SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(getUserInfo(), true));

    }

    private UserInfo getUserInfo() {
        UserProfile userProfile = new UserProfile("facility", "100067", null);

        return new UserInfo("102", "ABC", "abc@mail", 1, true, "111100",
                new ArrayList<String>(), asList(userProfile));
    }

    @Test
    public void shouldFindByHealthId() throws Exception {
        String healthId = "h100";
        PatientData patient = new PatientData();
        patient.setRequester("Bahmni", "Dr. Monika");
        patient.setCreatedAt(timeBased());
        when(patientService.findByHealthId(healthId)).thenReturn(patient);

        String eventTime = "2015-01-02T10:20:30Z";
        when(auditService.findByHealthId(healthId)).thenReturn(buildAuditLogs(eventTime));

        MvcResult mvcResult = mockMvc.perform(get(API_END_POINT, healthId).contentType(APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.created_by", is(patient.getCreatedBy())))
                .andExpect(jsonPath("$.created_at", is(patient.getCreatedAtAsString())))

                .andExpect(jsonPath("$.updates.[0].event_time", is(eventTime)))

                .andExpect(jsonPath("$.updates.[0].requested_by.given_name.facility.id", is(asList("Bahmni1"))))
                .andExpect(jsonPath("$.updates.[0].requested_by.occupation.facility.id", is(asList("Bahmni2"))))
                .andExpect(jsonPath("$.updates.[0].requested_by.edu_level.facility.id", is(asList("Bahmni3"))))

                .andExpect(jsonPath("$.updates.[0].approved_by", is("admin")))

                .andExpect(jsonPath("$.updates.[0].change_set.given_name", is(notNullValue())))
                .andExpect(jsonPath("$.updates.[0].change_set.given_name.old_value", is("Harry")))
                .andExpect(jsonPath("$.updates.[0].change_set.given_name.new_value", is("Potter")))

                .andExpect(jsonPath("$.updates.[0].change_set.occupation", is(notNullValue())))
                .andExpect(jsonPath("$.updates.[0].change_set.occupation.old_value", is("Wizard")))
                .andExpect(jsonPath("$.updates.[0].change_set.occupation.new_value", is("Jobless")))

                .andExpect(jsonPath("$.updates.[0].change_set.edu_level", is(notNullValue())))
                .andExpect(jsonPath("$.updates.[0].change_set.edu_level.old_value", is("Std 12")))
                .andExpect(jsonPath("$.updates.[0].change_set.edu_level.new_value", is("Std 10")));

        verify(patientService).findByHealthId(healthId);
        verify(auditService).findByHealthId(healthId);
    }

    private List<PatientAuditLogData> buildAuditLogs(String eventTime) {
        List<PatientAuditLogData> logs = new ArrayList<>();
        PatientAuditLogData log = new PatientAuditLogData();
        log.setEventTime(eventTime);
        log.setChangeSet(buildChangeSets());
        log.setApprovedBy("admin");
        log.addRequestedBy(GIVEN_NAME, buildRequesters("Bahmni1"));
        log.addRequestedBy(OCCUPATION, buildRequesters("Bahmni2"));
        log.addRequestedBy(EDU_LEVEL, buildRequesters("Bahmni3"));
        logs.add(log);
        return logs;
    }

    private Set<Requester> buildRequesters(String facilityId) {
        Set<Requester> requesters = new HashSet<>();
        Requester facility = new Requester(facilityId);
        requesters.add(facility);
        return requesters;
    }

    private Map<String, Map<String, Object>> buildChangeSets() {
        Map<String, Map<String, Object>> changeSets = new HashMap<>();
        changeSets.put(GIVEN_NAME, buildChangeSet("Harry", "Potter"));
        changeSets.put(OCCUPATION, buildChangeSet("Wizard", "Jobless"));
        changeSets.put(EDU_LEVEL, buildChangeSet("Std 12", "Std 10"));
        return changeSets;
    }

    private Map<String, Object> buildChangeSet(Object oldValue, Object newValue) {
        Map<String, Object> changeSet = new HashMap<>();
        changeSet.put(OLD_VALUE, oldValue);
        changeSet.put(NEW_VALUE, newValue);
        return changeSet;
    }
}