package com.platform.workflow_automation.controller;

import com.platform.workflow_automation.dto.WorkflowRequest;
import com.platform.workflow_automation.service.FileParserService;
import com.platform.workflow_automation.service.WorkflowRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("api/request")
@Slf4j
public class WorkflowRequestController {

    @Autowired
    private WorkflowRequestService workflowRequestService;

    @Autowired
    private FileParserService fileParserService;

    @PostMapping("/workflow")
    public ResponseEntity<UUID> workflowCreation(@RequestBody WorkflowRequest workflowRequest) {
        log.info("Received request to create workflow: {}", workflowRequest);
        UUID workflowId = workflowRequestService.createWorkflow(workflowRequest);
        log.info("Workflow created successfully with ID: {}", workflowId);
        return ResponseEntity.status(HttpStatus.OK).body(workflowId);
    }

    @PostMapping("/import")
    public ResponseEntity<UUID> importWorkflow(@RequestParam("file") MultipartFile file) {
        log.info("Received request to import workflow from file: {}", file.getOriginalFilename());
        try {
            String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            log.info("Detected file type: {}", fileType);

            log.debug("Parsing file content...");
            WorkflowRequest workflowRequest = fileParserService.parseFile(file.getInputStream(), fileType);
            log.info("File parsed successfully into WorkflowRequest: {}", workflowRequest);

            UUID workflowId = workflowRequestService.createWorkflow(workflowRequest);
            log.info("Workflow imported and created successfully with ID: {}", workflowId);

            return ResponseEntity.status(HttpStatus.OK).body(workflowId);

        } catch (Exception e) {
            log.error("Error occurred while importing workflow: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to import workflow", e);
        }
    }
}