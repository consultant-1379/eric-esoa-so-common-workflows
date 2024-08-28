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
package com.ericsson.oss.orchestration.so.util;

public class GeneralUtility {

    private GeneralUtility() {
    }

    public static String getEnvironmentVariable(String parameterName, String defaultValue) {
        String value = System.getenv(parameterName);
        return value != null ? value : defaultValue;
    }

    public static void delaySecond(int second) {
        try {
            Thread.sleep(1000L * second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
