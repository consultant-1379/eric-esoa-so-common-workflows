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
import com.ericsson.oss.orchestration.so.service.Retries;
import com.ericsson.oss.orchestration.so.util.FileUtility;
import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.ericsson.oss.orchestration.so.util.GeneralUtility.getEnvironmentVariable;

public class BpmnRestClient extends Retries<PcHealthCheck> {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static final String HOST = "pc_host";
    private static final String PORT = "pc_port";
    private static final String ROOT_URI = "pc_root_uri";
    private static final String AUTHORIZATION_BEARER_STRING = "Bearer %s";
    private static final String host;
    private static final String port;
    private static final String rootUri;
    private static final String url;
    private static final String health_url;

    static {
        host = getEnvironmentVariable(HOST, "localhost");
        port = getEnvironmentVariable(PORT, "8085");
        rootUri = getEnvironmentVariable(ROOT_URI, "process-controller/rest");
        url = String.format("%s://%s:%s/%s/deployment/create", httpProtocol, host, port, rootUri);
        health_url = String.format("%s://%s:%s/%s/process-definition", httpProtocol, host, port, rootUri);
    }

    private final FileUtility fileUtility = new FileUtility();

    private static HttpRequest.BodyPublisher buildMultipartBody(String fileName, String bpmnFile, String fileNameWithoutExtension) throws IOException {
        String formData = "--boundary\r\n" +
                "Content-Disposition: form-data; name=\"deployment-name\"\r\n" +
                "\r\n" + fileNameWithoutExtension + "\r\n--boundary\r\n" +
                "Content-Disposition: form-data; name=\"upload\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" + bpmnFile + "\r\n" +
                "--boundary--\r\n";
        return HttpRequest.BodyPublishers.ofString(formData);
    }

    public PcHealthCheck pcHealthCheck(String token) {
        PcHealthCheck healthCheck = new PcHealthCheck();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(health_url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", String.format(AUTHORIZATION_BEARER_STRING, token))
                    .GET()
                    .build();
            HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                healthCheck.setHealthy(true);
            } else {
                healthCheck.setHealthy(false);
                String message = String.format("Process Controller health Check has failed with status code: %s", statusCode);
                logger.atSevere().log(message);
                throw new RuntimeException(message);
            }
        } catch (IOException | InterruptedException ioe) {
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(ioe).log("Health Check of Process Controller failed");
            throw new RuntimeException(ioe);
        }
        return healthCheck;
    }

    public Workflow uploadBpmn(String bpmnName, String jarFileName, String token) {
        Workflow workflow = new Workflow();
        try {
            String fileName = fileUtility.extractFileName(bpmnName);
            String fileNameWithoutExtension = fileUtility.extractFileNameWithoutExtension(fileName);
            workflow.setWorkflowName(fileName);

            String bpmnFile = fileUtility.getFile(jarFileName, bpmnName);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "multipart/form-data; boundary=boundary")
                    .header("Authorization", String.format(AUTHORIZATION_BEARER_STRING, token))
                    .POST(buildMultipartBody(fileName, bpmnFile, fileNameWithoutExtension))
                    .build();
            HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logger.atInfo().log("Successfully uploaded %s", fileName);
                workflow.setStatus(Workflow.Status.SUCCESS);
            } else if (response.statusCode() == 401) {
                throw new AuthorizationError("Authorization error occurred");
            } else {
                workflow.setStatus(Workflow.Status.FAILED);
                if (response.body() != null) {
                    logger.atSevere().log("The response body for uploading bpmn file %s is %s with status %s", fileName,
                            response.body(), response.statusCode());
                } else {
                    logger.atSevere().log("Response body is Null please check the PC logs for more details as upload failed for status code %s", response.statusCode());
                }
            }
        } catch (IOException | InterruptedException ioe) {
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(ioe).log("Failed while uploading bpmn %s",
                    bpmnName);
            workflow.setStatus(Workflow.Status.FAILED);
            throw new RuntimeException(ioe);
        }
        return workflow;
    }

    @Override
    public PcHealthCheck run(String... token) {
        return pcHealthCheck(token[0]);
    }
}
