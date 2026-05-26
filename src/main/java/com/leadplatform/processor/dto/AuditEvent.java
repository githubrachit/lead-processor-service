package com.leadplatform.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an audit event message sent to the audit queue.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    private String leadId;
    private String eventName;
    private String remarks;
    private String timestamp;
}
