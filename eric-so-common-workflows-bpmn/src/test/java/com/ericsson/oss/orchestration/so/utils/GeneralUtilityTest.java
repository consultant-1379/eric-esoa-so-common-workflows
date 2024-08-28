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
package com.ericsson.oss.orchestration.so.utils;

import com.ericsson.oss.orchestration.so.util.GeneralUtility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralUtilityTest {

    /**
     * Test get variable that does not exist
     * <p>
     * Should return default as string
     */
    @Test
    public void getEnvironmentVariableDefaultValueTest() {
        String environmentVariable = GeneralUtility.getEnvironmentVariable("doesNotExist", "true");
        assertEquals("true", environmentVariable);
    }
}
