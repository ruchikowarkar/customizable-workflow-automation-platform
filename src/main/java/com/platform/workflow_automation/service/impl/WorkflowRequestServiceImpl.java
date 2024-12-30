package com.platform.workflow_automation.service.impl;

import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.dto.WorkflowRequest;
import com.platform.workflow_automation.repository.WorkflowRepository;
import com.platform.workflow_automation.service.TriggerGenerateService;
import com.platform.workflow_automation.service.WorkflowRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.platform.workflow_automation.util.Constant.ACTIVE;

@Slf4j
@Service
public class WorkflowRequestServiceImpl implements WorkflowRequestService {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private TriggerGenerateService triggerGenerateService;


    @Override
    public UUID createWorkflow(WorkflowRequest workflowRequest) {
        log.info("Starting workflow creation process.");

        UUID workflowId = UUID.randomUUID();
        log.debug("Generated new Workflow ID: {}", workflowId);

        Workflow workflow = new Workflow();
        workflow.setWorkflowId(workflowId);
        workflow.setWorkflowName(workflowRequest.getName());
        workflow.setDescription(workflowRequest.getDescription());
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setDueDate(workflowRequest.getDueDate());
        workflow.setStatus(ACTIVE);

        log.debug("Workflow entity populated: {}", workflow);

        try {
            workflowRepository.save(workflow);
            log.info("Workflow saved successfully with ID: {}", workflowId);
        } catch (Exception e) {
            log.error("Error saving workflow to the repository: {}", e.getMessage(), e);
            throw e;
        }

        try {
            log.info("Trigger generation process started for Workflow ID: {}", workflowId);
            triggerGenerateService.triggerCreation(workflowId, workflowRequest.getRecipients());
            log.info("Trigger generation process completed successfully for Workflow ID: {}", workflowId);
        } catch (Exception e) {
            log.error("Error during trigger generation for Workflow ID: {}", workflowId, e);
            throw e;
        }

        log.info("Workflow creation process completed successfully for Workflow ID: {}", workflowId);
        return workflowId;
    }
}
