package org.sharedhealth.mci.web.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sharedhealth.mci.web.config.EnvironmentMock;
import org.sharedhealth.mci.web.config.WebMvcConfigTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(initializers = EnvironmentMock.class, classes = WebMvcConfigTest.class)
public class RelationCodeValidatorTest extends BaseCodeValidatorTest<Relation> {

    @Test
    public void shouldPassForValidValues() throws Exception {
        String[] validRelations = {"FTH"};
        assertValidValues(validRelations, "type", Relation.class);
    }

    @Test
    public void shouldFailForInvalidValues() throws Exception {
        String[] inValidRelations = {"", "some_invalid_code"};
        assertInvalidValues(inValidRelations, "type", Relation.class);
    }

    @Test
    public void shouldFailIfTypeIsNullForNonEmptyRelation() {
        Relation relation = new Relation();
        relation.setHealthId("1234567890123456789");
        Set<ConstraintViolation<Relation>> constraintViolations = getValidator().validate(relation);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }
}