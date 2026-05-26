package com.leadplatform.processor.exception;

public class LeadNotFoundException extends RuntimeException {
    public LeadNotFoundException(String leadId) {
        super("Lead not found: " + leadId);
    }
}
