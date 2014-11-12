package org.sharedhealth.mci.web.infrastructure.persistence;

import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.utils.AppUtils;
import org.sharedhealth.mci.web.exception.HealthIDExistException;
import org.sharedhealth.mci.web.exception.PatientNotFoundException;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.PatientDto;
import org.sharedhealth.mci.web.mapper.SearchCriteria;
import org.sharedhealth.mci.web.model.*;
import org.sharedhealth.mci.web.utils.UidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static com.datastax.driver.core.querybuilder.Select.Where;
import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.apache.commons.collections4.CollectionUtils.union;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sharedhealth.mci.web.infrastructure.persistence.PatientQueryBuilder.*;
import static org.springframework.data.cassandra.core.CassandraTemplate.createInsertQuery;

@Component
public class PatientRepository extends BaseRepository {

    protected static final Logger logger = LoggerFactory.getLogger(PatientRepository.class);
    private final UidGenerator uid;
    private PatientMapper patientMapper;

    @Autowired
    public PatientRepository(@Qualifier("MCICassandraTemplate") CassandraTemplate template, PatientMapper patientMapper, UidGenerator uid) {
        super(template);
        this.uid = uid;
        this.patientMapper = patientMapper;
    }

    public MCIResponse create(PatientDto patientDto) throws ExecutionException, InterruptedException {
        if (isNotBlank(patientDto.getHealthId())) {
            throw new HealthIDExistException("Cannot create patient with user/system specified health id: " + patientDto.getHealthId());
        }

        Patient p = patientMapper.toEntity(patientDto);
        String healthId = uid.getId();
        p.setHealthId(healthId);
        p.setCreatedAt(new Date());
        p.setUpdatedAt(new Date());
        p.setSurName(patientDto.getSurName());

        Batch batch = batch();
        CassandraConverter converter = template.getConverter();
        batch.add(createInsertQuery("patient", p, null, converter));
        buildBatchForMapping(p, healthId, batch, converter);

        template.execute(batch);
        return new MCIResponse(p.getHealthId(), HttpStatus.CREATED);
    }

    private void buildBatchForMapping(Patient p, String healthId, Batch batch, CassandraConverter converter) {
        String nationalId = p.getNationalId();
        if (isNotBlank(nationalId)) {
            batch.add(createInsertQuery("nid_mapping", new NidMapping(nationalId, healthId), null, converter));
        }

        String brn = p.getBirthRegistrationNumber();
        if (isNotBlank(brn)) {
            batch.add(createInsertQuery("brn_mapping", new BrnMapping(brn, healthId), null, converter));
        }

        String uid = p.getUid();
        if (isNotBlank(uid)) {
            batch.add(createInsertQuery("uid_mapping", new UidMapping(uid, healthId), null, converter));
        }

        String phoneNumber = p.getCellNo();
        if (isNotBlank(phoneNumber)) {
            batch.add(createInsertQuery("phone_number_mapping", new PhoneNumberMapping(p.getPhoneNumberAreaCode(),
                    phoneNumber, healthId), null, converter));
        }

        String divisionId = p.getDivisionId();
        String districtId = p.getDistrictId();
        String upazilaId = p.getUpazillaId();
        String givenName = p.getGivenName();
        String surname = p.getSurName();
        if (AppUtils.isNotBlank(divisionId, districtId, upazilaId, givenName, surname)) {
            batch.add(createInsertQuery("name_mapping", new NameMapping(divisionId, districtId, upazilaId, givenName,
                    surname, healthId), null, converter));
        }
    }

    public List<PatientDto> findAll(SearchCriteria criteria) {
        List<String> healthIds = findHealthIds(criteria);
        if (!CollectionUtils.isEmpty(healthIds)) {
            Where where = select().from("patient").where(in(HEALTH_ID, healthIds.toArray(new String[healthIds.size()])));
            List<Patient> patients = template.select(where.toString(), Patient.class);
            return filterPatients(patients, criteria);
        }
        return null;
    }

    List<PatientDto> filterPatients(List<Patient> patients, SearchCriteria criteria) {
        List<PatientDto> result = new ArrayList<>();
        for (Patient p : patients) {
            if (isMatchingPatient(p, criteria)) {
                result.add(patientMapper.toDto(p));
            }
        }
        return result;
    }

    private boolean isMatchingPatient(Patient p, SearchCriteria criteria) {
        if (isNotBlank(criteria.getNid()) && !p.getNationalId().equals(criteria.getNid())) {
            return false;
        }
        if (isNotBlank(criteria.getBrn()) && !criteria.getBrn().equals(p.getBirthRegistrationNumber())) {
            return false;
        }
        if (isNotBlank(criteria.getUid()) && !criteria.getUid().equals(p.getUid())) {
            return false;
        }

        if (isNotBlank(criteria.getArea_code()) && !criteria.getArea_code().equals(p.getPhoneNumberAreaCode())) {
            return false;
        }
        if (isNotBlank(criteria.getPhone_number()) && !criteria.getPhone_number().equals(p.getCellNo())) {
            return false;
        }

        if (isNotBlank(criteria.getDivision_id()) && !criteria.getDivision_id().equals(p.getDivisionId())) {
            return false;
        }
        if (isNotBlank(criteria.getDistrict_id()) && !criteria.getDistrict_id().equals(p.getDistrictId())) {
            return false;
        }
        if (isNotBlank(criteria.getUpazila_id()) && !criteria.getUpazila_id().equals(p.getUpazillaId())) {
            return false;
        }
        if (isNotBlank(criteria.getCity_corp_id()) && !criteria.getCity_corp_id().equals(p.getCityCorporationId())) {
            return false;
        }
        if (isNotBlank(criteria.getWard_id()) && !criteria.getWard_id().equals(p.getWardId())) {
            return false;
        }

        if (isNotBlank(criteria.getGiven_name()) && !criteria.getGiven_name().equals(p.getGivenName())) {
            return false;
        }
        if (isNotBlank(criteria.getSurname()) && !criteria.getSurname().equals(p.getSurName())) {
            return false;
        }
        return true;
    }

