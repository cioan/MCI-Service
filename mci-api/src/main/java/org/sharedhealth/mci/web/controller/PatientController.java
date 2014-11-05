package org.sharedhealth.mci.web.controller;

import org.sharedhealth.mci.validation.group.RequiredGroup;
import org.sharedhealth.mci.validation.group.RequiredOnUpdateGroup;
import org.sharedhealth.mci.web.exception.SearchQueryParameterException;
import org.sharedhealth.mci.web.exception.ValidationException;
import org.sharedhealth.mci.web.handler.MCIMultiResponse;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.PaginationQuery;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.sharedhealth.mci.web.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<MCIResponse> create(
            @RequestBody @Validated({RequiredGroup.class, Default.class}) PatientDto patientDto,
            BindingResult bindingResult) throws ExecutionException, InterruptedException {

        logger.debug("Trying to create patient.");
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        MCIResponse response = patientService.createOrUpdate(patientDto);
        return new ResponseEntity<>(response, response.httpStatusObject);
    }

    @RequestMapping(value = "/{healthId}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<PatientDto>> findByHealthId(@PathVariable String healthId)
            throws ExecutionException, InterruptedException {
        logger.debug("Trying to find patient by health id [" + healthId + "]");
        final DeferredResult<ResponseEntity<PatientDto>> deferredResult = new DeferredResult<>();

        patientService.findByHealthId(healthId).addCallback(new ListenableFutureCallback<PatientDto>() {
            @Override
            public void onSuccess(PatientDto result) {
                deferredResult.setResult(new ResponseEntity<>(result, OK));
            }

            @Override
            public void onFailure(Throwable e) {
                deferredResult.setErrorResult(extractAppException(e));
            }
        });
        return deferredResult;
    }

    private Throwable extractAppException(Throwable e) {
        if (e instanceof ExecutionException && e.getCause() != null) {
            return e.getCause();
        }
        return e;
    }

    @RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MCIMultiResponse> findPatients(@Valid SearchCriteria criteria, BindingResult bindingResult)
            throws ExecutionException, InterruptedException {

        logger.debug("Find all patients  by search query ");
        if (bindingResult.hasErrors()) {
            throw new SearchQueryParameterException(bindingResult);
        }

        final int limit = patientService.getPerPageMaximumLimit();
        final String note = patientService.getPerPageMaximumLimitNote();
        criteria.setMaximum_limit(limit);

        List<PatientDto> patients = patientService.findAll(criteria);
        HashMap<String, String> additionalInfo = new HashMap<>();
        if (patients.size() > limit) {
            patients.remove(limit);
            additionalInfo.put("note", note);
        }
        MCIMultiResponse mciMultiResponse = new MCIMultiResponse(patients, additionalInfo, OK);
        return new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{healthId}", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<MCIResponse> update(@PathVariable String healthId, @Validated({RequiredOnUpdateGroup.class, Default.class}) @RequestBody PatientDto patientDto,
                                              BindingResult bindingResult) throws ExecutionException, InterruptedException {

        logger.debug(" Health id [" + healthId + "]");
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        MCIResponse mciResponse = patientService.createOrUpdate(patientDto);
        return new ResponseEntity<>(mciResponse, mciResponse.httpStatusObject);
    }

    @RequestMapping(value = "/facility/{facilityId}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<MCIMultiResponse>> findAllPatientsInCatchment(
            @PathVariable String facilityId,
            @Valid PaginationQuery paginationQuery, BindingResult bindingResult
    ) throws ExecutionException, InterruptedException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        logger.debug("Find all patients  for catchment of facility [" + facilityId + "]");
        final DeferredResult<ResponseEntity<MCIMultiResponse>> deferredResult = new DeferredResult<>();

        patientService.findAllByFacility(facilityId, paginationQuery.getLast(), paginationQuery.getDateSince()).addCallback(new ListenableFutureCallback<List<PatientDto>>() {
            @Override
            public void onSuccess(List<PatientDto> results) {
                HashMap<String, String> additionalInfo = null;
                MCIMultiResponse mciMultiResponse = new MCIMultiResponse(results, additionalInfo, OK);
                deferredResult.setResult(new ResponseEntity<>(mciMultiResponse, mciMultiResponse.httpStatusObject));
            }

            @Override
            public void onFailure(Throwable error) {
                deferredResult.setErrorResult(extractAppException(error));
            }
        });

        return deferredResult;
    }
}
