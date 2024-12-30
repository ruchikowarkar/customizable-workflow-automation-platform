package com.platform.workflow_automation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "action_id")
    UUID actionId;

    @Column(name = "trigger_id")
    UUID triggerId;

    @Column(name = "email_to")
    String emailTo;

    @Column(name = "email_subject")
    String emailSubject;

    @Column(name = "email_body")
    String emailBody;

    @Column(name = "execution_date")
    LocalDate executionDate;
}
