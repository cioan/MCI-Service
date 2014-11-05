package org.sharedhealth.mci.web.mapper;

import javax.validation.ConstraintViolation;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sharedhealth.mci.validation.group.RequiredGroup;

import static org.junit.Assert.assertEquals;

public class PatientDtoTest extends ValidationAwareMapper{

    @Test
    public void shouldFailIfGivenNameIsBlank() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "givenName", "");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfGivenNameIsNull() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "givenName", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfSurNameIsBlank() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "surName", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfDateOfBirthIsBlank() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "dateOfBirth", "   ");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfDateOfBirthIsInvalidDate() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "dateOfBirth", "1999-02-30");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfDateOfBirthIsValidDate() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "dateOfBirth", "1983-09-21");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfGenderIsBlank() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "gender", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfNationalIdIsInvalid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "nationalId", "1");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfNationalIdIs_13_DigitLong() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "nationalId", "1234567890123");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfNationalIdIs_17_DigitLong() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "nationalId", "12345678901234567");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIf_UID_LengthIsNotEqual_11() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "uid", "1");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIf_UUID_ContainSpecialCharacter() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "uid", "123456*8901");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIf_UID_Is_11_DigitAlphaNumeric() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "uid", "UID45678901");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfBirthRegistrationNumberIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "birthRegistrationNumber", "12345674891234567");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfBirthRegistrationNumberIsLessThan17() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "birthRegistrationNumber", "1234567489123456");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfBirthRegistrationNumberIsMoreThan17() {
        assertLengthViolation("birthRegistrationNumber", 17);
    }

    @Test
    public void shouldFailIfContainSpecialCharacter() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "birthRegistrationNumber", "123456748*12345644");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfFullNameBanglaIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "nameBangla", "এ বি এম আখতার হোসেন মন্ডল");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfFullNameBanglaIsMoreThan_125_Characters() {
        assertLengthViolation("nameBangla", 125);
    }

    @Test
    public void shouldFailIfGivenNameIsMoreThan_100_Characters() {
        assertLengthViolation("givenName", 100);
    }

    @Test
    public void shouldPassIfGivenNameIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "givenName", "imran");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfSurNameIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "surName", "imran");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfSurNameIsMoreThan_25_Characters() {
        assertLengthViolation("surName", 25);
    }

    @Test
    public void shouldFailIfPlaceOfBirthIsMoreThan_20_AlphabeticCharacters() {
        assertLengthViolation("placeOfBirth", 20);
    }

    @Test
    public void shouldFailIfPlaceOfBirthIsContainSpecialCharacterAndNumericCharacter() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "placeOfBirth", "rr;");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfPlaceOfBirthIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "placeOfBirth", "Dhaka");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfNationalityIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "nationality", "bangladeshi");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfNationalityIsMoreThan_50_Characters() {
        assertLengthViolation("nationality", 50);
    }

    @Test
    public void shouldPassIfAliveIsValid() {
        for (int i = 0; i < 2; i++) {
            Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "isAlive", Integer.toString(i));
            assertEquals(0, constraintViolations.size());
        }
    }

    @Test
    public void shouldFailIfAliveIsInvalid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "isAlive", "3");
        assertEquals(1, constraintViolations.size());
        assertEquals("1004", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfPrimaryContactIsMoreThan_100_Characters() {
        assertLengthViolation("primaryContact", 100);
    }

    @Test
    public void shouldPassIfPrimaryContactIsValid() {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, "primaryContact", "imran");
        assertEquals(0, constraintViolations.size());
    }

    private void assertLengthViolation(String field, int length) {
        Set<ConstraintViolation<PatientDto>> constraintViolations = validator.validateValue(PatientDto.class, field, StringUtils.repeat("a", length + 1));
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

}
