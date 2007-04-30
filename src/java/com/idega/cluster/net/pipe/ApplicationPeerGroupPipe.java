/*
 * $Id: ApplicationPeerGroupPipe.java,v 1.5 2007/04/30 13:17:39 thomas Exp $
 * Created on Dec 21, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.pipe;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.jxta.discovery.DiscoveryService;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.peer.PeerID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.util.CountingOutputStream;
import net.jxta.util.DevNullOutputStream;
import net.jxta.util.PipeUtilities;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.config.id.IDApplicationFactory;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.cluster.net.message.MessageListener;
import com.idega.cluster.net.message.ReceiveFilter;
import com.idega.cluster.net.message.SendFilter;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.cluster.net.message.impl.SimpleMessageImpl;
import com.idega.cluster.net.peergroup.ApplicationPeerGroup;
import com.idega.idegaweb.IWApplicationContext;


public class ApplicationPeerGroupPipe implements PipeMsgListener, ApplicationMessenger {
	
	public static final String APPLICATION_PEER_GROUP_PIPE_NAME = "idegaWeb application group pipe";
	

	
	private boolean active = false;

	private ApplicationPeerGroup applicationPeerGroup = null;
	
	PipeAdvertisement  pipeAdvertisement = null;
	private PipeService pipeService = null;
	private RendezVousService rendezVousService = null;
	private DiscoveryService discoveryService = null;

	private MessageQueue messageQueue = null;
	
	private List messageListeners = null;
	private List sendFilters = null;
	private List receiveFilters = null;
	
	// sender
	private String peerID = null;
	
	public boolean start(IWApplicationContext iwac) {
		initialize(iwac);
		return active;
	}

	public boolean stop(IWApplicationContext iwac) {
		destroy();
		return ! active;
	}
	
	private void initialize(IWApplicationContext iwac) {
		if (createAppliactionPeerGroup(iwac)) {
			if (createPipe(iwac)) {
				messageQueue = new MessageQueue();
				active = true;
				return;
			}
		}
		destroy();
		
	}
	
	private boolean createAppliactionPeerGroup(IWApplicationContext iwac) {
        // creating private idega application group
		applicationPeerGroup = new ApplicationPeerGroup(iwac);
		return applicationPeerGroup.isActive();
	}
	
	
	private boolean createPipe(IWApplicationContext iwac)  {
		PeerID peerIdentifier = applicationPeerGroup.getPeerID();
		URI peerURI = peerIdentifier.toURI();
		// peerID is the identifier used for sender
		peerID = peerURI.toString();
		pipeService = applicationPeerGroup.getPipeService();
		try {
//        	String path = System.getProperty("JXTA_HOME");
//        	File jxtaHome = new File(path);
//        	File pipexampleAdv = new File(jxtaHome.getParentFile(), "pipexample.adv");
//        	FileInputStream is = new FileInputStream(pipexampleAdv);
//            pipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(MimeMediaType.XMLUTF8, is);			
			
			
			PipeID pipeID = IDApplicationFactory.getApplicationPeerGroupPipeID(applicationPeerGroup, iwac);
			pipeAdvertisement = PipeUtilities.createPipeAdvertisement(pipeID, PipeService.PropagateType);
			pipeAdvertisement.setName(APPLICATION_PEER_GROUP_PIPE_NAME);
            
			pipeService.createInputPipe(pipeAdvertisement, this);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	public synchronized void destroy() {
		active = false;
		if (messageQueue != null) {
			messageQueue.destroy();
		}
		if (pipeService != null) {
			pipeService.stopApp();
		}
		if (rendezVousService != null) {
			rendezVousService.stopApp();
		}
		if (discoveryService != null) {
			discoveryService.stopApp();
		}
		if (applicationPeerGroup != null) {
			applicationPeerGroup.destroy();
		}
		messageQueue = null;
		discoveryService = null;
		rendezVousService = null;
		pipeService = null;
		applicationPeerGroup = null;
		pipeAdvertisement = null;
	}

	public void pipeMsgEvent(PipeMsgEvent event) {
		if (! active) {
			return;
		}
		Message msg=null;
        try {
            // grab the message from the event
            msg = event.getMessage();
            if (msg == null) {
                return;
            }
            //printMessageStats(msg, true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
     
        SimpleMessage simpleMessage = SimpleMessageImpl.getInstanceByReceivedMessage(msg);

		// apply filters
		if (receiveFilters != null) {
			Iterator iterator = receiveFilters.iterator();
			while (iterator.hasNext()) {
				ReceiveFilter receiveFilter = (ReceiveFilter) iterator.next();
				if (! receiveFilter.isAccepted(simpleMessage)) {
					return;
				}
			}
		}
        
        // notify listeners
        if (messageListeners != null) {	        
	        Iterator iterator = messageListeners.iterator();
	        while (iterator.hasNext()) {
	        	MessageListener messageListener = (MessageListener) iterator.next();
	        	messageListener.gotMessage(simpleMessage);
	        }
        }
	}
	
    public static void printMessageStats(Message msg, boolean verbose) {
        try {
            CountingOutputStream cnt;
            ElementIterator it = msg.getMessageElements();
            System.out.println("------------------Begin Message---------------------");
            //WireFormatMessage serialed = WireFormatMessageFactory.toWire(
               //                              msg,
               //                              new MimeMediaType("application/x-jxta-msg"), (MimeMediaType[]) null);
            //System.out.println("Message Size :" + serialed.getByteLength());
            while (it.hasNext()) {
                MessageElement el = (MessageElement) it.next();
                String eName = el.getElementName();
                cnt = new CountingOutputStream(new DevNullOutputStream());
                el.sendToStream(cnt);
                long size = cnt.getBytesWritten();
                System.out.println("Element " + eName + " : " + size);
                if (verbose) {
                    System.out.println("["+el+"]");
                }
            }
            System.out.println("-------------------End Message----------------------");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    
    public String getSender() {
    	return peerID;
    }
    

	public void addReceiveListener(MessageListener messageListener) {
		if (messageListeners == null) {
			messageListeners = new ArrayList();
		}
		messageListeners.add(messageListener);
		
	}
	
	public void addSendFilter(SendFilter sendFilter) {
		if (sendFilters == null) {
			sendFilters = new ArrayList();
		}
		sendFilters.add(sendFilter);
		
	}
	
	public void addReceiveFilter(ReceiveFilter receiveFilter) {
		if (receiveFilters == null) {
			receiveFilters = new ArrayList();
		}
		receiveFilters.add(receiveFilter);
		
	}

	public void sendMessage(SimpleMessage messageToSend) {
		if (! active) {
			return;
		}
		// 
		if (JxtaConfigSettings.USE_RENDEZVOUS_SERVICE) {
			if (rendezVousService == null) {
				rendezVousService = applicationPeerGroup.getRendezVousService();
				//rendezVousService.startRendezVous();
			}
			if (discoveryService == null) {
				discoveryService = applicationPeerGroup.getDiscoveryService();
			}
		}
		// apply filters
		if (sendFilters != null) {
			Iterator iterator = sendFilters.iterator();
			while (iterator.hasNext()) {
				SendFilter sendFilter = (SendFilter) iterator.next();
				if (! sendFilter.isAccepted(messageToSend)) {
					return;
				}
			}
		}
		
		// use queue because OutputListener might wait 
		Runnable outputPipeListener =  new SimpleMessageOutputListener(messageToSend, rendezVousService, discoveryService, pipeService, pipeAdvertisement);
		messageQueue.add(outputPipeListener);
	}



	
}
