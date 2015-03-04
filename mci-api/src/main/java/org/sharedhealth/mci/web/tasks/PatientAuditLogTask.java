package org.sharedhealth.mci.web.tasks;

import org.sharedhealth.mci.web.service.PatientAuditService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@EnableScheduling
public class PatientAuditLogTask {

    private static final Logger logger = getLogger(PatientAuditLogTask.class);

    @Autowired
    PatientAuditService auditService;

    @Scheduled(cron = "${AUDIT_LOG_SYNC_CRON_EXP}")
    public void execute() {
        try {
            logger.info("Syncing audit log.");
            auditService.sync();

        } catch (Exception e) {
            logger.info("Failed to sync audit log.", e);
        }
    }
}
