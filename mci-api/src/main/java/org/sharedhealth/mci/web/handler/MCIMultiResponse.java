package org.sharedhealth.mci.web.handler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonIgnoreProperties(value = {"httpStatusObject"})
@JsonPropertyOrder({"http_status", "results", "additional_info"})
public class MCIMultiResponse<T, A> {

    @JsonProperty("http_status")
    public int httpStatus;

    public List<T> results;

    @JsonProperty("additional_info")
    @JsonInclude(NON_EMPTY)
    public HashMap<String, String> additionalInfo;

    public HttpStatus httpStatusObject;

    public MCIMultiResponse(List<T> results, HashMap<String, String> additionalInfo, HttpStatus httpStatusObject) {
        this.httpStatusObject = httpStatusObject;
        this.httpStatus = httpStatusObject.value();
        this.results = results;
        this.additionalInfo = additionalInfo;
    }

    public MCIMultiResponse(HttpStatus httpStatusObject) {
        this.httpStatusObject = httpStatusObject;
        this.httpStatus = httpStatusObject.value();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public HashMap<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(HashMap<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public HttpStatus getHttpStatusObject() {
        return httpStatusObject;
    }

    public void setHttpStatusObject(HttpStatus httpStatusObject) {
        this.httpStatusObject = httpStatusObject;
    }

    @Override
    public boolean equals(Object rhs) {
        return EqualsBuilder.reflectionEquals(this, rhs);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
