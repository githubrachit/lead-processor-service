package com.leadplatform.processor.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.processor.dto.LeadMessage;
import com.leadplatform.processor.service.LeadProcessingService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeadSqsListener {

    private final LeadProcessingService leadProcessingService;
    private final ObjectMapper objectMapper;

    public LeadSqsListener(LeadProcessingService leadProcessingService, ObjectMapper objectMapper) {
        this.leadProcessingService = leadProcessingService;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${app.sqs.lead-processing-queue}")
    public void onMessage(String rawMessage) {
        log.info("Received SQS message");

        try {
            // SNS wraps the message in an envelope
            JsonNode snsEnvelope = objectMapper.readTree(rawMessage);
            String actualMessage;

            if (snsEnvelope.has("Message")) {
                actualMessage = snsEnvelope.get("Message").asText();
            } else {
                actualMessage = rawMessage;
            }

            LeadMessage leadMessage = objectMapper.readValue(actualMessage, LeadMessage.class);
            log.info("Processing message. leadId={}, correlationId={}",
                    leadMessage.getLeadId(), leadMessage.getCorrelationId());

            leadProcessingService.processLead(leadMessage);

        } catch (Exception e) {
            log.info("Error processing SQS message. error={}", e.getMessage(), e);
            throw new RuntimeException("Failed to process SQS message", e);
        }
    }
}
