/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.plugin.war;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

/**
 * Represents a configured tuscany dependency for boot and extension libraries.
 *
 * @version
 */
public class Dependency {

    /**
     * JAR type artifact.
     */
    private static final String TYPE_JAR = "jar";

    /**
     * Default boot libraries.
     * TODO Decide on whether to get snapshot version rather than hardcoded version..
     */
    private static final Dependency[] DEFAULT_BOOT_LIBS = new Dependency[] { new WebappHostDependency() };

    /**
     * Group Id that is injected in from configuration.
     */
    private String groupId;

    /**
     * Artifact Id that is injected in from configuration.
     */
    private String artifactId;

    /**
     * Version that is injected in from configuration.
     */
    private String version;

    /**
     * Default constructor.
     *
     */
    public Dependency() {
    }

    /**
     * Initializes the field.
     *
     * @param groupId
     *            Group id.
     * @param artifactId
     *            Artifact id.
     * @param version
     *            Artifact version.
     */
    public Dependency(String groupId, String artifactId, String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    /**
     * Gets the artifact using the specified artifact factory.
     *
     * @param artifactFactory
     *            Artifact factory to use.
     * @return Artifact identified by the dependency.
     */
    public Artifact getArtifact(ArtifactFactory artifactFactory) {
        return artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_RUNTIME, TYPE_JAR);
    }

    /**
     * Returns the default boot libraries.
     * @return Default boot libraries.
     */
    public static Dependency[] getDefaultBootLibs() {
        return DEFAULT_BOOT_LIBS;
    }

    /**
     * Checks whether the specified artifact has the same artifact id.
     * @param artifact Artifact to be matched.
     * @return True if the sepcified artifact has the same id.
     */
    public boolean match(Artifact artifact) {
        // TODO Auto-generated method stub
        return artifact.getArtifactId().equals(artifactId);
    }

    /**
     * Webapp host depndency.
     */
    private static class WebappHostDependency extends Dependency {
        // TODO Decide on whether to get snapshot version rather than hardcoded version
        private WebappHostDependency() {
            super("org.apache.tuscany.sca.runtime", "webapp-host", "1.0-incubator-M2-SNAPSHOT");
        }
    }

}
