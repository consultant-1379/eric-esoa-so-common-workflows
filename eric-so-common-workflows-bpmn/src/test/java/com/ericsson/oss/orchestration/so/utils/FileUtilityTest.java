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

import com.ericsson.oss.orchestration.so.util.FileUtility;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilityTest {

    final FileUtility fileUtility = new FileUtility();

    /**
     * Should find all resources
     */
    @Test
    public void getResourcesTest() {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        assertEquals(22, allBpmnFiles.size());
    }

    /**
     * Should extract fileName when path found
     */
    @Test
    public void ExtractFileNameTest() {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        final Map.Entry<String, String> firstEntry = iterator.next();
        final String fileName = fileUtility.extractFileName(firstEntry.getKey());
        assertEquals("nsDelete.bpmn", fileName);
    }

    /**
     * Should return given filePath when not found
     */
    @Test
    public void getFileNameTest() {
        final String filePath = "myFilePath";
        final String fileName = fileUtility.extractFileName(filePath);
        assertEquals(filePath, fileName);
    }

    /**
     * Should return given filePath when not found
     */
    @Test
    public void getFileFromJarTest() {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        final Map.Entry<String, String> firstEntry = iterator.next();
        String fileContents = fileUtility.getFile(firstEntry.getValue(), firstEntry.getKey());
        assertTrue(fileContents.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" " +
                "xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "id=\"Definitions_1\" targetNamespace=\"http://bpmn.io/schema/bpmn\" " +
                "exporter=\"Camunda Modeler\" exporterVersion=\"4.12.0\">\n" +
                "  <bpmn:process id=\"nsDelete\" name=\"Delete Network Service\" isExecutable=\"true\">\n"));
    }

    /**
     * Should extract fileNameWithoutExtension when path found
     */
    @Test
    public void ExtractFileNameWithoutExtensionTest() {
        final Map<String, String> allBpmnFiles = fileUtility.getResources();
        final Iterator<Map.Entry<String, String>> iterator = allBpmnFiles.entrySet().iterator();
        final Map.Entry<String, String> firstEntry = iterator.next();
        final String fileName = fileUtility.extractFileName(firstEntry.getKey());
        final String fileNameWithoutExtension = fileUtility.extractFileNameWithoutExtension(fileName);
        assertEquals("nsDelete", fileNameWithoutExtension);
    }
}
