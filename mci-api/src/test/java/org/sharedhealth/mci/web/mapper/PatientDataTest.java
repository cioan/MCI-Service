package org.sharedhealth.mci.web.mapper;

import javax.validation.ConstraintViolation;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sharedhealth.mci.validation.group.RequiredGroup;

import static org.junit.Assert.assertEquals;
import static org.sharedhealth.mci.web.utils.JsonConstants.GIVEN_NAME;

public class PatientDataTest extends ValidationAwareMapper {

    @Test
    public void shouldFailIfGivenNameIsBlank() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "givenName", "");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfGivenNameIsNull() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "givenName", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfSurNameIsBlank() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "surName", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfDateOfBirthIsBlank() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "dateOfBirth", "   ");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfDateOfBirthIsInvalidDate() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "dateOfBirth", "1999-02-30");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfDateOfBirthIsValidDate() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "dateOfBirth", "1983-09-21");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfGenderIsBlank() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "gender", null, RequiredGroup.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("1001", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfNationalIdIsInvalid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "nationalId", "1");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfNationalIdIs_13_DigitLong() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "nationalId", "1234567890123");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfNationalIdIs_17_DigitLong() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "nationalId", "12345678901234567");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIf_UID_LengthIsNotEqual_11() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "uid", "1");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIf_UUID_ContainSpecialCharacter() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "uid", "123456*8901");
        assertEquals(1, constraintViolations.size());
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIf_UID_Is_11_DigitAlphaNumeric() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "uid", "UID45678901");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfBirthRegistrationNumberIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "birthRegistrationNumber", "12345674891234567");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfBirthRegistrationNumberIsLessThan17() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "birthRegistrationNumber", "1234567489123456");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldFailIfBirthRegistrationNumberIsMoreThan17() {
        assertLengthViolation("birthRegistrationNumber", 17);
    }

    @Test
    public void shouldFailIfContainSpecialCharacter() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "birthRegistrationNumber", "123456748*12345644");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfFullNameBanglaIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "nameBangla", "এ বি এম আখতার হোসেন মন্ডল");
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
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "givenName", "imran");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfSurNameIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "surName", "imran");
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
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "placeOfBirth", "rr;");
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldPassIfPlaceOfBirthIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "placeOfBirth", "Dhaka");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldPassIfNationalityIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "nationality", "bangladeshi");
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldFailIfNationalityIsMoreThan_50_Characters() {
        assertLengthViolation("nationality", 50);
    }

    @Test
    public void shouldPassIfPatientStatusIsValid() {

        String[] validStatus = {"alive", "deceased", "unknown"};
        for (String status : validStatus) {
            Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "status", status);
            assertEquals(0, constraintViolations.size());
        }
    }

    @Test
    public void shouldFailIfPatientStatusIsInvalid() {
        String[] inValidStatus = {"", "somevalue", "aalive", "alivea", "adeceased", "deceaseda", "aunknown", "unknowne"};
        for (String status : inValidStatus) {
            Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "status", status);
            assertEquals(1, constraintViolations.size());
            assertEquals("1004", constraintViolations.iterator().next().getMessage());
        }
    }

    @Test
    public void shouldFailIfPrimaryContactIsMoreThan_100_Characters() {
        assertLengthViolation("primaryContact", 100);
    }

    @Test
    public void shouldPassIfPrimaryContactIsValid() {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, "primaryContact", "imran");
        assertEquals(0, constraintViolations.size());
    }

    private void assertLengthViolation(String field, int length) {
        Set<ConstraintViolation<PatientData>> constraintViolations = validator.validateValue(PatientData.class, field, StringUtils.repeat("a", length + 1));
        assertEquals("1002", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void shouldRetrieveFieldValueFromJsonKey() {
        PatientData patient = new PatientData();
        patient.setGivenName("Harry");
        patient.setSurName("Potter");
        Object value = patient.getValue(GIVEN_NAME);
        assertEquals(patient.getGivenName(), value);
    }
}
