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

import com.ericsson.oss.orchestration.so.rest.RestClient;
import com.ericsson.oss.orchestration.so.util.GeneralUtility;
import com.google.common.flogger.FluentLogger;

import static com.ericsson.oss.orchestration.so.util.GeneralUtility.getEnvironmentVariable;

public abstract class Retries<T> extends RestClient {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String MAX_RETRIES = "max_retries";
    private static final String DELAY_IN_SECOND = "delay";

    private static int maxRetries;
    private static int delay;

    static {
        maxRetries = Integer.parseInt(getEnvironmentVariable(MAX_RETRIES, "3"));
        delay = Integer.parseInt(getEnvironmentVariable(DELAY_IN_SECOND, "30"));
    }

    public T retry(String... token) {
        T t = null;
        for (int count = 1; count <= maxRetries; count++) {
            try {
                t = run(token);
                break;
            } catch (Exception ex) {
                logger.atInfo().log("Retrying...");
                GeneralUtility.delaySecond(delay);
                if (count == maxRetries) {
                    throw ex;
                }
            }
        }
        return t;
    }

    public abstract T run(String... token);
}
