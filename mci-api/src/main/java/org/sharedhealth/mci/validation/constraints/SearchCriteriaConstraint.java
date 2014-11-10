package org.sharedhealth.mci.validation.constraints;

import org.sharedhealth.mci.validation.constraintvalidator.SearchCriteriaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SearchCriteriaValidator.class)
@Documented
public @interface SearchCriteriaConstraint {
    String message() default "1006";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SearchCriteriaConstraint[] value();
    }
}
