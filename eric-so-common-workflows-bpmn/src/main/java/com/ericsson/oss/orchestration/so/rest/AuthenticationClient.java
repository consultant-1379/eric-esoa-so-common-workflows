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
import com.ericsson.oss.orchestration.so.exception.ConnectionTimeoutException;
import com.ericsson.oss.orchestration.so.models.Token;
import com.ericsson.oss.orchestration.so.service.Retries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static com.ericsson.oss.orchestration.so.util.GeneralUtility.getEnvironmentVariable;

public class AuthenticationClient extends Retries<Token> {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static final String CONNECTION_TIMEOUT = "connection_timeout";
    private static final String TLS_ENABLED = "tls_enabled";
    private static final String HOST = "auth_host";
    private static final String PORT = "auth_port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String AUTHORIZATION_STRING = "Basic %s";
    private static final String CREDENTIALS = "%s:%s";
    private static final String host;
    private static final String port;
    private static final String username;
    private static final String password;
    private static final String credentials;
    private static final String url;

    private static HttpClient httpClient;

    private static final ObjectMapper mapper;

    static {
        host = getEnvironmentVariable(HOST, "localhost");
        port = getEnvironmentVariable(PORT, "8080");
        username = getEnvironmentVariable(USERNAME, "");
        password = getEnvironmentVariable(PASSWORD, "");
        url = String.format("%s://%s:%s/eoc/avmSecurity/getToken", httpProtocol, host, port);
        mapper = new ObjectMapper();
        String encodedCredentialsString = Base64.getEncoder().encodeToString(
                String.format(CREDENTIALS, username, password).getBytes());
        credentials = String.format(AUTHORIZATION_STRING, encodedCredentialsString);
    }

    public Token getToken() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", credentials)
                    .GET()
                    .build();
            HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), Token.class);
            } else if (response.statusCode() == 401) {
                String message = "Invalid username or password";
                logger.atSevere().log(message);
                throw new AuthorizationError(message);
            } else {
                if (response.body() != null) {
                    String message = String.format("The response body for getting token : %s", response.body());
                    logger.atSevere().log(message);
                    throw new RuntimeException(message);
                } else {
                    logger.atSevere().log("Response body is null please check the eoc-admin logs for " +
                            "more details");
                    throw new RuntimeException("getting token response body is null");
                }
            }
        } catch (HttpConnectTimeoutException hte) {
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(hte).log("Connection timeout occurred");
            throw new ConnectionTimeoutException(hte.getMessage());
        } catch (IOException | InterruptedException e) {
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(e).log("Failed to get token");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token run(String... token) {
        return getToken();
    }
}
