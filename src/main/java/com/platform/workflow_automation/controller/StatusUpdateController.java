package com.platform.workflow_automation.controller;

import com.platform.workflow_automation.dto.StatusUpdateRequest;
import com.platform.workflow_automation.service.StatusUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/status")
@Slf4j
public class StatusUpdateController {

    @Autowired
    private StatusUpdateService statusUpdateService;

    @PostMapping("/update")
    public ResponseEntity<Void> triggerStatusUpdate(@RequestBody StatusUpdateRequest statusUpdateRequest) {
        log.info("Received request to trigger status update: {}", statusUpdateRequest);
        try {
            log.debug("Starting status update process...");
            statusUpdateService.triggerStatusUpdate(statusUpdateRequest);
            log.info("Status update process completed successfully for request: {}", statusUpdateRequest);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error occurred while processing status update for request: {}. Error: {}",
                    statusUpdateRequest, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}