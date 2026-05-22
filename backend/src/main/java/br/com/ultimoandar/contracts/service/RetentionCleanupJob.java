package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.config.RetentionProperties;
import br.com.ultimoandar.contracts.repository.AuditEventRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RetentionCleanupJob {

    private final AuditEventRepository auditRepository;
    private final RetentionProperties properties;

    public RetentionCleanupJob(AuditEventRepository auditRepository, RetentionProperties properties) {
        this.auditRepository = auditRepository;
        this.properties = properties;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void cleanupAuditEvents() {
        if (properties.auditDays() <= 0) {
            return;
        }
        auditRepository.deleteByCreatedAtBefore(Instant.now().minus(properties.auditDays(), ChronoUnit.DAYS));
    }
}
