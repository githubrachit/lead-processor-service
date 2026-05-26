package com.leadplatform.processor.service.impl;

import com.leadplatform.processor.dto.LeadMessage;
import com.leadplatform.processor.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestTemplate restTemplate;

    public ExternalApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean processLead(LeadMessage leadMessage) {
        log.info("Calling external API for leadId={}, correlationId={}",
                leadMessage.getLeadId(), leadMessage.getCorrelationId());

        String leadId = leadMessage.getLeadId();
        String status = "200";

        if (leadId != null && leadId.contains("500")) {
            status = "500";
        }

        try {
            String response = restTemplate.getForObject(
                    "https://httpbin.org/status/" + status,
                    String.class
            );

            log.info("External API call successful for leadId={}. Response: {}", leadId, response);

        } catch (HttpStatusCodeException e) {
            log.info("External API error for leadId={}. Status Code: {}, Body: {}",
                    leadId, e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.info("Network or unexpected failure calling external API for leadId={}: {}", leadId, e.getMessage());
            return false;
        }

        try {
            Thread.sleep(100); // simulate latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Thread interrupted during latency simulation");
        }

        return true;
    }
}
