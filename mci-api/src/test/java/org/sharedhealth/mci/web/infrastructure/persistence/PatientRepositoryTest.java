package org.sharedhealth.mci.web.infrastructure.persistence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.cassandra.core.CassandraTemplate;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.*;

public class PatientRepositoryTest {

    @Mock
    private CassandraTemplate template;
    private PatientRepository patientRepository;
    private String nid = "1234567890123";
    private String brn = "12345678901234567";
    private String uid = "12345678901";

    @Before
    public void setup() {
        initMocks(this);
        patientRepository = new PatientRepository(template, null);
    }

    @Test
    public void shouldReturnHealthId_ByComparingNidAndBrn() {
        when(template.select(buildFindHidByNidQuery(nid), String.class)).thenReturn(asList("h1", "h2", "h3"));
        when(template.select(buildFindHidByBrnQuery(brn), String.class)).thenReturn(asList("h2", "h3", "h4"));

        String hid = patientRepository.findHealthId(nid, brn, uid);
        verify(template, never()).select(buildFindHidByUidQuery(uid), String.class);
        assertTrue(asList("h2", "h3").contains(hid));
    }

    @Test
    public void shouldReturnHealthId_ByComparingNidBrnAndUid() {
        when(template.select(buildFindHidByNidQuery(nid), String.class)).thenReturn(asList("h1"));
        when(template.select(buildFindHidByBrnQuery(brn), String.class)).thenReturn(asList("h2"));
        when(template.select(buildFindHidByUidQuery(uid), String.class)).thenReturn(asList("h2"));

        String hid = patientRepository.findHealthId(nid, brn, uid);
        assertTrue("h2".equals(hid));
    }
}