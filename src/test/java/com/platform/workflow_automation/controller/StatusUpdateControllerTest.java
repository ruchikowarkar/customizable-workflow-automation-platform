package com.platform.workflow_automation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.workflow_automation.dto.StatusUpdateRequest;
import com.platform.workflow_automation.service.StatusUpdateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatusUpdateController.class)
class StatusUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatusUpdateService statusUpdateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTriggerStatusUpdateSuccess() throws Exception {
        UUID testWorkflowId = UUID.randomUUID();

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setWorkflowId(testWorkflowId);
        request.setEmail("test@example.com");

        Mockito.doNothing().when(statusUpdateService).triggerStatusUpdate(Mockito.any(StatusUpdateRequest.class));

        mockMvc.perform(post("/api/status/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(statusUpdateService, times(1)).triggerStatusUpdate(Mockito.any(StatusUpdateRequest.class));
    }
}
