package com.platform.workflow_automation.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowRequest {
    String name;
    String description;
    LocalDate dueDate;
    List<String> recipients;
}
