package org.sharedhealth.mci.validation.constraintvalidator;

import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.validation.constraints.SearchQueryConstraint;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class SearchQueryValidator implements ConstraintValidator<SearchQueryConstraint, SearchCriteria> {

    private static final Logger logger = LoggerFactory.getLogger(SearchQueryValidator.class);
    private static final String ERROR_CODE_REQUIRED = "1006";
    @Override
    public void initialize(SearchQueryConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(final SearchCriteria value, final ConstraintValidatorContext context) {

        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        logger.debug("Present address" + value.getDivisionId());
        logger.debug("Sur name" + value.getSurname());

        if (StringUtils.isEmpty(value.getDivisionId()) && StringUtils.isNotEmpty(value.getSurname())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if (StringUtils.isEmpty(value.getDivisionId()) && StringUtils.isNotEmpty(value.getGivenName())) {
            isValid = false;
            addConstraintViolation(context, ERROR_CODE_REQUIRED);
        }

        if(isAllFieldNull(value)) {
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
        for(Field field : searchCriteria.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            logger.debug("Search Field" + field);

            try {
                logger.debug("Search Field" + field.get(searchCriteria));
                if(field.get(searchCriteria) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


}