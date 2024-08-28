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

import com.ericsson.oss.orchestration.so.rest.AuthenticationClient;
import com.ericsson.oss.orchestration.so.rest.BpmnRestClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetriesTest {

    AuthenticationClient authenticationClient = new AuthenticationClient();
    BpmnRestClient bpmnRestClient = new BpmnRestClient();

    @Test
    public void getTokenTest() {
        Exception exception = assertThrows(RuntimeException.class, () -> authenticationClient.retry().getCwtoken());
        String expectedMessage = "java.net.ConnectException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void pcHealthCheckTest() {
        Exception exception = assertThrows(RuntimeException.class, () -> bpmnRestClient.retry("test"));
        String expectedMessage = "java.net.ConnectException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
