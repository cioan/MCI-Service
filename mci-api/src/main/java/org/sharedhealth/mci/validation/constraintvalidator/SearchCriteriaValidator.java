package org.sharedhealth.mci.validation.constraintvalidator;

import org.sharedhealth.mci.validation.constraints.SearchCriteriaConstraint;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SearchCriteriaValidator implements ConstraintValidator<SearchCriteriaConstraint, SearchCriteria> {

    private static final Logger logger = LoggerFactory.getLogger(SearchCriteriaValidator.class);
    private static final String ERROR_CODE_REQUIRED = "1006";

    @Override
    public void initialize(SearchCriteriaConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(final SearchCriteria value, final ConstraintValidatorContext context) {

        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        logger.debug("Present address" + value.getDivision_id());
        logger.debug("Sur name" + value.getSurname());

        if (isEmpty(value.getDivision_id()) && isNotEmpty(value.getSurname())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if (isEmpty(value.getDivision_id()) && isNotEmpty(value.getGiven_name())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if (isAllFieldNull(value)) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if (isEmpty(value.getPhone_number()) && isNotEmpty(value.getArea_code())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if (isEmpty(value.getDivision_id()) && isNotEmpty(value.getPhone_number())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        return isValid;

    }

    private void addConstraintViolation(ConstraintValidatorContext context, String code) {
        context.buildConstraintViolationWithTemplate(code)
                .addConstraintViolation();
    }

    private boolean isAllFieldNull(SearchCriteria searchCriteria) {
        for (Field field : searchCriteria.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            logger.debug("Search Field" + field);

            try {
                logger.debug("Search Field" + field.get(searchCriteria));
                if (field.get(searchCriteria) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}