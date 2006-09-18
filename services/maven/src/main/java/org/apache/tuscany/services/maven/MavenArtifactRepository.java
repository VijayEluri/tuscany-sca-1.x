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
package org.apache.tuscany.services.maven;

import java.util.Collection;

import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;
import org.osoa.sca.annotations.Destroy;

/**
 * Artifact repository used for resolving artifacts.
 * 
 * This is used by the composite loader for resolving artifacts transitively. The repository uses the Maven API for resolving dependencies and hence
 * expects the artifacts to be stored in a structure similar to the Maven repository layout. The repository first looks within the deployed unit (WAR
 * for example), before resorting to a local and set of remote Maven repositories.
 * 
 * @version $Rev$ $Date$
 */
public class MavenArtifactRepository implements ArtifactRepository {
    
    /** Maven helper */
    private MavenHelper mavenHelper;

    /**
     * Conctructs a new artifact repository.
     */
    public MavenArtifactRepository(String[] remoteRepoUrls) {
        mavenHelper = new MavenHelper(remoteRepoUrls);
        mavenHelper.start();
    }

    /**
     * Resolve an artifact.
     * This ensures that the information associated with an artifact is fully populated;
     * Specifically, after this operation the URL should contain a location where the artifact can be obtained.
     *
     * @param artifact the artifact to be resolved
     */
    public void resolve(Artifact rootArtifact) {
        mavenHelper.resolveTransitively(rootArtifact);
    }

    /**
     * Resolve a collection of Artifacts.
     *
     * @param artifacts a collection of artifacts to be resolved
     * @see #resolve(Artifact)
     */
    public void resolve(Collection<? extends Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            resolve(artifact);
        }
    }
    
    /**
     * Destroy method.
     *
     */
    @Destroy
    public void destroy() {
        mavenHelper.stop();
    }

}
