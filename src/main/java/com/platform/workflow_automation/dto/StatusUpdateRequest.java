package com.platform.workflow_automation.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatusUpdateRequest {
    UUID workflowId;
    String email;
}
