package com.platform.workflow_automation.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.platform.workflow_automation.dto.WorkflowConfig;
import com.platform.workflow_automation.dto.WorkflowRequest;
import com.platform.workflow_automation.service.FileParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static com.platform.workflow_automation.util.Constant.*;

@Service
@Slf4j
public class FileParserServiceImpl implements FileParserService {

    private final ObjectMapper yamlObjectMapper;
    private final ObjectMapper jsonObjectMapper;

    public FileParserServiceImpl() {
        log.debug("Initializing YAML and JSON Object Mappers...");
        this.yamlObjectMapper = new ObjectMapper(new YAMLFactory());
        this.yamlObjectMapper.registerModule(new JavaTimeModule());
        log.debug("YAML Object Mapper initialized.");

        this.jsonObjectMapper = new ObjectMapper();
        this.jsonObjectMapper.registerModule(new JavaTimeModule());
        log.debug("JSON Object Mapper initialized.");
    }

    @Override
    public WorkflowRequest parseFile(InputStream fileStream, String fileType) {
        log.info("Starting file parsing. File type: {}", fileType);
        try {
            WorkflowConfig config;

            if (YAML.equalsIgnoreCase(fileType) || YML.equalsIgnoreCase(fileType)) {
                log.info("Parsing YAML file...");
                config = yamlObjectMapper.readValue(fileStream, WorkflowConfig.class);
                log.debug("YAML file parsed successfully: {}", config);

            } else if (JSON.equalsIgnoreCase(fileType)) {
                log.info("Parsing JSON file...");
                config = jsonObjectMapper.readValue(fileStream, WorkflowConfig.class);
                log.debug("JSON file parsed successfully: {}", config);

            } else {
                log.error("Unsupported file type encountered: {}", fileType);
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
            }

            log.info("File parsed successfully. Mapping to WorkflowRequest...");
            WorkflowRequest workflowRequest = mapToWorkflowRequest(config);
            log.debug("Mapped WorkflowRequest: {}", workflowRequest);
            return workflowRequest;

        } catch (Exception e) {
            log.error("Error while parsing the file. File type: {}. Error: {}", fileType, e.getMessage(), e);
            throw new RuntimeException("Error while parsing the file: " + e.getMessage(), e);
        }
    }

    private WorkflowRequest mapToWorkflowRequest(WorkflowConfig config) {
        log.info("Mapping WorkflowConfig to WorkflowRequest...");
        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setName(config.getName());
        workflowRequest.setDescription(config.getDescription());
        workflowRequest.setDueDate(config.getDueDate());
        workflowRequest.setRecipients(config.getRecipients());
        log.debug("Mapping completed. WorkflowRequest: {}", workflowRequest);
        return workflowRequest;
    }
}