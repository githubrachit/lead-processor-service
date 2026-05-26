package com.leadplatform.processor.repository;

import com.leadplatform.processor.entity.LeadAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadAuditRepository extends JpaRepository<LeadAudit, Long> {
    List<LeadAudit> findByLeadIdOrderByCreatedAtDesc(String leadId);
}
