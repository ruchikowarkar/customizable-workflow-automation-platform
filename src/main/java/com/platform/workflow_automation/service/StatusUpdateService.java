package com.platform.workflow_automation.service;

import com.platform.workflow_automation.dto.StatusUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface StatusUpdateService {
    void triggerStatusUpdate(StatusUpdateRequest statusUpdateRequest);
}
