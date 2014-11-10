package org.sharedhealth.mci.web.exception;

import org.springframework.validation.BindingResult;

public class SearchCriteriaParameterException extends RuntimeException {

    private BindingResult bindingResult;

    public SearchCriteriaParameterException(BindingResult result) {
        super(result.toString());
        bindingResult = result;
    }

    public SearchCriteriaParameterException(String message) {
        super(message);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
