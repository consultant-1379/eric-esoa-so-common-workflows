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
package com.ericsson.oss.orchestration.so.service;

import com.ericsson.oss.orchestration.so.exception.AuthorizationError;
import com.ericsson.oss.orchestration.so.models.Workflow;
import com.ericsson.oss.orchestration.so.rest.BpmnRestClient;
import com.ericsson.oss.orchestration.so.util.FileUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UploadBpmnTest {

    UploadBpmn uploadBpmn = new UploadBpmn();
    UploadBpmn spyUploadBpmn;

    @BeforeEach
    public void setup() {
        BpmnRestClient mock = mock(BpmnRestClient.class);
        spyUploadBpmn = spy(uploadBpmn);
        doReturn("test").when(spyUploadBpmn).getToken();
        doNothing().when(spyUploadBpmn).pcHealthCheck(anyString());
        doReturn(mock).when(spyUploadBpmn).getBpmnRestClient();
        when(mock.uploadBpmn(anyString(), anyString(), anyString())).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                if (count++ == 1)
                    throw new AuthorizationError();

                return new Workflow("file", Workflow.Status.SUCCESS);
            }
        });
    }

    @Test
    public void upLoadBpmnTest() {
        final FileUtility fileUtility = new FileUtility();
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        List<Workflow> allWorkflowStatus = spyUploadBpmn.uploadBpmnWithToken(allBpmnFiles);
        assertEquals(22, allWorkflowStatus.size());
        Workflow next = allWorkflowStatus.iterator().next();
        assertEquals(Workflow.Status.SUCCESS, next.getStatus());
        assertEquals("file", next.getWorkflowName());
    }
}
