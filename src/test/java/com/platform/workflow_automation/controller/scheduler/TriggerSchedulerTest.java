package com.platform.workflow_automation.controller.scheduler;

import com.platform.workflow_automation.service.TriggerProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TriggerSchedulerTest {

    @InjectMocks
    private TriggerScheduler triggerScheduler;

    @MockBean
    private TriggerProcessorService triggerProcessorService;

    @Test
    void testScheduleTriggerProcessing_Success() {
        Mockito.doNothing().when(triggerProcessorService).fetchAndEnqueuePendingTriggers();
        Mockito.doNothing().when(triggerProcessorService).triggerActions();

        triggerScheduler.scheduleTriggerProcessing();

        Mockito.verify(triggerProcessorService, times(1)).fetchAndEnqueuePendingTriggers();
        Mockito.verify(triggerProcessorService, times(1)).triggerActions();
    }

    @Test
    void testScheduleTriggerProcessing_ExceptionHandling() {
        Mockito.doThrow(new RuntimeException("Fetching triggers failed"))
                .when(triggerProcessorService).fetchAndEnqueuePendingTriggers();

        Mockito.doNothing().when(triggerProcessorService).triggerActions();

        triggerScheduler.scheduleTriggerProcessing();

        Mockito.verify(triggerProcessorService, times(1)).fetchAndEnqueuePendingTriggers();
        Mockito.verify(triggerProcessorService, times(0)).triggerActions(); // Ensure triggerActions is not called
    }
}

