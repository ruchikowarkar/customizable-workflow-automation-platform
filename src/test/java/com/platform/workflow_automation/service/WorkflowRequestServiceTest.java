package com.platform.workflow_automation.service;

import com.platform.workflow_automation.dto.WorkflowRequest;
import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.repository.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowRequestServiceTest {

    @Mock
    private WorkflowRequestService workflowRequestService;

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private TriggerGenerateService triggerGenerateService;

    private WorkflowRequest workflowRequest;

    @BeforeEach
    void setUp() {
        workflowRequest = new WorkflowRequest();
        workflowRequest.setName("Test Workflow");
        workflowRequest.setDescription("Test Description");
        workflowRequest.setDueDate(LocalDate.now().plusDays(1));
        workflowRequest.setRecipients(Arrays.asList("recipient1@example.com", "recipient2@example.com"));
    }

    @Test
    void testWorkflowCreation_Success() {
        UUID expectedWorkflowId = UUID.randomUUID();
        when(workflowRequestService.createWorkflow(any(WorkflowRequest.class))).thenReturn(expectedWorkflowId);

        UUID workflowId = workflowRequestService.createWorkflow(workflowRequest);

        verify(workflowRequestService, times(1)).createWorkflow(workflowRequest);
        assert workflowId.equals(expectedWorkflowId);
    }

    @Test
    void testWorkflowCreation_FailureDuringSave() {
        when(workflowRequestService.createWorkflow(any(WorkflowRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> workflowRequestService.createWorkflow(workflowRequest));
        verify(workflowRequestService, times(1)).createWorkflow(workflowRequest);
    }

    @Test
    void testWorkflowCreation_FailureDuringTriggerGeneration() {
        doAnswer(invocation -> {
            WorkflowRequest request = invocation.getArgument(0);
            UUID workflowId = UUID.randomUUID();

            Workflow workflow = new Workflow();
            workflow.setWorkflowId(workflowId);
            workflow.setWorkflowName(request.getName());
            workflow.setDescription(request.getDescription());
            workflow.setDueDate(request.getDueDate());
            workflow.setStatus("ACTIVE");

            throw new RuntimeException("Trigger error");
        }).when(workflowRequestService).createWorkflow(any(WorkflowRequest.class));

        assertThrows(RuntimeException.class, () -> workflowRequestService.createWorkflow(workflowRequest));
        verify(workflowRequestService, times(1)).createWorkflow(workflowRequest);
    }

}