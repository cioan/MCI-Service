package org.sharedhealth.mci.validation.constraintvalidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sharedhealth.mci.validation.constraints.MaritalRelation;
import org.sharedhealth.mci.web.mapper.PatientDto;
public class MaritalRelationValidator implements ConstraintValidator<MaritalRelation, PatientDto> {

    private String field;

    @Override
    public void initialize(MaritalRelation constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(final PatientDto value, final ConstraintValidatorContext context) {

        if (value == null) return true;

        if (value.getRelation("SPS") == null) return true;

        if (isNotNullOrNotUnmarried(value.getMaritalStatus())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(this.field)
                .addConstraintViolation();
        return false;
    }

    private boolean isNotNullOrNotUnmarried(String maritalStatus) {
        return maritalStatus != null && !(maritalStatus.equals("1"));
    }
}