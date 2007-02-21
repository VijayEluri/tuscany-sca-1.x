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

package org.apache.tuscany.core.services.deployment.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.tuscany.core.services.deployment.ContentTypeDescriberImpl;
import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.spi.deployer.ContentTypeDescriber;
import org.apache.tuscany.spi.deployer.ContributionProcessor;
import org.apache.tuscany.spi.extension.ContributionProcessorExtension;
import org.apache.tuscany.spi.model.Contribution;

public class JarContributionProcessor extends ContributionProcessorExtension implements ContributionProcessor {
    public static final String CONTENT_TYPE = "application/x-compressed";

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    /**
     * Get a list of selected resources by the extensions
     * 
     * @return
     * @throws IOException
     */
    protected List<URL> getArtifacts(URL rootURL, InputStream sourceInputStream) throws IOException {
        List<URL> artifacts = new ArrayList<URL>();

        // Assume the root is a jar file
        JarInputStream jar = new JarInputStream(sourceInputStream);
        try {
            while (true) {
                JarEntry entry = jar.getNextJarEntry();
                if (entry == null) {
                    // EOF
                    break;
                }
                if (entry.isDirectory()) {
                    continue;
                }

                artifacts.add(new URL(rootURL, entry.getName()));
            }
        } finally {
            jar.close();
        }
        return artifacts;
    }

    private URL forceJarURL(URL sourceURL) throws MalformedURLException {
        if (sourceURL.toString().startsWith("jar:")) {
            return sourceURL;
        } else {
            return new URL("jar:" + sourceURL.toExternalForm() + "!/");
        }

    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws DeploymentException, IOException {
        if(contribution == null){
            throw new IllegalArgumentException("Invalid null contribution.");
        }

        if (source == null) {
            throw new IllegalArgumentException("Invalid null source uri.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        URL sourceURL = contribution.getArtifact(source).getLocation();

        sourceURL = forceJarURL(sourceURL);

        for (URL artifactURL : getArtifacts(sourceURL, inputStream)) {
            // FIXME
            // contribution.addArtifact(artifact)
            
            ContentTypeDescriber contentTypeDescriber = new ContentTypeDescriberImpl();
            String contentType = contentTypeDescriber.getContentType(artifactURL, null);
            System.out.println("Type : " + contentType);
            

            //just process scdl for now
            if("application/v.tuscany.scdl".equals(contentType) || "application/java-vm".equals(contentType) ){
                this.registry.processContent(contribution, source, inputStream);
            }
            // process each artifact
            //this.registry.processContent(contribution, artifactURL, inputStream);

        }

    }

    public void processModel(Contribution contribution, URI source, Object modelObject) throws DeploymentException,
        IOException {
        // TODO Auto-generated method stub

    }

}
