
/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.oss.orchestration.so.rest;

import com.ericsson.oss.orchestration.so.exception.AuthorizationError;
import com.ericsson.oss.orchestration.so.models.PcHealthCheck;
import com.ericsson.oss.orchestration.so.models.Workflow;
import com.ericsson.oss.orchestration.so.util.FileUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BpmnRestClientTest {

    BpmnRestClient bpmnRestClient = new BpmnRestClient();
    BpmnRestClient spyBpmnRestClient = spy(bpmnRestClient);
    Map.Entry<String, String> file = null;

    final FileUtility fileUtility = new FileUtility();

    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse httpResponseMock = mock(HttpResponse.class);

    @BeforeEach
    public void setup() {
        doReturn(httpClientMock).when(spyBpmnRestClient).getHttpClient();
    }


    @Test
    public void uploadBpmnTest() throws IOException, InterruptedException {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        Map.Entry<String, String> file = iterator.next();
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        Workflow workflow = spyBpmnRestClient.uploadBpmn(file.getKey(), file.getValue(), "my-token");
        assertEquals(Workflow.Status.SUCCESS, workflow.getStatus());
        assertEquals("nsDelete.bpmn", workflow.getWorkflowName());
    }

    @Test
    public void uploadBpmnUnauthorizedTestTest() throws IOException, InterruptedException {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        Map.Entry<String, String> file = iterator.next();
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(401);
        Exception exception = assertThrows(AuthorizationError.class, () -> spyBpmnRestClient.uploadBpmn(file.getKey(), file.getValue(), "my-token"));
        String expectedMessage = "Authorization error occurred";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void uploadBpmnFailureTest() throws IOException, InterruptedException {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        Map.Entry<String, String> file = iterator.next();
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        when(httpResponseMock.body()).thenReturn("{}");
        Workflow workflow = spyBpmnRestClient.uploadBpmn(file.getKey(), file.getValue(), "my-token");
        assertEquals(Workflow.Status.FAILED, workflow.getStatus());
        assertEquals("nsDelete.bpmn", workflow.getWorkflowName());
    }

    @Test
    public void uploadBpmnFailureResponseNullTest() throws IOException, InterruptedException {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        Map.Entry<String, String> file = iterator.next();
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        Workflow workflow = spyBpmnRestClient.uploadBpmn(file.getKey(), file.getValue(), "my-token");
        assertEquals(Workflow.Status.FAILED, workflow.getStatus());
        assertEquals("nsDelete.bpmn", workflow.getWorkflowName());
    }

    @Test
    public void pcHealthCheckFailsTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> spyBpmnRestClient.pcHealthCheck("test"));
        String expectedMessage = "Process Controller health Check has failed with status code: 500";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void pcHealthCheckTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        PcHealthCheck healthCheck = spyBpmnRestClient.pcHealthCheck("test");
        assertTrue(healthCheck.isHealthy());
    }
}
