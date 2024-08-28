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

import java.net.http.HttpClient;
import java.time.Duration;

import static com.ericsson.oss.orchestration.so.util.GeneralUtility.getEnvironmentVariable;

public class RestClient {

    private static final String CONNECTION_TIMEOUT = "connection_timeout";
    private static final String TLS_ENABLED = "tls_enabled";
    private static int connectionTimeOut;
    public static String httpProtocol;
    private static HttpClient httpClient;

    static {
        connectionTimeOut = Integer.parseInt(getEnvironmentVariable(CONNECTION_TIMEOUT, "60"));
        httpProtocol = Boolean.parseBoolean(getEnvironmentVariable(TLS_ENABLED, "true")) ? "https" : "http";
    }

    protected HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(connectionTimeOut)).build();
        }
        return httpClient;
    }
}
