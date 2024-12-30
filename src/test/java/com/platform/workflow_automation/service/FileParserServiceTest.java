package com.platform.workflow_automation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.workflow_automation.dto.WorkflowConfig;
import com.platform.workflow_automation.dto.WorkflowRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;

import static com.platform.workflow_automation.util.Constant.JSON;
import static com.platform.workflow_automation.util.Constant.YAML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


class FileParserServiceTest {

    private FileParserService fileParserService;

    @Mock
    private ObjectMapper yamlObjectMapper;

    @Mock
    private ObjectMapper jsonObjectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        fileParserService = new FileParserService() {
            @Override
            public WorkflowRequest parseFile(InputStream fileStream, String fileType) {
                try {
                    WorkflowConfig config;

                    if (YAML.equalsIgnoreCase(fileType)) {
                        config = yamlObjectMapper.readValue(fileStream, WorkflowConfig.class);
                    } else if (JSON.equalsIgnoreCase(fileType)) {
                        config = jsonObjectMapper.readValue(fileStream, WorkflowConfig.class);
                    } else {
                        throw new IllegalArgumentException("Unsupported file type: " + fileType);
                    }

                    WorkflowRequest workflowRequest = new WorkflowRequest();
                    workflowRequest.setName(config.getName());
                    workflowRequest.setDescription(config.getDescription());
                    workflowRequest.setDueDate(config.getDueDate());
                    workflowRequest.setRecipients(config.getRecipients());
                    return workflowRequest;

                } catch (Exception e) {
                    throw new RuntimeException("Error while parsing the file: " + e.getMessage(), e);
                }
            }
        };
    }

    @Test
    void testParseFile_Success_YAML() throws Exception {
        WorkflowConfig mockConfig = new WorkflowConfig();
        mockConfig.setName("Test Workflow");
        mockConfig.setDescription("This is a test description.");
        mockConfig.setDueDate(LocalDate.parse("2024-12-31"));
        mockConfig.setRecipients(Collections.singletonList("test@example.com"));

        InputStream mockInputStream = mock(InputStream.class);
        when(yamlObjectMapper.readValue(mockInputStream, WorkflowConfig.class)).thenReturn(mockConfig);

        WorkflowRequest result = fileParserService.parseFile(mockInputStream, YAML);

        assertEquals("Test Workflow", result.getName());
        assertEquals("This is a test description.", result.getDescription());
        assertEquals(LocalDate.parse("2024-12-31"), result.getDueDate());
        assertEquals(Collections.singletonList("test@example.com"), result.getRecipients());

        verify(yamlObjectMapper, times(1)).readValue(mockInputStream, WorkflowConfig.class);
        verifyNoInteractions(jsonObjectMapper);
    }


    @Test
    void testParseFile_Success_JSON() throws Exception {
        WorkflowConfig mockConfig = new WorkflowConfig();
        mockConfig.setName("Test Workflow");
        mockConfig.setDescription("This is a test description.");
        mockConfig.setDueDate(LocalDate.parse("2024-12-31"));
        mockConfig.setRecipients(Collections.singletonList("test@example.com"));

        InputStream mockInputStream = mock(InputStream.class);
        when(jsonObjectMapper.readValue(mockInputStream, WorkflowConfig.class)).thenReturn(mockConfig);

        WorkflowRequest result = fileParserService.parseFile(mockInputStream, JSON);

        assertEquals("Test Workflow", result.getName());
        assertEquals("This is a test description.", result.getDescription());
        assertEquals(LocalDate.parse("2024-12-31"), result.getDueDate());
        assertEquals(Collections.singletonList("test@example.com"), result.getRecipients());

        verify(jsonObjectMapper, times(1)).readValue(mockInputStream, WorkflowConfig.class);
        verifyNoInteractions(yamlObjectMapper);
    }

    @Test
    void testParseFile_UnsupportedFileType() {
        InputStream mockInputStream = mock(InputStream.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileParserService.parseFile(mockInputStream, "TXT"));

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Unsupported file type: TXT", exception.getCause().getMessage());
    }


    @Test
    void testParseFile_ExceptionThrownDuringParsing() throws Exception {
        InputStream mockInputStream = mock(InputStream.class);
        when(yamlObjectMapper.readValue(mockInputStream, WorkflowConfig.class)).thenThrow(new RuntimeException("Parsing error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileParserService.parseFile(mockInputStream, YAML));
        assertEquals("Error while parsing the file: Parsing error", exception.getMessage());
    }
}
