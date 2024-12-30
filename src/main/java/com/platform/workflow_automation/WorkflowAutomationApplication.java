package com.platform.workflow_automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSpringConfigured
@EnableScheduling
public class WorkflowAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowAutomationApplication.class, args);
	}

}
