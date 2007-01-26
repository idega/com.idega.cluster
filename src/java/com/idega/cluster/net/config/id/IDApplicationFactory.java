/*
 * $Id: IDApplicationFactory.java,v 1.1 2007/01/26 07:15:02 thomas Exp $
 * Created on Dec 21, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.config.id;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.logging.Logger;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.cluster.net.peergroup.ApplicationPeerGroup;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplicationSettings;


public class IDApplicationFactory {
	
	public static final String INFRASTRUCTURE_PEER_GROUP_ID = "jxta_net_id";
	public static final String APPLICATION_PEER_GROUP_ID = "jxta_app_peer_group_id";
	public static final String APPLICATION_PEER_GROUP_PIPE_ID = "jxta_app_peer_group_pipe_id";

	public static PeerGroupID getInfrastructurePeerGroupID(IWApplicationContext iwac) throws URISyntaxException {
		return (PeerGroupID) getID(null, INFRASTRUCTURE_PEER_GROUP_ID, iwac);
	}
	
	public static PeerGroupID getApplicationPeerGroupID(IWApplicationContext iwac) throws URISyntaxException {
		return (PeerGroupID) getID(null, APPLICATION_PEER_GROUP_ID, iwac);
	}
	
	public static PipeID getApplicationPeerGroupPipeID(ApplicationPeerGroup applicationPeerGroup, IWApplicationContext iwac) throws URISyntaxException {
		PeerGroupID peerGroupID = applicationPeerGroup.getPeerGroupID();
		return (PipeID) getID(peerGroupID, APPLICATION_PEER_GROUP_PIPE_ID, iwac);
	}
	
	
	static private Logger getLogger() {
		 return Logger.getLogger(IDApplicationFactory.class.getName());
	 }
	
	private static ID getID(PeerGroupID peerGroupID, String key, IWApplicationContext iwac) throws URISyntaxException {
		IWMainApplicationSettings iwMainApplicationSettings =iwac.getApplicationSettings();
		// step 1 reading
		String uriString = iwMainApplicationSettings.getProperty(key);
		if (uriString == null) {
			// create a new unique id
			ID newID = null;
			if (peerGroupID == null) {
				newID = IDFactory.newPeerGroupID();
			}
			else {
				newID = IDFactory.newPipeID(peerGroupID);
			}
			// do not use toString() 
			URI uri = newID.toURI();
			uriString = uri.toString();
			// step 2 writing
			iwMainApplicationSettings.setProperty(key, uriString);
			// go sleeping for a while
			try {
				Thread.sleep(4000);
			}
			catch (InterruptedException e) {
				getLogger().warning("[IDApplicationFactory] Sleeping thread was interrupted");
			}
			// check now if some other application has written to the database during the time between step 1 and step 2
			// do not use the cache read database
			ICApplicationBindingBusiness applicationBindingBusiness = getApplicationBindingBusiness(iwac);
			try {
				String uriStringDatabase = applicationBindingBusiness.get(key);
			// do not use the created id if the database contains a different one use the one from the database
				if (! uriString.equals(uriStringDatabase)) {
					uriString = uriStringDatabase;
					// update the own cache, the same value might be written again to the database
					iwMainApplicationSettings.setProperty(key, uriString);
				}
			}
			catch (RemoteException re) {
				getLogger().warning("[IDApplicationFactory] ICApplicationBindingBusiness could not be found");
				throw new IBORuntimeException(re.getMessage());
			}
			catch (IOException ex) {
				getLogger().warning("[IDApplicationFactory] ICApplicationBindingBusiness could not be read");
				throw new URISyntaxException("empty string","uri could not be read from database");
			}
		}
		URI uri = new URI(uriString);
		return IDFactory.fromURI(uri);
	}

	private static ICApplicationBindingBusiness getApplicationBindingBusiness(IWApplicationContext iwac) {
		try {
			return (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
		}
		catch (IBOLookupException ex) {
			getLogger().warning("[ApplicationPeerGroup] ICApplicationBindingBusiness could not be found");
			throw new IBORuntimeException(ex.getMessage());
		}
	}
	
	// id for private network
    public static final net.jxta.peergroup.PeerGroupID createInfrastructurePeerGroupID(String clearTextID){
        //LOG.info("Creating peer group ID =  clearText:'"+clearTextID+"' , function:'"+function+"'");
        byte[] digest = generateHash(clearTextID);
        net.jxta.peergroup.PeerGroupID peerGroupID = IDFactory.newPeerGroupID(  digest );
        return peerGroupID;
    }
    /**
     * Generates an SHA-1 digest hash of the string: clearTextID+"-"+function or: clearTextID if function was blank.<p>
     *
     * Note that the SHA-1 used only creates a 20 byte hash.<p>
     *
     * @param clearTextID A string that is to be hashed. This can be any string used for hashing or hiding data.
     * @param function A function related to the clearTextID string. This is used to create a hash associated with clearTextID so that it is a uique code.
     *
     * @return array of bytes containing the hash of the string: clearTextID+"-"+function or clearTextID if function was blank. Can return null if SHA-1 does not exist on platform.
     */
    public static final byte[] generateHash(String clearTextID) {
        String id;
        
//        if (function == null) {
            id = clearTextID;
//        } else {
//            id = clearTextID + functionSeperator + function;
//        }
        byte[] buffer = id.getBytes();
        
        MessageDigest algorithm = null;
        
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            //LOG.error("Cannot load selected Digest Hash implementation",e);
            return null;
        }
        
        
        // Generate the digest.
        algorithm.reset();
        algorithm.update(buffer);
        
        try{
            byte[] digest1 = algorithm.digest();
            return digest1;
        }catch(Exception de){
            //LOG.error("Failed to creat a digest.",de);
            return null;
        }
    }

}
