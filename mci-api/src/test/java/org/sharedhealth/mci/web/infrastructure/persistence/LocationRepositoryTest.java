package org.sharedhealth.mci.web.infrastructure.persistence;

import org.junit.Before;
import org.mockito.Mock;
import org.springframework.data.cassandra.core.CassandraTemplate;

import static org.mockito.MockitoAnnotations.initMocks;

public class LocationRepositoryTest {

    @Mock
    private CassandraTemplate template;
    private LocationRepository locationRepository;

    @Before
    public void setUp() {
        initMocks(this);

    }

//    @Test
//    public void testFindByGeoCode()  {
//        String geoCode = "1234";
//        Location location = new Location();
//        ResultSet resultSet = null;
//
//        when(template.query(anyString())).thenReturn(resultSet);
//        locationRepository.findByGeoCode(geoCode);
//        verify(template).query(anyString());
//
//    }
}