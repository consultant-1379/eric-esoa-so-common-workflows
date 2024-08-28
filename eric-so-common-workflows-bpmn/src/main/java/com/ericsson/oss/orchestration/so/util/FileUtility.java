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

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtility {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final Pattern BPMN_FILE_MAPPER = Pattern.compile(".*\\.(bpmn|dmn)$");

    private static final String FILE_PATH_SEPARATOR = "/";

    private static final String FILE_NAME_SEPARATOR = "\\.";

    private static final String CLASS_PATH_PROPERTY = "java.class.path";

    public Map<String, String> getResources(){
        final Map<String, String> retval = new HashMap<>();
        final String classPath = System.getProperty(CLASS_PATH_PROPERTY, ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for(final String element : classPathElements){
            retval.putAll(getResources(element));
        }
        return retval;
    }

    private Map<String, String> getResources(final String element){
        final Map<String, String> retval = new HashMap<>();
        final File file = new File(element);
        if(!file.isDirectory()){
            retval.putAll(getResourcesFromJarFile(file));
        }
        return retval;
    }

    private Map<String, String> getResourcesFromJarFile(final File file) {
        final Map<String, String> retval = new HashMap<>();
        try (ZipFile zf = new ZipFile(file)) {
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while(e.hasMoreElements()){
                final ZipEntry ze = e.nextElement();
                final String fileName = ze.getName();
                final boolean accept = BPMN_FILE_MAPPER.matcher(fileName).matches();
                if(accept){
                    retval.put(fileName, zf.getName());
                }
            }
        } catch(final IOException e){
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(e).log("Failed to get resources " +
                    "from jar file");
            throw new Error(e);
        }
        return retval;
    }

    public String getFile(String jarFile, String fileName) {
        try (ZipFile zf = new ZipFile(new File(jarFile))) {
            ZipEntry bpmnFile = zf.getEntry(fileName);
            return new String(zf.getInputStream(bpmnFile).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            logger.atSevere().withStackTrace(StackSize.FULL).withCause(ioe).log("Failed to get bytes of bpmn %s " +
                    "from jar %s", fileName, jarFile);
            throw new Error(ioe);
        }
    }


    public String extractFileName(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf(FILE_PATH_SEPARATOR);
        if (lastSeparatorIndex != -1) {
            return filePath.substring(lastSeparatorIndex + 1);
        } else {
            return filePath;
        }
    }

    public String extractFileNameWithoutExtension(String fileName) {
        return fileName.split(FILE_NAME_SEPARATOR)[0];
    }

}
