package org.sharedhealth.mci.web.mapper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Catchment {

    private static final String INVALID_DIVISION_OR_DISTRICT = "Division ID and/or district ID cannot be blank";
    private static final String INVALID_UPAZILA = "Upazila ID cannot be blank";
    private static final String INVALID_CITY_CORP = "City corporation ID cannot be blank";
    private static final String INVALID_UNION_OR_URBAN_WARD = "Union/Urban ward IDcannot be blank";
    private String divisionId;
    private String districtId;
    private String upazilaId;
    private String cityCorpId;
    private String unionOrUrbanWardId;
    private String ruralWardId;
    private Object ids;

    public Catchment(String divisionId, String districtId) {
        this(divisionId, districtId, null);
    }

    public Catchment(String divisionId, String districtId, String upazilaId) {
        if (isBlank(divisionId) || isBlank(districtId)) {
            throw new IllegalArgumentException(INVALID_DIVISION_OR_DISTRICT);
        }
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.upazilaId = upazilaId;
    }

    public String getId() {
        StringBuilder id = new StringBuilder(format("A%sB%s", divisionId, districtId));
        if (isNotBlank(upazilaId)) {
            id.append(format("C%s", upazilaId));

            if (isNotBlank(cityCorpId)) {
                id.append(format("D%s", cityCorpId));

                if (isNotBlank(unionOrUrbanWardId)) {
                    id.append(format("E%s", unionOrUrbanWardId));

                    if (isNotBlank(ruralWardId)) {
                        id.append(format("F%s", ruralWardId));
                    }
                }
            }
        }
        return id.toString();
    }

    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        StringBuilder id = new StringBuilder(format("A%sB%s", divisionId, districtId));
        ids.add(id.toString());

        if (isNotBlank(upazilaId)) {
            id.append(format("C%s", upazilaId));
            ids.add(id.toString());

            if (isNotBlank(cityCorpId)) {
                id.append(format("D%s", cityCorpId));
                ids.add(id.toString());

                if (isNotBlank(unionOrUrbanWardId)) {
                    id.append(format("E%s", unionOrUrbanWardId));
                    ids.add(id.toString());

                    if (isNotBlank(ruralWardId)) {
                        id.append(format("F%s", ruralWardId));
                        ids.add(id.toString());
                    }
                }
            }
        }
        return ids;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public String getCityCorpId() {
        return cityCorpId;
    }

    public void setCityCorpId(String cityCorpId) {
        if (isBlank(upazilaId)) {
            throw new IllegalArgumentException(INVALID_UPAZILA);
        }
        this.cityCorpId = cityCorpId;
    }

    public String getUnionOrUrbanWardId() {
        return unionOrUrbanWardId;
    }

    public void setUnionOrUrbanWardId(String unionOrUrbanWardId) {
        if (isBlank(cityCorpId)) {
            throw new IllegalArgumentException(INVALID_CITY_CORP);
        }
        this.unionOrUrbanWardId = unionOrUrbanWardId;
    }

    public String getRuralWardId() {
        return ruralWardId;
    }

    public void setRuralWardId(String ruralWardId) {
        if (isBlank(unionOrUrbanWardId)) {
            throw new IllegalArgumentException(INVALID_UNION_OR_URBAN_WARD);
        }
        this.ruralWardId = ruralWardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catchment)) return false;

        Catchment catchment = (Catchment) o;

        if (cityCorpId != null ? !cityCorpId.equals(catchment.cityCorpId) : catchment.cityCorpId != null) return false;
        if (districtId != null ? !districtId.equals(catchment.districtId) : catchment.districtId != null) return false;
        if (!divisionId.equals(catchment.divisionId)) return false;
        if (ruralWardId != null ? !ruralWardId.equals(catchment.ruralWardId) : catchment.ruralWardId != null)
            return false;
        if (unionOrUrbanWardId != null ? !unionOrUrbanWardId.equals(catchment.unionOrUrbanWardId) : catchment.unionOrUrbanWardId != null)
            return false;
        if (upazilaId != null ? !upazilaId.equals(catchment.upazilaId) : catchment.upazilaId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = divisionId.hashCode();
        result = 31 * result + (districtId != null ? districtId.hashCode() : 0);
        result = 31 * result + (upazilaId != null ? upazilaId.hashCode() : 0);
        result = 31 * result + (cityCorpId != null ? cityCorpId.hashCode() : 0);
        result = 31 * result + (unionOrUrbanWardId != null ? unionOrUrbanWardId.hashCode() : 0);
        result = 31 * result + (ruralWardId != null ? ruralWardId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Catchment{");
        sb.append("divisionId='").append(divisionId).append('\'');
        sb.append(", districtId='").append(districtId).append('\'');
        sb.append(", upazilaId='").append(upazilaId).append('\'');
        sb.append(", cityCorpId='").append(cityCorpId).append('\'');
        sb.append(", unionOrUrbanWardId='").append(unionOrUrbanWardId).append('\'');
        sb.append(", ruralWardId='").append(ruralWardId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
