package org.sharedhealth.mci.web.mapper;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sharedhealth.mci.validation.group.RequiredOnUpdateGroup;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class AddressTest extends ValidationAwareMapper {

    @Test
    public void shouldFailIfAddressLineIsBlank() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "addressLine", null, RequiredOnUpdateGroup.class);
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);
    }

    @Test
    public void shouldFailIfAddressLineSizeLessThan3() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "addressLine", "ab");
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);
    }

    @Test
    public void shouldPassIfAddressLineSize3() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "addressLine", "row");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfDivisionIdIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "divisionId", "15");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfDivisionIdIsInvalid() {
        assertInvalidAddressId("divisionId");
    }

    @Test
    public void shouldPassIfDistrictIdIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "districtId", "15");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfDistrictIdIsInvalid() {
        assertInvalidAddressId("districtId");
    }

    @Test
    public void shouldPassIfUpazillaIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "upazillaId", "15");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfUpazillaIdIsInvalid() {
        assertInvalidAddressId("upazillaId");
    }

    @Test
    public void shouldPassIfThanaIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "thanaId", "15");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfThanaIdIsInvalid() {
        assertInvalidAddressId("thanaId");
    }

    @Test
    public void shouldPassIfCityCorporationIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "cityCorporationId", "12");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfCityCorporationIdIsInvalid() {
        assertInvalidAddressId("cityCorporationId");
    }

    @Test
    public void shouldPassIfWardIdIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "wardId", "13");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfWardIdIsInvalid() {
        assertInvalidAddressId("wardId");
    }


    @Test
    public void shouldPassIfUnionIdIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "unionId", "15");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfUnionIdIsInvalid() {
        assertInvalidAddressId("unionId");
    }

    @Test
    public void shouldFailIfStreetIsMoreThan_50_Characters() {
        assertLengthViolation("street", 50);
    }

    @Test
    public void shouldPassIfStreetIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "street", "DH1234567");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfPostOfficeIsMoreThan_50_Characters() {
        assertLengthViolation("postOffice", 50);
    }

    @Test
    public void shouldPassIfPostOfficeIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "postOffice", "Dhaka");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfPostCodeIsMoreThan_4_Characters() {
        assertLengthViolation("postCode", 4);
    }

    @Test
    public void shouldPassIfPostCodeIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "postCode", "1362");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfVillageIsMoreThan_50_Characters() {
        assertLengthViolation("village", 50);
    }

    @Test
    public void shouldPassIfVillageIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "village", "Dhaka");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfAreaMoujaIsMoreThan_50_Characters() {
        assertLengthViolation("areaMouja", 50);
    }

    @Test
    public void shouldPassIfAreaMoujaIsValid() {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, "areaMouja", "Dhaka");
        assertEquals(0, constraintViolations.size());
    }

    private void assertInvalidAddressId(String field) {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, field, "abcd");
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);

        constraintViolations = validator.validateValue(Address.class, field, "2001");
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);

        constraintViolations = validator.validateValue(Address.class, field, "1");
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);
    }

    protected void assertLengthViolation(String field, int length) {
        assertLengthViolation(field, length, "a");
    }

    protected void assertLengthViolation(String field, int length, String str) {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateValue(Address.class, field, StringUtils.repeat(str, length + 1));
        assertEquals(1, constraintViolations.size());
        printViolations(constraintViolations);
    }

    protected void printViolations(Set<ConstraintViolation<Address>> constraintViolations) {

        for (ConstraintViolation<Address> violation : constraintViolations) {

            String invalidValue = (String) violation.getInvalidValue();
            String message = violation.getMessage();
            System.out.println("Found constraint violation. Value: " + invalidValue
                    + " Message: " + message);
        }
    }
}
