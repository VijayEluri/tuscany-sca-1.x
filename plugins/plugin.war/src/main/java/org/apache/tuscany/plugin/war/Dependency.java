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
 * Represents a configured tuscany dependency for boot and extension
 * libraries.
 * 
 * @version
 */
public class Dependency {
    
    /**
     * JAR type artifact.
     */
    private static final String TYPE_JAR = "jar";
    
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
     * Gets the artifact using the specified artifact factory.
     * 
     * @param artifactFactory Artifact factory to use.
     * @return Artifact identified by the dependency.
     */
    public Artifact getArtifact(ArtifactFactory artifactFactory) {
        return artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_RUNTIME, TYPE_JAR);
    }

}
