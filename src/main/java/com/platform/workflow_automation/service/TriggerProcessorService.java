package com.platform.workflow_automation.service;

import org.springframework.stereotype.Service;

@Service
public interface TriggerProcessorService {
    void fetchAndEnqueuePendingTriggers();
    void triggerActions();
}
