package com.platform.workflow_automation.service.impl;

import com.platform.workflow_automation.entity.Trigger;
import com.platform.workflow_automation.repository.TriggerRepository;
import com.platform.workflow_automation.service.TriggerGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.platform.workflow_automation.util.Constant.PENDING;

@Service
@Slf4j
public class TriggerGenerateServiceImpl implements TriggerGenerateService {

    @Autowired
    private TriggerRepository triggerRepository;

    @Override
    public void triggerCreation(UUID workflowId, List<String> recipients) {
        log.info("Starting trigger creation for Workflow ID: {}", workflowId);

        for (String recipient : recipients) {
            UUID triggerId = UUID.randomUUID();
            log.debug("Generated Trigger ID: {} for recipient: {}", triggerId, recipient);

            Trigger trigger = new Trigger();
            trigger.setTriggerId(triggerId);
            trigger.setWorkflowId(workflowId);
            trigger.setEmailTo(recipient);
            trigger.setStatus(PENDING);

            try {
                triggerRepository.save(trigger);
                log.info("Trigger successfully created for recipient: {} with Trigger ID: {}", recipient, triggerId);
            } catch (Exception e) {
                log.error("Failed to save trigger for recipient: {}. Error: {}", recipient, e.getMessage(), e);
            }
        }

        log.info("Trigger creation completed for Workflow ID: {}", workflowId);
    }
}