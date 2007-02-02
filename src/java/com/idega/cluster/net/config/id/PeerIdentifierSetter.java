/*
 * $Id: PeerIdentifierSetter.java,v 1.2 2007/02/02 00:53:42 thomas Exp $
 * Created on Dec 21, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.config.id;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.StringHandler;


public class PeerIdentifierSetter {
	
	public static final String APPLICATION_PEERS = "jxta_app_peers";
	private static final String AD = "@";
	private static final String HASH = "#";
	private static final String TOKEN = AD + HASH;	
	private static final Integer DEFAULT_TCP_PORT = new Integer(9701);
	
	private Integer myTCPPort = null;
	private Integer myHTTPPort = null;
	String myIP = null;
	String myIdentifier = null;
	List seedPeers = null;
	
	static private Logger getLogger() {
		 return Logger.getLogger(PeerIdentifierSetter.class.getName());
	 }
	
	public PeerIdentifierSetter(IWApplicationContext iwac)  {
		initialize(iwac);
	}
	
	public List getSeedPeers() {
		return seedPeers;
	}
	
	public Integer getTCPPort() {
		return myTCPPort;
	}
	
	public Integer getHTTPPort() {
		return myHTTPPort;
	}
	
	
	private void initialize(IWApplicationContext iwac)  {	
		seedPeers = new ArrayList();
		try {
			while (! setPeerIdentifierIntoDatabase(iwac)) {
				// repeat until successful
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			getLogger().warning("[PeerIdentifierSetter] Could not write peer uri to database");
			myTCPPort = DEFAULT_TCP_PORT;
			seedPeers = new ArrayList();
		}
		// set TCP port
		myHTTPPort = new Integer(myTCPPort.intValue() - 1);
	}
	
	
	// returns true if successful
	private boolean setPeerIdentifierIntoDatabase(IWApplicationContext iwac) throws IOException {
		// get ip address
		try {
			// there is sometimes a problem here, see:
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4073539
			//myIP = "157.157.121.37";//myIPAddress.getHostAddress();
			Socket socket = SocketFactory.getDefault().createSocket();
			int hallo = socket.getLocalPort();
			String allo = socket.getLocalAddress().getHostAddress();
			myIP = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e) {
			getLogger().warning("[PeerIdentifierSetter] Own IP address could not be retrieved");
			throw new IOException("[PeerIdentifierSetter] Own IP address could not be retrieved");
		}
		// get identifier
		String path = iwac.getIWMainApplication().getApplicationRealPath();
		myIdentifier = Integer.toString(path.hashCode());
		// get other peers
		IWMainApplicationSettings iwMainApplicationSettings =iwac.getApplicationSettings();
		// step 1 reading
		String peerIPs = iwMainApplicationSettings.getProperty(APPLICATION_PEERS);
		if (findPortNumber(peerIPs)) {
			// nothing to do, entry found
			return true;
		}
		// add entry 
		StringBuffer buffer = new StringBuffer();
		if (peerIPs != null) {
			buffer.append(peerIPs);
			buffer.append(AD);
		}
		// assemble identifier  uri:myIdentifer
		int myPort = myTCPPort.intValue();
		URI myURI = null;
		try {
			myURI = new URI("tcp",null,myIP,myPort,null,null,null);
		}
		catch (URISyntaxException e1) {
			getLogger().warning("[PeerIdentifierSetter] Own IP address could not be created");
			throw new IOException("[PeerIdentifierSetter] Own IP address could not be created");
		}
		buffer.append(myURI.toString());
		buffer.append(HASH);
		buffer.append(myIdentifier);
		// extend peerIDs
		String extendedPeerIDs = buffer.toString(); 
		
		// step 2 writing
		iwMainApplicationSettings.setProperty(APPLICATION_PEERS, extendedPeerIDs);
		// go sleeping for a while
		try {
			Thread.sleep(4000);
		}
		catch (InterruptedException e) {
			getLogger().warning("[PeerURISetter] Sleeping thread was interrupted");
		}
		// check now if some other application has written to the database during the time between step 1 and step 2
		// do not use the cache read database
		ICApplicationBindingBusiness applicationBindingBusiness = getApplicationBindingBusiness(iwac);
		try {
			String secondPeerIds = applicationBindingBusiness.get(APPLICATION_PEERS);
			// not successful if the strings are different
			return secondPeerIds.equals(extendedPeerIDs);
		}
		catch (RemoteException re) {
			getLogger().warning("[PeerURISetter] ICApplicationBindingBusiness could not be found");
			throw new IBORuntimeException(re.getMessage());
		}
	}
	
	// returns true if a port was found false if a port has been created
	private boolean findPortNumber(String peerIPs) {
		if (peerIPs == null) {
			myTCPPort = DEFAULT_TCP_PORT;
			return false;
		}	
		// analyse peerIPs - create a list of found peers
		
		StringTokenizer tokenizer = new StringTokenizer(peerIPs,TOKEN, true);
		List foundPeersWithSameIP = new ArrayList();
		boolean uriFollows = true;
		URI currentURI = null;
		String currentPath = null;
		while (tokenizer.hasMoreTokens()) {
			String element = tokenizer.nextToken();
			if (AD.equals(element)) {
				uriFollows = true;
				storeCurrentPeer(currentURI, currentPath, foundPeersWithSameIP);
				// store current peer
				// reset current peer
				currentURI = null;
				currentPath = null;
			}
			else if (HASH.equals(element)) {
				uriFollows = false;
			}
			else if (uriFollows) {
				// element should be an URI
				try {
					currentURI = new URI(element);
				}
				catch (URISyntaxException e) {
					getLogger().warning("[PeerIdentifierSetter] URI syntax in database wrong: " + element);
					// ignore this entry by setting to null
					currentURI = null;
				}
					
			}
			else {
				currentPath = element;
			}
		}
		// do not forget to store the last peer
		storeCurrentPeer(currentURI, currentPath, foundPeersWithSameIP);
		
		// analyse all peers with the same ip address
			
		// case 1: no peers with same ip address found
		if (foundPeersWithSameIP.size() == 0) {
			myTCPPort = DEFAULT_TCP_PORT;
			// return not found
			return false;
		}
		// case 2: only one found - ignore identifier 
		// in that way someone can put an entry into the database without an identifier
		// or the application can be moved around on the same machine
		if (foundPeersWithSameIP.size() == 1) {
			// special case: someone put the entry in the database manually without an identifier, accept this case
			PeerIdentifier peerIdentifier = (PeerIdentifier) foundPeersWithSameIP.get(0);
			String tempIdentifer = peerIdentifier.getIdentifier();
			if (StringHandler.isEmpty(tempIdentifer)) {
				int tempPort = peerIdentifier.getUri().getPort();
				myTCPPort = new Integer(tempPort);
				// return found
				return true;
			}
		}
		// case 3: one or more peers with identifiers found
		List usedPorts = new ArrayList();
		Iterator iterator = foundPeersWithSameIP.iterator();
		while (iterator.hasNext()) {
			PeerIdentifier peerIdentifier = (PeerIdentifier) iterator.next();
			String identifier = peerIdentifier.getIdentifier();
			int tempPort = peerIdentifier.getUri().getPort();
			if (myIdentifier.equals(identifier)) {
				myTCPPort = new Integer(tempPort);
			}
			else {
				// add the one with the different identifier to the others peers
				seedPeers.add(peerIdentifier);
				usedPorts.add(new Integer(tempPort));
			}
		}
		if (myTCPPort != null) {
			// return found
			return true;
		}
		// find a port number

		// start at 9700 that is the default
		myTCPPort = DEFAULT_TCP_PORT;
		while (usedPorts.contains(myTCPPort)) {
			myTCPPort = new Integer(myTCPPort.intValue() + 2);
		}
		// not found
		return false;
	}
	
	private void storeCurrentPeer(URI currentURI, String currentPath, List foundPeersWithSameIP) {
		if (StringHandler.isNotEmpty(currentURI)) {
			PeerIdentifier currentPeer = new PeerIdentifier(currentURI, currentPath);
			String ip = currentURI.getHost();
			if (ip.equals(myIP)) {
				foundPeersWithSameIP.add(currentPeer);
			}
			else {
				seedPeers.add(currentPeer);
			}
		}
	}


	private static ICApplicationBindingBusiness getApplicationBindingBusiness(IWApplicationContext iwac) {
		try {
			return (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
		}
		catch (IBOLookupException ex) {
			getLogger().warning("[PeerIdentifierSetter] ICApplicationBindingBusiness could not be found");
			throw new IBORuntimeException(ex.getMessage());
		}
	}
	


}
