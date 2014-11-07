package org.sharedhealth.mci.web.infrastructure.persistence;

import com.datastax.driver.core.ResultSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sharedhealth.mci.web.mapper.Location;
import org.springframework.data.cassandra.core.CassandraTemplate;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationRepositoryTest {

    @Mock
    private CassandraTemplate template;
    private LocationRepository locationRepository;

    @Before
    public  void setUp(){
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