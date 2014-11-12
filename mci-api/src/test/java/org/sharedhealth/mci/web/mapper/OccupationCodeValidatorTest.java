package org.sharedhealth.mci.web.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sharedhealth.mci.web.config.WebMvcConfigTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMvcConfigTest.class)
public class OccupationCodeValidatorTest extends BaseCodeValidatorTest<PatientDto> {

    @Test
    public void shouldPassForValidValues() throws Exception {
        String[] validStatuses = {"01", "02", "03", "92"};
        assertValidValues(validStatuses, "occupation", PatientDto.class);
    }

    @Test
    public void shouldFailForInvalidValues() throws Exception {
        String[] inValidRelations = {"", "some_invalid_code", "4", "93"};
        assertInvalidValues(inValidRelations, "occupation", PatientDto.class);
    }
}