package com.platform.workflow_automation.repository;

import com.platform.workflow_automation.entity.Trigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TriggerRepository extends JpaRepository<Trigger, UUID> {
    @Query("SELECT t FROM Trigger t WHERE t.status = :status")
    List<Trigger> getTriggersByStatus(String status);

    @Query("SELECT t FROM Trigger t WHERE t.workflowId = :workflowId AND t.emailTo = :emailTo")
    Trigger getTriggerByWorkflowIdEmail(UUID workflowId, String emailTo);

    @Query("SELECT t FROM Trigger t WHERE t.workflowId = :workflowId AND t.status = :status")
    List<Trigger> getTriggersByWorkflowIdStatus(UUID workflowId, String status);
}
