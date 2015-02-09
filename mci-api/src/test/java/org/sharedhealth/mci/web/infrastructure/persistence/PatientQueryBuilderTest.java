package org.sharedhealth.mci.web.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.sharedhealth.mci.web.mapper.Address;
import org.sharedhealth.mci.web.mapper.PatientData;

import static org.junit.Assert.*;
import static org.sharedhealth.mci.utils.FileUtil.asString;

public class PatientQueryBuilderTest {

    @Test
    public void shouldReturnTrueIfSomeLoggableDataChanged() throws Exception {

        PatientData p = new PatientData();
        p.setSurName("S");
        assertTrue(PatientQueryBuilder.someLoggableDataChanged(p));

        p = new PatientData();
        p.setGivenName("G");
        assertTrue(PatientQueryBuilder.someLoggableDataChanged(p));

        p = new PatientData();
        p.setConfidential("Yes");
        assertTrue(PatientQueryBuilder.someLoggableDataChanged(p));

        p = new PatientData();
        p.setGender("M");
        assertTrue(PatientQueryBuilder.someLoggableDataChanged(p));

        p = new PatientData();
        Address newAddress = new Address("99", "88", "77");
        p.setAddress(newAddress);
        assertTrue(PatientQueryBuilder.someLoggableDataChanged(p));
    }

    @Test
    public void shouldReturnFalseIfNoLoggableDataChanged() throws Exception {
        String json = asString("jsons/patient/full_payload.json");

        PatientData p  = getPatientObjectFromString(json);
        p.setSurName(null);
        p.setGivenName(null);
        p.setGender(null);
        p.setConfidential(null);
        p.setAddress(null);
        assertFalse(PatientQueryBuilder.someLoggableDataChanged(p));
    }

    protected PatientData getPatientObjectFromString(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, PatientData.class);
    }
}