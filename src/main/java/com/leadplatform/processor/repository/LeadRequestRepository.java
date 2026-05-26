package com.leadplatform.processor.repository;

import com.leadplatform.processor.entity.LeadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadRequestRepository extends JpaRepository<LeadRequest, Long> {
    Optional<LeadRequest> findByLeadId(String leadId);
    boolean existsByLeadId(String leadId);
}
