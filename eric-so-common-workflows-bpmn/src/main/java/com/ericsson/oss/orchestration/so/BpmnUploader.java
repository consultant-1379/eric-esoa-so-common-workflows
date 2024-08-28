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
package com.ericsson.oss.orchestration.so;

import com.ericsson.oss.orchestration.so.models.Workflow;
import com.ericsson.oss.orchestration.so.rest.BpmnRestClient;
import com.ericsson.oss.orchestration.so.service.UploadBpmn;
import com.ericsson.oss.orchestration.so.util.FileUtility;
import com.google.common.flogger.FluentLogger;


import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;


public class BpmnUploader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static void main(final String[] args) {
        final UploadBpmn uploadBpmn = new UploadBpmn();
        final FileUtility fileUtility = new FileUtility();

        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        List<Workflow> allWorkflowStatus = uploadBpmn.uploadBpmnWithToken(allBpmnFiles);

        Formatter fmt = new Formatter();
        fmt.format("%30s %30s\n", "Workflow Name", "Upload Status");
        for (Workflow workflow : allWorkflowStatus) {
            fmt.format("%29s %29s\n", workflow.getWorkflowName(), workflow.getStatus());
        }
        logger.atInfo().log(fmt.toString());
        if (allWorkflowStatus.stream().anyMatch(workflow -> workflow.getStatus() == Workflow.Status.FAILED)) {
            throw new RuntimeException("Failed to upload workflow, Check the container logs for more details.");
        }
    }
}
