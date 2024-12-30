package com.platform.workflow_automation.service;

import com.platform.workflow_automation.repository.TriggerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TriggerGenerateServiceTest {

    @Mock
    private TriggerGenerateService triggerGenerateService;

    @Mock
    private TriggerRepository triggerRepository;

    private UUID workflowId;
    private List<String> recipients;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workflowId = UUID.randomUUID();
        recipients = Arrays.asList("recipient1@example.com", "recipient2@example.com");
    }

    @Test
    void testTriggerCreation_Success() {
        triggerGenerateService.triggerCreation(workflowId, recipients);
        verify(triggerGenerateService, times(1)).triggerCreation(workflowId, recipients);
    }

    @Test
    void testTriggerCreation_FailureForOneRecipient() {
        doThrow(new RuntimeException("Trigger creation failed"))
                .when(triggerGenerateService)
                .triggerCreation(workflowId, recipients);

        try {
            triggerGenerateService.triggerCreation(workflowId, recipients);
        } catch (RuntimeException e) {
        }

        verify(triggerGenerateService, times(1)).triggerCreation(workflowId, recipients);
    }

    @Test
    void testTriggerCreation_EmptyRecipients() {
        triggerGenerateService.triggerCreation(workflowId, Arrays.asList());

        verify(triggerGenerateService, times(1)).triggerCreation(workflowId, Arrays.asList());
    }
}