package com.dotcms.rest.api.v1.vtl;

import com.dotcms.repackage.javax.ws.rs.core.MultivaluedMap;
import com.dotcms.rest.api.v1.vtl.VTLResourceIntegrationTest.ResourceMethod;

import java.io.File;
import java.util.Map;

public class VTLResourceTestCase {

    private final File vtlFile;
    private final String folderName;
    private final MultivaluedMap<String, String> queryParameters;
    private final String pathParameter;
    private final String expectedJSON;
    private final String expectedOutput;
    private final int expectedException;
    private final String userId;
    private final Map<String, String> bodyMap;
    private final ResourceMethod resourceMethod;

    File getVtlFile() {
        return vtlFile;
    }

    String getFolderName() {
        return folderName;
    }

    MultivaluedMap<String, String> getQueryParameters() {
        return queryParameters;
    }

    String getPathParameter() {
        return pathParameter;
    }

    String getExpectedJSON() {
        return expectedJSON;
    }

    String getExpectedOutput() {
        return expectedOutput;
    }

    int getExpectedException() {
        return expectedException;
    }

    String getUserId() {
        return userId;
    }

    public ResourceMethod getResourceMethod() {
        return resourceMethod;
    }

    public Map<String, String> getBodyMap() {
        return bodyMap;
    }

    private VTLResourceTestCase(final File vtlFile, final String folderName, final MultivaluedMap<String, String> queryParameters,
                                final String pathParameter, final String expectedJSON, final String expectedOutput,
                                final int expectedException, final String user, final Map<String, String> bodyMap,
                                final ResourceMethod resourceMethod) {
        this.vtlFile = vtlFile;
        this.folderName = folderName;
        this.queryParameters = queryParameters;
        this.pathParameter = pathParameter;
        this.expectedJSON = expectedJSON;
        this.expectedOutput = expectedOutput;
        this.expectedException = expectedException;
        this.userId = user;
        this.bodyMap = bodyMap;
        this.resourceMethod = resourceMethod;
    }

    public static class Builder {
        private File vtlFile;
        private String folderName;
        private MultivaluedMap<String, String> queryParameters;
        private String pathParameter;
        private String expectedJSON;
        private String expectedOutput;
        private int expectedException;
        private String user = "system";
        private Map<String, String> bodyMap = null;
        private ResourceMethod resourceMethod = ResourceMethod.GET;

        Builder setVtlFile(final File vtlFile) {
            this.vtlFile = vtlFile;
            return this;
        }

        Builder setFolderName(final String folderName) {
            this.folderName = folderName;
            return this;
        }

        Builder setQueryParameters(final MultivaluedMap<String, String> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }

        Builder setPathParameter(final String pathParameter) {
            this.pathParameter = pathParameter;
            return this;
        }

        Builder setExpectedJSON(final String expectedJSON) {
            this.expectedJSON = expectedJSON;
            return this;
        }

        Builder setExpectedOutput(final String expectedOutput) {
            this.expectedOutput = expectedOutput;
            return this;
        }

        Builder setExpectedException(final int expectedException) {
            this.expectedException = expectedException;
            return this;
        }

        Builder setUser(final String user) {
            this.user = user;
            return this;
        }

        Builder setBodyMap(final Map<String, String> bodyMap) {
            this.bodyMap = bodyMap;
            return this;
        }

        Builder setResourceMethod(final ResourceMethod resourceMethod) {
            this.resourceMethod = resourceMethod;
            return this;
        }

        VTLResourceTestCase build() {
            return new VTLResourceTestCase(vtlFile, folderName, queryParameters, pathParameter,expectedJSON,
                    expectedOutput, expectedException, user, bodyMap, resourceMethod);
        }
    }
}
