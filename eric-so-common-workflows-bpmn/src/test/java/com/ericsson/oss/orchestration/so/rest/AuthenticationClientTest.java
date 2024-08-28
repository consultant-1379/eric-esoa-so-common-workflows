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
import com.ericsson.oss.orchestration.so.models.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationClientTest {
    AuthenticationClient authenticationClient = new AuthenticationClient();
    AuthenticationClient spyAuthenticationClient;
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse httpResponseMock = mock(HttpResponse.class);

    @BeforeEach
    public void setup() {
        spyAuthenticationClient = spy(authenticationClient);
        doReturn(httpClientMock).when(spyAuthenticationClient).getHttpClient();
    }

    @Test
    public void getTokenSuccessTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("{\"cwtoken\":\"my_token\"}");
        Token token = spyAuthenticationClient.getToken();
        assertEquals("my_token", token.getCwtoken());
    }

    @Test
    public void getTokenUnauthorizedTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(401);
        when(httpResponseMock.body()).thenReturn("{\"cwtoken\":\"my_token\"}");
        Exception exception = assertThrows(AuthorizationError.class, () -> spyAuthenticationClient.getToken());
        String expectedMessage = "Invalid username or password";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getTokenErrorBodyNullTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        Exception exception = assertThrows(RuntimeException.class, () -> spyAuthenticationClient.getToken());
        String expectedMessage = "getting token response body is null";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getTokenErrorBodyTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        when(httpResponseMock.body()).thenReturn("{\"cwtoken\":\"my_token\"}");
        Exception exception = assertThrows(RuntimeException.class, () -> spyAuthenticationClient.getToken());
        String expectedMessage = "The response body for getting token : {\"cwtoken\":\"my_token\"}";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getTokenConnectionTimeoutTest() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new HttpConnectTimeoutException("timed out"));
        Exception exception = assertThrows(RuntimeException.class, () -> spyAuthenticationClient.getToken());
        String expectedMessage = "timed out";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
