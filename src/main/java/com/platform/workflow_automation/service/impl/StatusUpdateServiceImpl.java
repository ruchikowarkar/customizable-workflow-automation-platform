package com.platform.workflow_automation.service.impl;

import com.platform.workflow_automation.dto.StatusUpdateRequest;
import com.platform.workflow_automation.entity.Trigger;
import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.repository.TriggerRepository;
import com.platform.workflow_automation.repository.WorkflowRepository;
import com.platform.workflow_automation.service.StatusUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.platform.workflow_automation.util.Constant.COMPLETED;
import static com.platform.workflow_automation.util.Constant.PENDING;

@Service
@Slf4j
public class StatusUpdateServiceImpl implements StatusUpdateService {

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Override
    public void triggerStatusUpdate(StatusUpdateRequest statusUpdateRequest) {
        log.info("Processing status update request for Workflow ID: {}, Email: {}",
                statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail());

        try {
            Trigger trigger = triggerRepository.getTriggerByWorkflowIdEmail(
                    statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail());

            if (trigger == null) {
                log.warn("No trigger found for Workflow ID: {} and Email: {}",
                        statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail());
                return;
            }

            trigger.setStatus(COMPLETED);
            triggerRepository.save(trigger);
            log.info("Trigger status updated to COMPLETED for Trigger ID: {}", trigger.getTriggerId());

            List<Trigger> triggers = triggerRepository.getTriggersByWorkflowIdStatus(
                    statusUpdateRequest.getWorkflowId(), PENDING);

            if (triggers.isEmpty()) {
                log.info("No pending triggers found for Workflow ID: {}. Updating workflow status to {}.",
                        statusUpdateRequest.getWorkflowId(), COMPLETED);

                Workflow workflow = workflowRepository.findById(statusUpdateRequest.getWorkflowId())
                        .orElseThrow(() -> {
                            log.error("Workflow not found for ID: {}", statusUpdateRequest.getWorkflowId());
                            return new RuntimeException("Workflow not found for ID: " + statusUpdateRequest.getWorkflowId());
                        });

                workflow.setStatus(COMPLETED);
                workflowRepository.save(workflow);
                log.info("Workflow status updated to {} for Workflow ID: {}", COMPLETED, workflow.getWorkflowId());
            } else {
                log.info("Pending triggers still exist for Workflow ID: {}. Workflow status remains unchanged.",
                        statusUpdateRequest.getWorkflowId());
            }
        } catch (Exception e) {
            log.error("Error occurred while processing status update for Workflow ID: {}, Email: {}. Error: {}",
                    statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error processing status update: " + e.getMessage(), e);
        }

        log.info("Status update process completed for Workflow ID: {}, Email: {}",
                statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail());
    }
}