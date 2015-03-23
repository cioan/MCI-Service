package org.sharedhealth.mci.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sharedhealth.mci.web.handler.MCIMultiResponse;
import org.sharedhealth.mci.web.mapper.*;
import org.sharedhealth.mci.web.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.text.ParseException;
import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @Mock
    private LocalValidatorFactoryBean localValidatorFactoryBean;


    private MockMvc mockMvc;
    public static final String API_END_POINT = "/api/v1/locations";
    private LocationCriteria locationCriteria;
    private List<LocationData> locations;

    @Before
    public void setup() throws ParseException {
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new LocationController(locationService))
                .setValidator(validator())
                .build();

        locationCriteria = new LocationCriteria();

    }

    @Test
    public void shouldFindAllDivisionsIfLevelAbsent() throws Exception {

        buildLocationData();
        when(locationService.findLocationsByParent(locationCriteria)).thenReturn(this.locations);

        HashMap<String, String> additionalInfo = new HashMap<>();

        MCIMultiResponse mciMultiResponse = new MCIMultiResponse(locations, additionalInfo, OK);

        mockMvc.perform(get(API_END_POINT))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject)));

        verify(locationService).findLocationsByParent(locationCriteria);

    }

    @Test
    public void shouldFindLocationsByLevel() throws Exception {

        List<LocationData> locations = new ArrayList<>();
        buildLocationData();
        locationCriteria.setParent("10");
        when(locationService.findLocationsByParent(locationCriteria)).thenReturn(locations);

        HashMap<String, String> additionalInfo = new HashMap<>();

        MCIMultiResponse mciMultiResponse = new MCIMultiResponse(locations, additionalInfo, OK);

        mockMvc.perform(get(API_END_POINT + "?parent=10"))
                .andExpect(request().asyncResult(new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject)));

        verify(locationService).findLocationsByParent(locationCriteria);

    }

    private LocalValidatorFactoryBean validator() {
        return localValidatorFactoryBean;
    }

    private List<LocationData> buildLocationData()
    {
        locations = new ArrayList<>();

        LocationData data = new LocationData();
        data.setCode("10");
        data.setName("Dhaka");
        data.setParent("00");

        LocationData data1 = new LocationData();
        data.setCode("10");
        data.setName("Dhaka");
        data.setParent("00");

        locations.add(data);
        locations.add(data1);

        return locations;
    }


}

