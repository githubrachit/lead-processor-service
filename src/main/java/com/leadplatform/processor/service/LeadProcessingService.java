package com.leadplatform.processor.service;

import com.leadplatform.processor.dto.LeadMessage;

/**
 * Service interface for processing lead messages from SQS.
 */
public interface LeadProcessingService {

    void processLead(LeadMessage leadMessage);
}
