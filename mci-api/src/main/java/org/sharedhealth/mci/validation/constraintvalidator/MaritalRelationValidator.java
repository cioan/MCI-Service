package org.sharedhealth.mci.validation.constraintvalidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sharedhealth.mci.validation.constraints.MaritalRelation;
import org.sharedhealth.mci.web.mapper.PatientData;

public class MaritalRelationValidator implements ConstraintValidator<MaritalRelation, PatientData> {

    private String field;

    @Override
    public void initialize(MaritalRelation constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(final PatientData value, final ConstraintValidatorContext context) {

        if (value == null) return true;

        if (value.getRelationOfType("SPS") == null) return true;

        if (isNotUnmarried(value.getMaritalStatus())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(this.field)
                .addConstraintViolation();
        return false;
    }

    private boolean isNotUnmarried(String maritalStatus) {
        return maritalStatus != null && !(maritalStatus.equals("1"));
    }
}