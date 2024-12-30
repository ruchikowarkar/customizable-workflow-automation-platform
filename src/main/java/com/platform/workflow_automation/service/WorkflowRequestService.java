package com.platform.workflow_automation.service;

import com.platform.workflow_automation.dto.WorkflowRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface WorkflowRequestService {
    UUID createWorkflow(WorkflowRequest workflowRequest);
}
