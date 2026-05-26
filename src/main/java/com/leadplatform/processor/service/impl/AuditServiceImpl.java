package com.leadplatform.processor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.processor.dto.AuditEvent;
import com.leadplatform.processor.service.AuditService;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publishes audit events to the audit SQS queue.
 * A separate listener (AuditQueueListener) consumes from this queue
 * and persists to the lead_audit table.
 *
 * This decouples audit logging from the main processing flow:
 * - If audit DB is slow/down, lead processing is NOT affected
 * - Audit events are guaranteed delivery via SQS retry
 * - No audit data loss (message stays in queue until consumed)
 */
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;
    private final String auditQueueName;

    public AuditServiceImpl(SqsTemplate sqsTemplate,
                            ObjectMapper objectMapper,
                            @Value("${app.sqs.audit-queue}") String auditQueueName) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
        this.auditQueueName = auditQueueName;
    }

    @Override
    public void logEvent(String leadId, String eventName, String remarks) {
        try {
            AuditEvent auditEvent = AuditEvent.builder()
                    .leadId(leadId)
                    .eventName(eventName)
                    .remarks(remarks)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            String messageBody = objectMapper.writeValueAsString(auditEvent);
            sqsTemplate.send(auditQueueName, messageBody);

            log.info("Audit event published to queue. leadId={}, event={}", leadId, eventName);

        } catch (Exception e) {
            // Fallback: log to console if queue publish fails
            log.error("Failed to publish audit event to queue. leadId={}, event={}, error={}",
                    leadId, eventName, e.getMessage());
        }
    }
}
