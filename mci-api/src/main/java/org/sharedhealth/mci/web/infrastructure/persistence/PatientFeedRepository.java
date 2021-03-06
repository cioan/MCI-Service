package org.sharedhealth.mci.web.infrastructure.persistence;

import org.sharedhealth.mci.web.model.PatientUpdateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.sharedhealth.mci.web.infrastructure.persistence.PatientUpdateLogQueryBuilder.buildFindUpdateLogStmt;

@Component
public class PatientFeedRepository extends BaseRepository {

    @Autowired
    public PatientFeedRepository(@Qualifier("MCICassandraTemplate") CassandraOperations cassandraOperations) {
        super(cassandraOperations);
    }

    public List<PatientUpdateLog> findPatientsUpdatedSince(Date since, int limit, UUID lastMarker) {
        return cassandraOps.select(buildFindUpdateLogStmt(since, limit, lastMarker),
                PatientUpdateLog.class);
    }

    public List<PatientUpdateLog> findPatientsUpdatedSince(UUID lastMarker, int limit) {
        return cassandraOps.select(buildFindUpdateLogStmt(lastMarker, limit),
                PatientUpdateLog.class);
    }
}
