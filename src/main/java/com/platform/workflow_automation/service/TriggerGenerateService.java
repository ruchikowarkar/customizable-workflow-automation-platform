package com.platform.workflow_automation.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TriggerGenerateService {
    void triggerCreation(UUID workflowId, List<String> recipients);
}
