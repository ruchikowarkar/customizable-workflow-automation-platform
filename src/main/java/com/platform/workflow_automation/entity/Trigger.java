package com.platform.workflow_automation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "triggers")
public class Trigger {
    @Id
    @Column(name = "trigger_id")
    UUID triggerId;

    @Column(name = "workflow_id")
    UUID workflowId;

    @Column(name = "email_to")
    String emailTo;

    @Column(name = "status")
    String status;
}
