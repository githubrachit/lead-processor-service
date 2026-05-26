package com.leadplatform.processor.service;

/**
 * Service interface for audit logging.
 */
public interface AuditService {

    void logEvent(String leadId, String eventName, String remarks);
}
