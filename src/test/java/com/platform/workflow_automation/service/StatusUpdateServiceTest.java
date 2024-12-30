package com.platform.workflow_automation.service;

import com.platform.workflow_automation.dto.StatusUpdateRequest;
import com.platform.workflow_automation.entity.Trigger;
import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.repository.TriggerRepository;
import com.platform.workflow_automation.repository.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.platform.workflow_automation.util.Constant.COMPLETED;
import static com.platform.workflow_automation.util.Constant.PENDING;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatusUpdateServiceTest {

    private StatusUpdateService statusUpdateService;

    @Mock
    private TriggerRepository triggerRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    private StatusUpdateRequest statusUpdateRequest;

    private UUID workflowId;
    private String email;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        statusUpdateService = new StatusUpdateService() {
            @Override
            public void triggerStatusUpdate(StatusUpdateRequest statusUpdateRequest) {
                Trigger trigger = triggerRepository.getTriggerByWorkflowIdEmail(
                        statusUpdateRequest.getWorkflowId(), statusUpdateRequest.getEmail());

                if (trigger == null) {
                    return;
                }

                trigger.setStatus(COMPLETED);
                triggerRepository.save(trigger);

                List<Trigger> triggers = triggerRepository.getTriggersByWorkflowIdStatus(
                        statusUpdateRequest.getWorkflowId(), PENDING);

                if (triggers.isEmpty()) {
                    Workflow workflow = workflowRepository.findById(statusUpdateRequest.getWorkflowId())
                            .orElseThrow(() -> new RuntimeException("Workflow not found"));

                    workflow.setStatus(COMPLETED);
                    workflowRepository.save(workflow);
                }
            }
        };

        workflowId = UUID.randomUUID();
        email = "test@example.com";

        statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setWorkflowId(workflowId);
        statusUpdateRequest.setEmail(email);
    }

    @Test
    void testTriggerStatusUpdate_Success_WithoutPendingTriggers() {
        Trigger mockTrigger = new Trigger();
        mockTrigger.setTriggerId(UUID.randomUUID());
        mockTrigger.setWorkflowId(workflowId);
        mockTrigger.setEmailTo(email);
        mockTrigger.setStatus(PENDING);

        when(triggerRepository.getTriggerByWorkflowIdEmail(workflowId, email)).thenReturn(mockTrigger);
        when(triggerRepository.getTriggersByWorkflowIdStatus(workflowId, PENDING)).thenReturn(new ArrayList<>());

        Workflow mockWorkflow = new Workflow();
        mockWorkflow.setWorkflowId(workflowId);
        mockWorkflow.setStatus(PENDING);

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(mockWorkflow));

        statusUpdateService.triggerStatusUpdate(statusUpdateRequest);

        verify(triggerRepository, times(1)).getTriggerByWorkflowIdEmail(workflowId, email);
        verify(triggerRepository, times(1)).save(mockTrigger);
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, times(1)).save(mockWorkflow);
    }

    @Test
    void testTriggerStatusUpdate_Success_WithPendingTriggers() {
        Trigger mockTrigger = new Trigger();
        mockTrigger.setTriggerId(UUID.randomUUID());
        mockTrigger.setWorkflowId(workflowId);
        mockTrigger.setEmailTo(email);
        mockTrigger.setStatus(PENDING);

        when(triggerRepository.getTriggerByWorkflowIdEmail(workflowId, email)).thenReturn(mockTrigger);

        List<Trigger> pendingTriggers = new ArrayList<>();
        pendingTriggers.add(new Trigger());
        when(triggerRepository.getTriggersByWorkflowIdStatus(workflowId, PENDING)).thenReturn(pendingTriggers);

        statusUpdateService.triggerStatusUpdate(statusUpdateRequest);

        verify(triggerRepository, times(1)).getTriggerByWorkflowIdEmail(workflowId, email);
        verify(triggerRepository, times(1)).save(mockTrigger);
        verify(workflowRepository, never()).findById(any());
        verify(workflowRepository, never()).save(any());
    }
}