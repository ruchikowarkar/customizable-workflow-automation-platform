package com.platform.workflow_automation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WorkflowConfig {
    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("dueDate")
    LocalDate dueDate;

    @JsonProperty("recipients")
    List<String> recipients;

}