    List<String> findHealthIds(SearchCriteria criteria) {
        String query = null;

        if (isNotBlank(criteria.getNid())) {
            query = buildFindHidByNidQuery(criteria.getNid());

        } else if (isNotBlank(criteria.getBrn())) {
            query = buildFindHidByBrnQuery(criteria.getBrn());

        } else if (isNotBlank(criteria.getUid())) {
            query = buildFindHidByUidQuery(criteria.getUid());

        } else if (isNotBlank(criteria.getPhone_number())) {
            query = buildFindHidByPhoneNumberQuery(criteria.getPhone_number());

        } else if (AppUtils.isNotBlank(criteria.getDivision_id(), criteria.getDistrict_id(), criteria.getUpazila_id(),
                criteria.getGiven_name())) {
            query = buildFindHidByAddressAndNameQuery(criteria.getDivision_id(), criteria.getDistrict_id(),
                    criteria.getUpazila_id(), criteria.getGiven_name(), criteria.getSurname());
        }

        if (isNotBlank(query)) {
            return template.queryForList(query, String.class);
        }
        return null;
    }

    public String findHealthId(String nid, String brn, String uid) {
        Collection<String> hids = new ArrayList<>();
        if (isNotBlank(nid)) {
            hids = executeQuery(buildFindHidByNidQuery(nid));
        }

        if (isNotBlank(brn)) {
            hids = intersection(hids, executeQuery(buildFindHidByBrnQuery(brn)));
            if (CollectionUtils.isNotEmpty(hids)) {
                return hids.iterator().next();
            } else {
                hids = union(hids, executeQuery(buildFindHidByBrnQuery(brn)));
            }
        }

        if (isNotBlank(uid)) {
            hids = intersection(hids, executeQuery(buildFindHidByUidQuery(uid)));
            if (CollectionUtils.isNotEmpty(hids)) {
                return hids.iterator().next();
            }
        }
        return null;
    }

    private List<String> executeQuery(String query) {
        List<String> result = template.queryForList(query, String.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    public PatientDto findByHealthId(final String healthId) {
        Patient patient = template.selectOne(buildFindByHidQuery(healthId), Patient.class);
        if (patient == null) {
            return null;
        }
        return patientMapper.toDto(patient);
    }

    public MCIResponse update(String hid, PatientDto dto) {
        if (findByHealthId(hid) == null) {
            throw new PatientNotFoundException("No patient found with health id: " + hid);
        }
        Patient p = patientMapper.toEntity(dto);
        p.setHealthId(hid);
        p.setUpdatedAt(new Date());
        p = template.update(p);
        return new MCIResponse(p.getHealthId(), HttpStatus.ACCEPTED);
    }


    public List<PatientDto> findAllByLocations(List<String> locations, String start, Date since) {
        List<PatientDto> patients = new ArrayList<>();
        int limit = PER_PAGE_LIMIT;
        if (locations != null && locations.size() > 0) {
            String locationPointer = getLocationPointer(locations, start, null);
            for (String catchment : locations) {
                if (patients.size() == 0 && !isLocationBelongsToCatchment(locationPointer, catchment)) {
                    continue;
                }
                List<PatientDto> res = this.findAllByLocation(catchment, start, limit, since);
                try {
                    patients.addAll(res);
                    if (patients.size() < PER_PAGE_LIMIT) {
                        start = null;
                        limit = PER_PAGE_LIMIT - patients.size();
                        locationPointer = null;
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return this.findAllByLocation(null, start, limit, since);
        }
        return patients;
    }

    public List<PatientDto> findAllByLocation(String location, String start, int limit, Date since) {

        Select select = QueryBuilder.select().from("patient");

        if (StringUtils.isBlank(location)) {
            return new ArrayList<>();
        }

        select.where(QueryBuilder.eq(getAddressHierarchyField(location.length()), location));

        if (isNotBlank(start)) {
            select.where(QueryBuilder.gt(QueryBuilder.token("health_id"), QueryBuilder.raw("token('" + start + "')")));
        }

        if (since != null) {
            select.where(QueryBuilder.gt("updated_at", since));
            select.allowFiltering();
        }

        if (limit > 0) {
            select.limit(limit);
        }
        return patientMapper.toDto(template.select(select, Patient.class));
    }

    private String getLocationPointer(List<String> locations, String start, String d) {
        if (locations.size() > 1 && isNotBlank(start)) {
            PatientDto p = findByHealthId(start);
            return p.getAddress().getGeoCode();
        }
        return d;
    }

    private boolean isLocationBelongsToCatchment(String location, String catchment) {
        return StringUtils.isBlank(location) || location.startsWith(catchment);
    }

    private String getAddressHierarchyField(int length) {
        return "location_level" + (length / 2);
    }
}
