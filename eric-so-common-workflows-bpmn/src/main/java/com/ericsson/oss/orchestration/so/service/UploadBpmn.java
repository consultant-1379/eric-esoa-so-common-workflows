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
import com.ericsson.oss.orchestration.so.models.PcHealthCheck;
import com.ericsson.oss.orchestration.so.models.Token;
import com.ericsson.oss.orchestration.so.models.Workflow;
import com.ericsson.oss.orchestration.so.rest.AuthenticationClient;
import com.ericsson.oss.orchestration.so.rest.BpmnRestClient;
import com.google.common.flogger.FluentLogger;
import org.assertj.core.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadBpmn {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public List<Workflow> uploadBpmnWithToken(Map<String, String> allBpmnFiles) {
        String token = getToken();
        pcHealthCheck(token);
        List<Workflow> allWorkflowStatus = new ArrayList<>();
        BpmnRestClient bpmnRestClient = getBpmnRestClient();
        for (Map.Entry<String, String> entry : allBpmnFiles.entrySet()) {
            try {
                allWorkflowStatus.add(bpmnRestClient.uploadBpmn(entry.getKey(), entry.getValue(), token));
            } catch (AuthorizationError ex) {
                String newToken = getToken();
                pcHealthCheck(token);
                allWorkflowStatus.add(bpmnRestClient.uploadBpmn(entry.getKey(), entry.getValue(), newToken));
            }
        }
        return allWorkflowStatus;
    }

    @VisibleForTesting
    protected void pcHealthCheck(String token) {
        logger.atInfo().log("Checking status of Process Controller before uploading BPMN files");
        Retries<PcHealthCheck> pcHealthCheckRetries = new BpmnRestClient();
        pcHealthCheckRetries.retry(token);
        logger.atInfo().log("Process Controller is up, will proceed to upload BPMN files");
    }

    @VisibleForTesting
    protected String getToken() {
        Retries<Token> tokenRetry = new AuthenticationClient();
        return tokenRetry.retry().getCwtoken();
    }

    @VisibleForTesting
    protected BpmnRestClient getBpmnRestClient() {
        BpmnRestClient bpmnRestClient = new BpmnRestClient();
        return bpmnRestClient;
    }
}
