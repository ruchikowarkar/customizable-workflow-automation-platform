package com.platform.workflow_automation.service;

import com.platform.workflow_automation.dto.WorkflowRequest;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface FileParserService {
    WorkflowRequest parseFile(InputStream fileStream, String fileType);
}
