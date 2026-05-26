package com.leadplatform.processor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.processor.dto.LeadMessage;
import com.leadplatform.processor.entity.LeadRequest;
import com.leadplatform.processor.repository.LeadRequestRepository;
import com.leadplatform.processor.service.AuditService;
import com.leadplatform.processor.service.ExternalApiService;
import com.leadplatform.processor.service.LeadProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LeadProcessingServiceImpl implements LeadProcessingService {

    private final LeadRequestRepository leadRequestRepository;
    private final ExternalApiService externalApiService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public LeadProcessingServiceImpl(LeadRequestRepository leadRequestRepository,
                                     ExternalApiService externalApiService,
                                     AuditService auditService,
                                     ObjectMapper objectMapper) {
        this.leadRequestRepository = leadRequestRepository;
        this.externalApiService = externalApiService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void processLead(LeadMessage leadMessage) {
        String leadId = leadMessage.getLeadId();
        String correlationId = leadMessage.getCorrelationId();
        long startTime = System.currentTimeMillis();

        log.info("Processing lead. leadId={}, correlationId={}", leadId, correlationId);

        try {
            // Check for duplicate
            LeadRequest existingLead = leadRequestRepository.findByLeadId(leadId).orElse(null);

            if (existingLead != null && "SUCCESS".equals(existingLead.getStatus())) {
                log.info("Lead already processed successfully. leadId={}, correlationId={}", leadId, correlationId);
                auditService.logEvent(leadId, "DUPLICATE_SKIPPED", "Lead already in SUCCESS state");
                return;
            }

            // Create or update lead record
            LeadRequest leadRequest;
            if (existingLead != null) {
                leadRequest = existingLead;
                leadRequest.setRetryCount(leadRequest.getRetryCount() + 1);
            } else {
                String payload = objectMapper.writeValueAsString(leadMessage);
                leadRequest = LeadRequest.builder()
                        .leadId(leadId)
                        .requestPayload(payload)
                        .status("PROCESSING")
                        .retryCount(0)
                        .build();
            }

            leadRequest.setStatus("PROCESSING");
            leadRequestRepository.save(leadRequest);
            auditService.logEvent(leadId, "PROCESSING_STARTED",
                    "correlationId=" + correlationId + ", retryCount=" + leadRequest.getRetryCount());

            // Call external API
            boolean success = externalApiService.processLead(leadMessage);

            if (success) {
                leadRequest.setStatus("SUCCESS");
                leadRequest.setErrorMessage(null);
                leadRequestRepository.save(leadRequest);
                auditService.logEvent(leadId, "PROCESSING_SUCCESS", "Lead processed successfully");
            } else {
                throw new RuntimeException("External API returned failure");
            }

            long latency = System.currentTimeMillis() - startTime;
            log.info("Lead processed successfully. leadId={}, correlationId={}, latencyMs={}",
                    leadId, correlationId, latency);

        } catch (Exception e) {
            handleProcessingFailure(leadMessage, e);
            throw new RuntimeException("Lead processing failed for leadId=" + leadId, e);
        }
    }

    private void handleProcessingFailure(LeadMessage leadMessage, Exception e) {
        String leadId = leadMessage.getLeadId();
        log.info("Lead processing failed. leadId={}, correlationId={}, error={}",
                leadId, leadMessage.getCorrelationId(), e.getMessage());

        try {
            LeadRequest leadRequest = leadRequestRepository.findByLeadId(leadId).orElse(null);
            if (leadRequest != null) {
                leadRequest.setStatus("FAILED");
                leadRequest.setErrorMessage(e.getMessage());
                leadRequestRepository.save(leadRequest);
            }
            auditService.logEvent(leadId, "PROCESSING_FAILED", "Error: " + e.getMessage());
        } catch (Exception dbError) {
            log.info("Failed to update lead status in DB. leadId={}, error={}",
                    leadId, dbError.getMessage());
        }
    }
}
