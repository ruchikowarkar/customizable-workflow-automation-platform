package com.platform.workflow_automation.controller.scheduler;

import com.platform.workflow_automation.service.TriggerProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.platform.workflow_automation.util.Constant.CRON_EXPRESSION;
import static com.platform.workflow_automation.util.Constant.TIME_ZONE;

@Component
@Slf4j
public class TriggerScheduler {

    @Autowired
    private TriggerProcessorService triggerProcessorService;

    @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    public void scheduleTriggerProcessing() {
        log.info("Scheduled task started: Fetching and processing pending triggers.");

        try {
            log.debug("Fetching pending triggers...");
            triggerProcessorService.fetchAndEnqueuePendingTriggers();
            log.info("Successfully fetched and enqueued pending triggers.");

            log.debug("Executing trigger actions...");
            triggerProcessorService.triggerActions();
            log.info("Trigger actions executed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during scheduled task: {}", e.getMessage(), e);
        }

        log.info("Scheduled task completed: All pending triggers processed.");
    }
}