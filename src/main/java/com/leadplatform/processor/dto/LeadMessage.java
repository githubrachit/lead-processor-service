package com.leadplatform.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadMessage {
    private String correlationId;
    private String leadId;
    private String name;
    private String mobile;
    private String email;
}
