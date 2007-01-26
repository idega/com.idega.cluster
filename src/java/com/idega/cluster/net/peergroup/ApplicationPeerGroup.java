/*
 * $Id: ApplicationPeerGroup.java,v 1.4 2007/01/26 07:15:02 thomas Exp $
 * Created on Dec 20, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.peergroup;

import java.io.StringWriter;
import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.config.JxtaPlatformConfigurator;
import com.idega.cluster.net.config.id.IDApplicationFactory;
import com.idega.idegaweb.IWApplicationContext;


public class ApplicationPeerGroup {
	

	public final String APPLICATION_PEER_GROUP_NAME = "idegaWeb application group";
	public final String APPLICATION_PEER_GROUP_DESCRIPTION = "group that is used by several instances of the same idegaWeb application";
	
	private PeerGroup parentPeerGroup = null;
	private DiscoveryService discoveryService = null;
	private PeerGroup myPeerGroup = null;
	
	private boolean active = false;
	
	public ApplicationPeerGroup(IWApplicationContext iwac)  {
		initialize(iwac);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public synchronized void destroy() {
		active = false;
		// stop all modules
		if (discoveryService != null) {
			discoveryService.stopApp();
		}
		if (myPeerGroup != null) {
			myPeerGroup.stopApp();
		}
		if (parentPeerGroup != null) {
			parentPeerGroup.stopApp();
		}
		discoveryService = null;
		parentPeerGroup = null;
		myPeerGroup = null;
	}
	
	private void initialize(IWApplicationContext iwac) {
		if (startJXTA(iwac)) {
			if (createGroups(iwac)) {
				active = true;
				return;
			}
		}
		destroy();
	}
		
	private boolean startJXTA(IWApplicationContext iwac) {
		// creating platform config
		//ApplicationJxtaNetConfigurator.createPlatformConfig(iwac);
		
		
		JxtaPlatformConfigurator.createPlatformConfig(iwac);
		
		
		//RendezvousRelayPeerConfiguration.createPlatformConfig(iwac);
		//TCPEdgePeerConfiguration.createPlatformConfig(iwac);
		
		// getting default peer group
		parentPeerGroup = null; 
		try {
			parentPeerGroup = PeerGroupFactory.newNetPeerGroup();   
		}
		catch (PeerGroupException e) {
			e.printStackTrace();
			return false;
		}
        discoveryService = parentPeerGroup.getDiscoveryService();
//      this step helps when running standalone (local sub-net without any redezvous setup)
        discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null, 1, null);
        return true;
	}


	private boolean createGroups(IWApplicationContext iwac) {
		if (! JxtaConfigSettings.USE_OWN_GROUP) {
			// use the default group
			myPeerGroup = parentPeerGroup;
			return true;
		}
		// go the simple way, create a all purpose group
		try {
			ModuleImplAdvertisement moduleImplAdvertisement = parentPeerGroup.getAllPurposePeerGroupImplAdvertisement();
			// create the unique group id 
			PeerGroupID peerGroupID = IDApplicationFactory.getApplicationPeerGroupID(iwac);
			myPeerGroup = parentPeerGroup.newGroup(peerGroupID, moduleImplAdvertisement, APPLICATION_PEER_GROUP_NAME, APPLICATION_PEER_GROUP_DESCRIPTION);
			// publish group
			PeerGroupAdvertisement peerGroupAdvertisement = myPeerGroup.getPeerGroupAdvertisement();
			discoveryService.publish(peerGroupAdvertisement);
			discoveryService.remotePublish(peerGroupAdvertisement);
			joinGroup(myPeerGroup);
			PeerAdvertisement peerAdvertisement = myPeerGroup.getPeerAdvertisement();
			discoveryService.publish(peerAdvertisement);
			discoveryService.remotePublish(peerAdvertisement);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
    private void joinGroup(PeerGroup peerGroup) throws Exception {
        // Generate the credentials for the Peer Group
    	AuthenticationCredential authCred =  new AuthenticationCredential( peerGroup, null, null );
        // Get the MembershipService from the peer group
        MembershipService membership = peerGroup.getMembershipService();
        // Get the Authenticator from the Authentication creds
        Authenticator auth = membership.apply( authCred );
        // Check if everything is okay to join the group
        if (auth.isReadyForJoin()){
        	Credential myCred = membership.join(auth);
            System.out.println("Successfully joined group " +
			  peerGroup.getPeerGroupName());
                
                // display the credential as a plain text document.
                System.out.println("\nCredential: ");
                StructuredTextDocument doc = (StructuredTextDocument)
		    myCred.getDocument(new MimeMediaType("text/plain"));
                
            StringWriter out = new StringWriter();
            doc.sendToWriter(out);
            System.out.println(out.toString());
            out.close();
        }
        else {
        	System.out.println("Failure: unable to join group");
        }
	}

    // All services must be stopped by the caller 
    
    public RendezVousService getRendezVousService() {
    	return myPeerGroup.getRendezVousService();
    }
    
    public DiscoveryService getDiscoveryService() {
    	return myPeerGroup.getDiscoveryService();
    }

    public PipeService getPipeService() {
    	return myPeerGroup.getPipeService();
    }
    
    public PeerGroupID getPeerGroupID() {
    	return myPeerGroup.getPeerGroupID();
    }
    
    public PeerID getPeerID() {
    	return parentPeerGroup.getPeerID();
    }
    
}
