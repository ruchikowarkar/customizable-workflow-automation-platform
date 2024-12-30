package com.platform.workflow_automation.service;

import com.platform.workflow_automation.entity.Action;
import com.platform.workflow_automation.entity.Trigger;
import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.repository.ActionRepository;
import com.platform.workflow_automation.repository.TriggerRepository;
import com.platform.workflow_automation.repository.WorkflowRepository;
import com.platform.workflow_automation.service.impl.TriggerProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.platform.workflow_automation.util.Constant.PENDING;
import static com.platform.workflow_automation.util.Constant.REDIS_QUEUE_KEY;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriggerProcessorServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private TriggerRepository triggerRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TriggerProcessorServiceImpl triggerProcessorService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void testFetchAndEnqueuePendingTriggers() {
        Trigger mockTrigger = new Trigger();
        mockTrigger.setTriggerId(UUID.randomUUID());
        mockTrigger.setStatus(PENDING);
        when(triggerRepository.getTriggersByStatus(PENDING)).thenReturn(Collections.singletonList(mockTrigger));

        triggerProcessorService.fetchAndEnqueuePendingTriggers();

        verify(redisTemplate.opsForList()).rightPush(REDIS_QUEUE_KEY, mockTrigger);
        verify(triggerRepository).getTriggersByStatus(PENDING);
    }

    @Test
    void testTriggerActions() {
        UUID triggerId = UUID.randomUUID();
        UUID workflowId = UUID.randomUUID();

        Trigger mockTrigger = new Trigger();
        mockTrigger.setTriggerId(triggerId);
        mockTrigger.setWorkflowId(workflowId);
        mockTrigger.setEmailTo("test@example.com");

        Workflow mockWorkflow = new Workflow();
        mockWorkflow.setWorkflowId(workflowId);
        mockWorkflow.setWorkflowName("Test Workflow");
        mockWorkflow.setDescription("Test Description");

        Action mockAction = new Action();
        mockAction.setActionId(UUID.randomUUID());
        mockAction.setTriggerId(triggerId);

        ListOperations<String, Object> listOperations = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.leftPop(anyString(), eq(1L), eq(TimeUnit.SECONDS))).thenReturn(mockTrigger);
        when(redisTemplate.hasKey(anyString())).thenReturn(true, false);
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(mockWorkflow));

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(actionRepository.save(any(Action.class))).thenReturn(mockAction);

        triggerProcessorService.triggerActions();

        verify(redisTemplate, times(1)).opsForList();
        verify(listOperations, times(1)).leftPop(anyString(), eq(1L), eq(TimeUnit.SECONDS));
        verify(workflowRepository).findById(workflowId);
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(actionRepository).save(any(Action.class));
    }


    @Test
    void testTriggerActions_WorkflowNotFound() {
        Trigger mockTrigger = new Trigger();
        mockTrigger.setTriggerId(UUID.randomUUID());
        mockTrigger.setWorkflowId(UUID.randomUUID());

        ListOperations<String, Object> listOperations = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        when(redisTemplate.hasKey("triggerQueue")).thenReturn(true, false);
        when(listOperations.leftPop(eq("triggerQueue"), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(mockTrigger);
        when(workflowRepository.findById(mockTrigger.getWorkflowId()))
                .thenThrow(new RuntimeException("Workflow not found for ID: " + mockTrigger.getWorkflowId()));

        try {
            triggerProcessorService.triggerActions();
        } catch (RuntimeException e) {
        }

        verify(redisTemplate, times(2)).hasKey("triggerQueue");
        verify(redisTemplate, times(1)).opsForList();
        verify(listOperations, times(1)).leftPop(eq("triggerQueue"), anyLong(), eq(TimeUnit.SECONDS));
        verify(workflowRepository).findById(mockTrigger.getWorkflowId());
    }
    
}
