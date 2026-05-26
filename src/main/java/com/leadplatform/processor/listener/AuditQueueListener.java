package com.leadplatform.processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.processor.dto.AuditEvent;
import com.leadplatform.processor.entity.LeadAudit;
import com.leadplatform.processor.repository.LeadAuditRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Consumes audit events from the audit SQS queue and persists them to the database.
 *
 * This listener is decoupled from lead processing:
 * - If this fails, the message stays in the audit queue and retries
 * - Lead processing is never blocked by audit DB writes
 * - Guaranteed at-least-once delivery of audit events
 */
@Component
@Slf4j
public class AuditQueueListener {

    private final LeadAuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public AuditQueueListener(LeadAuditRepository auditRepository, ObjectMapper objectMapper) {
        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${app.sqs.audit-queue}")
    public void onAuditMessage(String rawMessage) {
        log.debug("Received audit queue message");

        try {
            AuditEvent auditEvent = objectMapper.readValue(rawMessage, AuditEvent.class);

            LeadAudit audit = LeadAudit.builder()
                    .leadId(auditEvent.getLeadId())
                    .eventName(auditEvent.getEventName())
                    .remarks(auditEvent.getRemarks())
                    .build();

            auditRepository.save(audit);

            log.info("Audit persisted. leadId={}, event={}", auditEvent.getLeadId(), auditEvent.getEventName());

        } catch (Exception e) {
            log.error("Failed to process audit message. error={}", e.getMessage(), e);
            // Throwing exception = message NOT acknowledged = SQS will retry
            throw new RuntimeException("Failed to persist audit event", e);
        }
    }
}
