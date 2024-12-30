package com.platform.workflow_automation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.workflow_automation.dto.WorkflowRequest;
import com.platform.workflow_automation.service.FileParserService;
import com.platform.workflow_automation.service.WorkflowRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkflowRequestController.class)
class WorkflowRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowRequestService workflowRequestService;

    @MockBean
    private FileParserService fileParserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testWorkflowCreation() throws Exception {
        WorkflowRequest request = new WorkflowRequest();
        request.setName("Test Workflow");
        request.setDescription("Test Description");

        UUID mockWorkflowId = UUID.randomUUID();

        Mockito.when(workflowRequestService.createWorkflow(Mockito.any(WorkflowRequest.class)))
                .thenReturn(mockWorkflowId);

        mockMvc.perform(post("/api/request/workflow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(workflowRequestService, times(1)).createWorkflow(Mockito.any(WorkflowRequest.class));
    }

    @Test
    void testImportWorkflow() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "workflow.json",
                "application/json", "{\"name\":\"Sample Workflow\",\"description\":\"Test Description\"}".getBytes());

        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setName("Test Workflow");
        workflowRequest.setDescription("Test Description");

        UUID mockWorkflowId = UUID.randomUUID();

        Mockito.when(fileParserService.parseFile(Mockito.any(), anyString()))
                .thenReturn(workflowRequest);

        Mockito.when(workflowRequestService.createWorkflow(Mockito.any(WorkflowRequest.class)))
                .thenReturn(mockWorkflowId);

        mockMvc.perform(multipart("/api/request/import")
                        .file(mockFile))
                .andExpect(status().isOk());

        Mockito.verify(fileParserService, times(1)).parseFile(Mockito.any(), anyString());
        Mockito.verify(workflowRequestService, times(1)).createWorkflow(Mockito.any(WorkflowRequest.class));
    }

}
