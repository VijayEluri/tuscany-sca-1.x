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
package org.apache.tuscany.service.discovery.jxta;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.cert.CertificateException;
import javax.xml.stream.XMLStreamReader;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.impl.id.binaryID.DigestTool;
import net.jxta.impl.protocol.ResolverQuery;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.resolver.QueryHandler;
import net.jxta.resolver.ResolverService;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.omg.CORBA.Any;
import org.osoa.sca.annotations.Property;

/**
 * Discovery service implemented using Apple bonjour.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryService extends AbstractDiscoveryService implements Runnable {
    
    /** Default discovery interval. */
    private static long DEFAULT_INTERVAL = 10000L;

    /** Peer listener. */
    private PeerListener peerListener;
    
    /** Resolver service. */
    private ResolverService resolverService;
    
    /** Domain group. */
    private PeerGroup domainGroup;

    /** Network platform configurator. */
    private NetworkConfigurator configurator;
    
    /** Work scheduler. */
    private WorkScheduler workScheduler;
    
    /** Interval for sending discivery messages .*/
    private long interval = DEFAULT_INTERVAL;
    
    /** Started flag. */
    private final AtomicBoolean started = new AtomicBoolean();
    
    /** Message id generator. */
    private final AtomicInteger messageIdGenerator = new AtomicInteger();

    /**
     * Adds a network configurator for this service.
     * @param configurator Network configurator.
     */
    @Autowire
    public void setConfigurator(NetworkConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Adds a work scheduler for runningbackground discovery operations.
     * @param workScheduler Work scheduler.
     */
    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }
    
    /**
     * Sets the interval at which discovery messages are sent.
     * @param interval Interval at which discovery messages are sent.
     */
    @Property
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * Starts the discovery service.
     * @throws Any unexpected JXTA exception to bubble up the call stack.
     */
    @Override
    public void onStart() throws JxtaException {
        workScheduler.scheduleWork(this);
    }
    
    /**
     * Rusn the discovery service in a different thread.
     */
    public void run() {

        try {  
            
            configure();            
            createAndJoinDomainGroup();
            
            setupDiscovery();        
            setupResolver();
            
            started.set(true);        
            peerListener.start();
            
        } catch (PeerGroupException ex) {
            throw new JxtaException(ex);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        } catch (Exception ex) {
            throw new JxtaException(ex);
        }
        
    }

    /**
     * Sends a message to the specified runtime.
     * 
     * @param runtimeId Runtime id of recipient.
     * @param content Message content.
     * @return The message id. 
     */
    public int sendMessage(final String runtimeId, final XMLStreamReader content) {
        
        if(runtimeId == null) {
            throw new IllegalArgumentException("Runtime id is null");
        }
        if(content == null) {
            throw new IllegalArgumentException("Content id is null");
        }
        
        PeerID peerID = peerListener.getPeerId(runtimeId);
        if(peerID == null) {
            throw new JxtaException("Unrecognized runtime " + runtimeId);
        }
        
        // TODO get message from XML stream reader
        final String message = null;
        int messageId = messageIdGenerator.incrementAndGet();
        
        ResolverQuery query = new ResolverQuery();
        query.setHandlerName(TuscanyQueryHandler.class.getSimpleName());
        query.setQuery(message);
        query.setSrc(domainGroup.getPeerID().toString());
        resolverService.sendQuery(peerID.toString(), query);
        
        return messageId;
        
    }
    
    /**
     * Checks whether the service is started.
     * @return True if the service is started.
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Stops the discovery service.
     */
    @Override
    protected void onStop() {
        peerListener.stop();
        started.set(false);
    }

    /**
     * Configures the platform.
     *
     */
    private void configure() {

        try {
            
            String runtimeId = getRuntimeInfo().getRuntimeId();
            
            configurator.setName(runtimeId);
            configurator.setHome(new File(runtimeId));
            
            if (configurator.exists()) {
                File pc = new File(configurator.getHome(), "PlatformConfig");
                configurator.load(pc.toURI());
                configurator.save();
            } else {
                configurator.save();
            }
            
        } catch (IOException ex) {
            throw new JxtaException(ex);
        } catch (CertificateException ex) {
            throw new JxtaException(ex);
        }
        
    }

    /**
     * Creates and joins the domain peer group.
     * @throws Exception In case of unexpected JXTA exceptions.
     */
    private void createAndJoinDomainGroup() throws Exception {
        
        String domain = getRuntimeInfo().getDomain().toString();
        
        PeerGroup netGroup = new NetPeerGroupFactory().getInterface();
        PeerGroupID peerGroupId = createPeerGroupId(domain, netGroup);    
        ModuleImplAdvertisement implAdv = netGroup.getAllPurposePeerGroupImplAdvertisement();
        domainGroup = netGroup.newGroup(peerGroupId, implAdv, domain, "Tuscany domain group");
            
        AuthenticationCredential authCred = new AuthenticationCredential(domainGroup, null, null);
        MembershipService membership = domainGroup.getMembershipService();
        Authenticator auth = membership.apply(authCred);
                
        if (auth.isReadyForJoin()){
            membership.join(auth);
        } else {
            throw new JxtaException("Unable to join domain group");
        }
        
    }

    /**
     * Sets up the resolver service.
     */
    private void setupResolver() {
        
        resolverService = domainGroup.getResolverService();
        QueryHandler queryHandler = new TuscanyQueryHandler(resolverService, this);
        resolverService.registerHandler(TuscanyQueryHandler.class.getSimpleName(), queryHandler);
        
    }

    /**
     * Sets up peer discovery service.
     */
    private void setupDiscovery() {
        
        final DiscoveryService discoveryService = domainGroup.getDiscoveryService();
        discoveryService.remotePublish(domainGroup.getPeerAdvertisement());
        peerListener = new PeerListener(discoveryService, interval, getRuntimeInfo().getRuntimeId());
        
    }

    /**
     * Creates a well-known peer group id for the domain.
     * 
     * @param domain Name of the domain.
     * @param netGroup Parent net peer group.
     * @return Well-known peer group id.
     */
    private PeerGroupID createPeerGroupId(String domain, PeerGroup netGroup) {
        return new DigestTool().createPeerGroupID(netGroup.getPeerGroupID(), domain, null);
    }

}
