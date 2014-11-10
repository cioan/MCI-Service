package org.sharedhealth.mci.web.infrastructure.persistence;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.utils.AppUtils;
import org.sharedhealth.mci.web.exception.HealthIDExistException;
import org.sharedhealth.mci.web.exception.PatientNotFoundException;
import org.sharedhealth.mci.web.handler.MCIResponse;
import org.sharedhealth.mci.web.mapper.*;
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

import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
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

    @Autowired
    public PatientRepository(@Qualifier("MCICassandraTemplate") CassandraTemplate template, UidGenerator uid) {
        super(template);
        this.uid = uid;
    }

    public MCIResponse create(PatientDto patientDto) throws ExecutionException, InterruptedException {
        if (isNotBlank(patientDto.getHealthId())) {
            throw new HealthIDExistException("Cannot create patient with user/system specified health id: " + patientDto.getHealthId());
        }
        String fullName = "";
        if (patientDto.getGivenName() != null) {
            fullName = patientDto.getGivenName();
        }
        if (patientDto.getSurName() != null) {
            fullName = fullName + " " + patientDto.getSurName();
        }

        Patient p = getEntityFromPatientMapper(patientDto);

        String healthId = uid.getId();
        p.setHealthId(healthId);
        p.setFullName(fullName);
        p.setCreatedAt(new Date());
        p.setUpdatedAt(new Date());
        p.setSurName(patientDto.getSurName());

        Batch batch = QueryBuilder.batch();
        CassandraConverter converter = template.getConverter();

        batch.add(createInsertQuery("patient", p, null, converter));

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
            batch.add(createInsertQuery("phone_number_mapping", new PhoneNumberMapping(p.getPhoneNumberAreaCode(), phoneNumber, healthId), null, converter));
        }

        String divisionId = p.getDivisionId();
        String districtId = p.getDistrictId();
        String upazilaId = p.getUpazillaId();
        String givenName = p.getGivenName();
        String surname = p.getSurName();
        if (AppUtils.isNotBlank(divisionId, districtId, upazilaId, givenName, surname)) {
            batch.add(createInsertQuery("name_mapping", new NameMapping(divisionId, districtId, upazilaId, givenName, surname, healthId), null, converter));
        }

        template.execute(batch);
        return new MCIResponse(p.getHealthId(), HttpStatus.CREATED);
    }

    public List<PatientDto> findAll(SearchCriteria criteria) {
        List<String> healthIds = findHealthIds(criteria);
        if (!CollectionUtils.isEmpty(healthIds)) {
            Where where = select().from("patient").where(in(HEALTH_ID, healthIds.toArray(new String[]{})));
            List<Patient> patients = template.select(where.toString(), Patient.class);
            return filterPatients(patients, criteria);
        }
        return null;
    }

    List<PatientDto> filterPatients(List<Patient> patients, SearchCriteria criteria) {
        List<PatientDto> result = new ArrayList<>();
        for (Patient p : patients) {
            if (isMatchingPatient(p, criteria)) {
                result.add(buildPatientDto(p));
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
        return buildPatientDto(patient);
    }

    private PatientDto buildPatientDto(Patient patient) {
        PatientDto patientDto = new PatientDto();

        patientDto.setHealthId(patient.getHealthId());
        patientDto.setNationalId(patient.getNationalId());
        patientDto.setUid(patient.getUid());
        patientDto.setPlaceOfBirth(patient.getPlaceOfBirth());

        patientDto.setReligion(patient.getReligion());
        patientDto.setBloodGroup(patient.getBloodGroup());
        patientDto.setNameBangla(patient.getFullNameBangla());
        patientDto.setBirthRegistrationNumber(patient.getBirthRegistrationNumber());
        patientDto.setGivenName(patient.getGivenName());
        patientDto.setSurName(patient.getSurName());
        //  patientDto.setDateOfBirth(patient.getDateOfBirth().toString());
        patientDto.setGender(patient.getGender());
        patientDto.setOccupation(patient.getOccupation());
        patientDto.setEducationLevel(patient.getEducationLevel());
        patientDto.setNationality(patient.getNationality());
        patientDto.setDisability(patient.getDisability());
        patientDto.setEthnicity(patient.getEthnicity());
        patientDto.setIsAlive(patient.getIsAlive());
        patientDto.setMaritalStatus(patient.getMaritalStatus());

        patientDto.setPrimaryContact(patient.getPrimaryContact());


        Address address = new Address();
        address.setAddressLine(patient.getAddressLine());
        address.setDivisionId(patient.getDivisionId());
        address.setDistrictId(patient.getDistrictId());
        address.setUpazillaId(patient.getUpazillaId());
        address.setUnionId(patient.getUnionId());
        address.setHoldingNumber(patient.getHoldingNumber());
        address.setStreet(patient.getStreet());
        address.setAreaMouja(patient.getAreaMouja());
        address.setVillage(patient.getVillage());
        address.setPostOffice(patient.getPostOffice());
        address.setPostCode(patient.getPostCode());
        address.setWardId(patient.getWardId());
        address.setThanaId(patient.getThanaId());
        address.setCityCorporationId(patient.getCityCorporationId());
        address.setCountryCode(patient.getCountryCode());
        patientDto.setAddress(address);

        Address permanentaddress = new Address();
        permanentaddress.setAddressLine(patient.getPermanentAddressLine());
        permanentaddress.setDivisionId(patient.getPermanentDivisionId());
        permanentaddress.setDistrictId(patient.getPermanentDistrictId());
        permanentaddress.setUpazillaId(patient.getPermanentUpazillaId());
        permanentaddress.setUnionId(patient.getPermanentUnionId());
        permanentaddress.setHoldingNumber(patient.getPermanentHoldingNumber());
        permanentaddress.setStreet(patient.getPermanentStreet());
        permanentaddress.setAreaMouja(patient.getPermanentAreaMouja());
        permanentaddress.setVillage(patient.getPermanentVillage());
        permanentaddress.setPostOffice(patient.getPostOffice());
        permanentaddress.setPostCode(patient.getPostCode());
        permanentaddress.setWardId(patient.getPermanentWardId());
        permanentaddress.setThanaId(patient.getPermanentThanaId());
        permanentaddress.setCityCorporationId(patient.getPermanentCityCorporationId());
        permanentaddress.setCountryCode(patient.getPermanentCountryCode());

        PhoneNumber phoneNumber = new PhoneNumber();
        PhoneNumber primaryContactNumber = new PhoneNumber();

        phoneNumber.setNumber(patient.getCellNo());
        phoneNumber.setAreaCode(patient.getPhoneNumberAreaCode());
        phoneNumber.setCountryCode(patient.getPhoneNumberCountryCode());
        phoneNumber.setExtension(patient.getPhoneNumberExtension());

        primaryContactNumber.setNumber(patient.getPrimaryContact());
        primaryContactNumber.setAreaCode(patient.getPrimaryContactNumberAreaCode());
        primaryContactNumber.setCountryCode(patient.getPrimaryContactNumberCountryCode());
        primaryContactNumber.setExtension(patient.getPrimaryContactNumberExtension());

        if (primaryContactNumber.getNumber() != null) {
            patientDto.setPhoneNumber(phoneNumber);
        }

        if (phoneNumber.getNumber() != null) {
            patientDto.setPrimaryContactNumber(primaryContactNumber);
        }

        if (permanentaddress.getCountryCode() != null) {
            if (permanentaddress.getCountryCode() == "050" && permanentaddress.getDistrictId() != null) {
                patientDto.setPermanentAddress(permanentaddress);
            }

            if (permanentaddress.getCountryCode() != "050") {
                patientDto.setPermanentAddress(permanentaddress);
            }
        }

        patientDto.setCreatedAt(patient.getCreatedAt());
        patientDto.setUpdatedAt(patient.getUpdatedAt());

        return patientDto;
    }

    public MCIResponse update(String hid, PatientDto dto) {
        if (findByHealthId(hid) == null) {
            throw new PatientNotFoundException("No patient found with health id: " + hid);
        }

        String fullName = "";
        if (dto.getGivenName() != null) {
            fullName = dto.getGivenName();
        }
        if (dto.getSurName() != null) {
            fullName = fullName + " " + dto.getSurName();
        }

        Patient p = getEntityFromPatientMapper(dto);
        p.setHealthId(hid);
        p.setFullName(fullName);
        p.setUpdatedAt(new Date());
        p = template.update(p);
        return new MCIResponse(p.getHealthId(), HttpStatus.ACCEPTED);
    }

    public Patient getEntityFromPatientMapper(PatientDto dto, Patient patient) {

        String relationsJson = "";
        ObjectMapper mapper = new ObjectMapper();

        Relation father = dto.getRelation("FTH");
        Relation mother = dto.getRelation("MTH");

        try {
            relationsJson = mapper.writeValueAsString(dto.getRelations());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Address address = dto.getAddress();
        Address permanentAddress = dto.getPermanentAddress();

        PhoneNumber phoneNumber = dto.getPhoneNumber();
        PhoneNumber primaryContactNumber = dto.getPrimaryContactNumber();

        patient.setHealthId(dto.getHealthId());
        patient.setNationalId(dto.getNationalId());
        patient.setBirthRegistrationNumber(dto.getBirthRegistrationNumber());
        patient.setFullNameBangla(StringUtils.trim(dto.getNameBangla()));
        patient.setGivenName(StringUtils.trim(dto.getGivenName()));
        if (dto.getGivenName() != null) {
            patient.setLowerGivenName(StringUtils.trim(dto.getGivenName()).toLowerCase());
        }
        patient.setSurName(StringUtils.trim(dto.getSurName()));
        if (dto.getSurName() != null) {
            patient.setLowerSurName(StringUtils.trim(dto.getSurName()).toLowerCase());
        }
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setOccupation(dto.getOccupation());
        patient.setEducationLevel(dto.getEducationLevel());

        if (father != null) {
            patient.setFathersNameBangla(StringUtils.trim(father.getNameBangla()));
            patient.setFathersGivenName(StringUtils.trim(father.getGivenName()));
            patient.setFathersSurName(StringUtils.trim(father.getSurName()));
            patient.setFathersBrn(father.getBirthRegistrationNumber());
            patient.setFathersNid(father.getNationalId());
            patient.setFathersUid(father.getUid());
        }

        if (mother != null) {
            patient.setMothersNameBangla(StringUtils.trim(mother.getNameBangla()));
            patient.setMothersGivenName(StringUtils.trim(mother.getGivenName()));
            patient.setMothersSurName(StringUtils.trim(mother.getSurName()));
            patient.setMothersBrn(mother.getBirthRegistrationNumber());
            patient.setMothersNid(mother.getNationalId());
            patient.setMothersUid(mother.getUid());
        }

        patient.setUid(dto.getUid());
        patient.setPlaceOfBirth(StringUtils.trim(dto.getPlaceOfBirth()));
        patient.setReligion(dto.getReligion());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setNationality(StringUtils.trim(dto.getNationality()));
        patient.setDisability(dto.getDisability());
        patient.setEthnicity(dto.getEthnicity());
        patient.setIsAlive(dto.getIsAlive());
        patient.setMaritalStatus(dto.getMaritalStatus());

        if (address != null) {
            patient.setAddressLine(address.getAddressLine());
            patient.setDivisionId(address.getDivisionId());
            patient.setDistrictId(address.getDistrictId());
            patient.setUpazillaId(address.getUpazillaId());
            patient.setUnionId(address.getUnionId());
            patient.setHoldingNumber(StringUtils.trim(address.getHoldingNumber()));
            patient.setStreet(StringUtils.trim(address.getStreet()));
            patient.setAreaMouja(StringUtils.trim(address.getAreaMouja()));
            patient.setVillage(StringUtils.trim(address.getVillage()));
            patient.setPostOffice(StringUtils.trim(address.getPostOffice()));
            patient.setPostCode(address.getPostCode());
            patient.setWardId(address.getWardId());
            patient.setThanaId(address.getThanaId());
            patient.setCityCorporationId(address.getCityCorporationId());
            patient.setCountryCode(address.getCountryCode());
        }

        if (permanentAddress != null) {
            patient.setPermanentAddressLine(permanentAddress.getAddressLine());
            patient.setPermanentDivisionId(permanentAddress.getDivisionId());
            patient.setPermanentDistrictId(permanentAddress.getDistrictId());
            patient.setPermanentUpazillaId(permanentAddress.getUpazillaId());
            patient.setPermanentUnionId(permanentAddress.getUnionId());
            patient.setPermanentHoldingNumber(StringUtils.trim(permanentAddress.getHoldingNumber()));
            patient.setPermanentStreet(StringUtils.trim(permanentAddress.getStreet()));
            patient.setPermanentAreaMouja(StringUtils.trim(permanentAddress.getAreaMouja()));
            patient.setPermanentVillage(StringUtils.trim(permanentAddress.getVillage()));
            patient.setPermanentPostOffice(StringUtils.trim(permanentAddress.getPostOffice()));
            patient.setPermanentPostCode(permanentAddress.getPostCode());
            patient.setPermanentWardId(permanentAddress.getWardId());
            patient.setPermanentThanaId(permanentAddress.getThanaId());
            patient.setPermanentCityCorporationId(permanentAddress.getCityCorporationId());
            patient.setPermanentCountryCode(permanentAddress.getCountryCode());
        }

        patient.setRelations(relationsJson);

        if (phoneNumber != null) {
            patient.setCellNo(phoneNumber.getNumber());
            patient.setPhoneNumberAreaCode(phoneNumber.getAreaCode());
            patient.setPhoneNumberCountryCode(phoneNumber.getCountryCode());
            patient.setPhoneNumberExtension(phoneNumber.getExtension());
        }

        if (primaryContactNumber != null) {
            patient.setPrimaryCellNo(primaryContactNumber.getNumber());
            patient.setPrimaryContactNumberAreaCode(primaryContactNumber.getAreaCode());
            patient.setPrimaryContactNumberCountryCode(primaryContactNumber.getCountryCode());
            patient.setPrimaryContactNumberExtension(primaryContactNumber.getExtension());
        }

        patient.setPrimaryContact(StringUtils.trim(dto.getPrimaryContact()));


        return patient;
    }

    public List<PatientDto> findAllByLocations(List<String> locations, String start, Date since) {

        final SettableFuture<List<PatientDto>> result = SettableFuture.create();
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
                    List<PatientDto> temp = res;
                    patients.addAll(temp);

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

        return getPatientListListenableFuture(select);
    }

    private String getLocationPointer(List<String> locations, String start, String d) {
        if (locations.size() > 1 && isNotBlank(start)) {
            PatientDto p = findByHealthId(start);
            return p.getAddress().getGeoCode();
        }

        return d;
    }

    private List<PatientDto> getPatientListListenableFuture(final Select select) {
        ResultSet resultSet = template.query(select);
        List<PatientDto> patientMappers = new ArrayList<>();
        for (Row result : resultSet.all()) {
            PatientDto patientMapper = getPatientFromRow(result);
            patientMappers.add(patientMapper);
        }
        return patientMappers;
    }

    private boolean isLocationBelongsToCatchment(String location, String catchment) {

        return StringUtils.isBlank(location) || location.startsWith(catchment);

    }

    private String getAddressHierarchyField(int length) {
        return "location_level" + (length / 2);
    }

    private PatientDto getPatientFromRow(Row r) {
        DatabaseRow row = new DatabaseRow(r);
        PatientDto patientMapper = new PatientDto();
        ObjectMapper mapper = new ObjectMapper();

        try {
            patientMapper.setRelations(mapper.readValue(row.getString(RELATIONS), List.class));
        } catch (Exception e) {
            logger.debug(" Relations: [" + e.getMessage() + "]");
        }

        patientMapper.setHealthId(row.getString(HEALTH_ID));
        patientMapper.setNationalId(row.getString(NATIONAL_ID));
        patientMapper.setUid(row.getString(UID));
        patientMapper.setPlaceOfBirth(row.getString(PLACE_OF_BIRTH));

        patientMapper.setReligion(row.getString(RELIGION));
        patientMapper.setBloodGroup(row.getString(BLOOD_GROUP));
        patientMapper.setNameBangla(row.getString(FULL_NAME_BANGLA));
        patientMapper.setBirthRegistrationNumber(row.getString(BIN_BRN));
        patientMapper.setGivenName(row.getString(GIVEN_NAME));
        patientMapper.setSurName(row.getString(SUR_NAME));
        patientMapper.setDateOfBirth(row.getDateAsString(DATE_OF_BIRTH));
        patientMapper.setGender(row.getString(GENDER));
        patientMapper.setOccupation(row.getString(OCCUPATION));
        patientMapper.setEducationLevel(row.getString(EDU_LEVEL));
        patientMapper.setNationality(row.getString(NATIONALITY));
        patientMapper.setDisability(row.getString(DISABILITY));
        patientMapper.setEthnicity(row.getString(ETHNICITY));
        patientMapper.setIsAlive(row.getString(IS_ALIVE));
        patientMapper.setMaritalStatus(row.getString(MARITAL_STATUS));

        patientMapper.setPrimaryContact(row.getString(PRIMARY_CONTACT));


        Address address = new Address();
        address.setAddressLine(row.getString(ADDRESS_LINE));
        address.setDivisionId(row.getString(DIVISION_ID));
        address.setDistrictId(row.getString(DISTRICT_ID));
        address.setUpazillaId(row.getString(UPAZILLA_ID));
        address.setUnionId(row.getString(UNION_ID));
        address.setHoldingNumber(row.getString(HOLDING_NUMBER));
        address.setStreet(row.getString(STREET));
        address.setAreaMouja(row.getString(AREA_MOUJA));
        address.setVillage(row.getString(VILLAGE));
        address.setPostOffice(row.getString(POST_OFFICE));
        address.setPostCode(row.getString(POST_CODE));
        address.setWardId(row.getString(WARD));
        address.setThanaId(row.getString(THANA));
        address.setCityCorporationId(row.getString(CITY_CORPORATION));
        address.setCountryCode(row.getString(COUNTRY));
        patientMapper.setAddress(address);

        Address permanentaddress = new Address();
        permanentaddress.setAddressLine(row.getString(PERMANENT_ADDRESS_LINE));
        permanentaddress.setDivisionId(row.getString(PERMANENT_DIVISION_ID));
        permanentaddress.setDistrictId(row.getString(PERMANENT_DISTRICT_ID));
        permanentaddress.setUpazillaId(row.getString(PERMANENT_UPAZILLA_ID));
        permanentaddress.setUnionId(row.getString(PERMANENT_UNION_ID));
        permanentaddress.setHoldingNumber(row.getString(PERMANENT_HOLDING_NUMBER));
        permanentaddress.setStreet(row.getString(PERMANENT_STREET));
        permanentaddress.setAreaMouja(row.getString(PERMANENT_AREA_MOUJA));
        permanentaddress.setVillage(row.getString(PERMANENT_VILLAGE));
        permanentaddress.setPostOffice(row.getString(PERMANENT_POST_OFFICE));
        permanentaddress.setPostCode(row.getString(PERMANENT_POST_CODE));
        permanentaddress.setWardId(row.getString(PERMANENT_WARD));
        permanentaddress.setThanaId(row.getString(PERMANENT_THANA));
        permanentaddress.setCityCorporationId(row.getString(PERMANENT_CITY_CORPORATION));
        permanentaddress.setCountryCode(row.getString(PERMANENT_COUNTRY));

        PhoneNumber phoneNumber = new PhoneNumber();
        PhoneNumber primaryContactNumber = new PhoneNumber();

        phoneNumber.setNumber(row.getString(PHONE_NO));
        phoneNumber.setAreaCode(row.getString(PHONE_NUMBER_AREA_CODE));
        phoneNumber.setCountryCode(row.getString(PHONE_NUMBER_COUNTRY_CODE));
        phoneNumber.setExtension(row.getString(PHONE_NUMBER_EXTENSION));

        primaryContactNumber.setNumber(row.getString(PRIMARY_CONTACT_NO));
        primaryContactNumber.setAreaCode(row.getString(PRIMARY_CONTACT_NUMBER_AREA_CODE));
        primaryContactNumber.setCountryCode(row.getString(PRIMARY_CONTACT_NUMBER_COUNTRY_CODE));
        primaryContactNumber.setExtension(row.getString(PRIMARY_CONTACT_NUMBER_EXTENSION));

        if (primaryContactNumber.getNumber() != null) {
            patientMapper.setPrimaryContactNumber(primaryContactNumber);
        }

        if (phoneNumber.getNumber() != null) {
            patientMapper.setPhoneNumber(phoneNumber);
        }

        if (permanentaddress.getCountryCode() != null) {
            if (permanentaddress.getCountryCode() == "050" && permanentaddress.getDistrictId() != null) {
                patientMapper.setPermanentAddress(permanentaddress);
            }

            if (permanentaddress.getCountryCode() != "050") {
                patientMapper.setPermanentAddress(permanentaddress);
            }
        }

        patientMapper.setCreatedAt(row.getDate("created_at"));
        patientMapper.setUpdatedAt(row.getDate("updated_at"));

        return patientMapper;
    }

    public Patient getEntityFromPatientMapper(PatientDto p) {
        return getEntityFromPatientMapper(p, new Patient());
    }
}
