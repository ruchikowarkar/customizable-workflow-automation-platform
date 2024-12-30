package com.platform.workflow_automation.service.impl;

import com.platform.workflow_automation.entity.Action;
import com.platform.workflow_automation.entity.Trigger;
import com.platform.workflow_automation.entity.Workflow;
import com.platform.workflow_automation.repository.ActionRepository;
import com.platform.workflow_automation.repository.TriggerRepository;
import com.platform.workflow_automation.repository.WorkflowRepository;
import com.platform.workflow_automation.service.TriggerProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static com.platform.workflow_automation.util.Constant.PENDING;
import static com.platform.workflow_automation.util.Constant.REDIS_QUEUE_KEY;

@Service
@Slf4j
public class TriggerProcessorServiceImpl implements TriggerProcessorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void fetchAndEnqueuePendingTriggers() {
        log.info("Fetching pending triggers with status 'PENDING'.");
        List<Trigger> alertTriggers = triggerRepository.getTriggersByStatus(PENDING);

        for (Trigger trigger : alertTriggers) {
            redisTemplate.opsForList().rightPush(REDIS_QUEUE_KEY, trigger);
            log.info("Enqueued trigger ID: {} to Redis queue.", trigger.getTriggerId());
        }

        log.info("Fetched and enqueued {} pending triggers to Redis.", alertTriggers.size());
    }

    @Override
    public void triggerActions() {
        log.info("Starting to process triggers from Redis queue.");
        while (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_QUEUE_KEY))) {
            log.info("Checking for next trigger in Redis queue.");
            Trigger trigger = (Trigger) redisTemplate.opsForList().leftPop(REDIS_QUEUE_KEY, 1, TimeUnit.SECONDS);

            if (trigger == null) {
                log.warn("No trigger found in Redis queue or timeout reached.");
                break;
            }

            log.info("Processing trigger ID: {}.", trigger.getTriggerId());

            try {
                Workflow workflow = workflowRepository.findById(trigger.getWorkflowId())
                        .orElseThrow(() -> new RuntimeException("Workflow not found for ID: " + trigger.getWorkflowId()));

                log.info("Found workflow ID: {} for trigger ID: {}.", workflow.getWorkflowId(), trigger.getTriggerId());

                String emailSubject = "Workflow Alert: " + workflow.getWorkflowName();
                String emailText = "Workflow Description: " + workflow.getDescription() + "\n\nThis is an automated alert.";

                log.info("Sending email for trigger ID: {} to recipient: {}.", trigger.getTriggerId(), trigger.getEmailTo());
                sendEmail(trigger.getEmailTo(), emailSubject, emailText);

                UUID actionId = UUID.randomUUID();

                Action action = new Action();
                action.setActionId(actionId);
                action.setTriggerId(trigger.getTriggerId());
                action.setEmailTo(trigger.getEmailTo());
                action.setEmailSubject(emailSubject);
                action.setEmailBody(emailText);
                action.setExecutionDate(LocalDate.now());

                actionRepository.save(action);

                log.info("Action ID: {} created for trigger ID: {}.", actionId, trigger.getTriggerId());

            } catch (Exception e) {
                log.error("Error processing trigger ID: {}: {}", trigger.getTriggerId(), e.getMessage(), e);
            }
        }
        log.info("All triggers in Redis queue processed.");
    }

    private void sendEmail(String recipientEmail, String emailSubject, String emailText) {
        try {
            log.info("Preparing email for recipient: {}.", recipientEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject(emailSubject);
            message.setText(emailText);
            mailSender.send(message);

            log.info("Email sent successfully to {}.", recipientEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage(), e);
        }
    }
}