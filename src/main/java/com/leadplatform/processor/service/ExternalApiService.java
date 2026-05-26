package com.leadplatform.processor.service;

import com.leadplatform.processor.dto.LeadMessage;

/**
 * Service interface for calling external/downstream APIs.
 */
public interface ExternalApiService {

    boolean processLead(LeadMessage leadMessage);
}
