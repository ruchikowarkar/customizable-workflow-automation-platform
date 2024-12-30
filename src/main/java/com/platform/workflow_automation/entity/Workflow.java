package com.platform.workflow_automation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "workflows")
public class Workflow {

    @Id
    @Column(name = "workflow_id", updatable = false, nullable = false)
    UUID workflowId;

    @Column(name = "name")
    String workflowName;

    @Column(name = "description")
    String description;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "due_date")
    LocalDate dueDate;

    @Column(name = "status")
    String status;
}
