package com.platform.workflow_automation.repository;

import com.platform.workflow_automation.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActionRepository extends JpaRepository<Action, UUID> {
}
