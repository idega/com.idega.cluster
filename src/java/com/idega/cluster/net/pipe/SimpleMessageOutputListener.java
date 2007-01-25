/*
 * $Id: SimpleMessageOutputListener.java,v 1.3 2007/01/25 09:25:14 thomas Exp $
 * Created on Dec 28, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.pipe;

import java.io.IOException;
import net.jxta.discovery.DiscoveryService;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.message.SimpleMessage;


/**
 * 
 *  Last modified: $Date: 2007/01/25 09:25:14 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class SimpleMessageOutputListener implements OutputPipeListener, RendezvousListener, Runnable {
	
	private SimpleMessage messageToSend = null;
	private RendezVousService rendezVousService = null;
	private DiscoveryService discoveryService = null;
	private PipeService pipeService = null;
	private PipeAdvertisement pipeAdvertisement = null;
	
	public SimpleMessageOutputListener(
			SimpleMessage messageToSend, 
			RendezVousService rendezVousService,
			DiscoveryService discoveryService,
			PipeService pipeService, 
			PipeAdvertisement pipeAdvertisement) {
		this.messageToSend = messageToSend;
		this.rendezVousService = rendezVousService;
		this.discoveryService = discoveryService;
		this.pipeService = pipeService;
		this.pipeAdvertisement = pipeAdvertisement;
	}
	

	public void run() {
		createOutputPipe();
	}

	private synchronized void createOutputPipe() {
		try {
            // this step helps when running standalone (local sub-net without any redezvous setup)
			discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null, 1, null);
			pipeService.createOutputPipe(pipeAdvertisement, this);
			if (! JxtaConfigSettings.USE_RENDEZVOUS_SERVICE) {
				return;
			}
            // send out a second pipe resolution after we connect
            // to a rendezvous
			if (!rendezVousService.isConnectedToRendezVous()) {
				addRendezvousListener();
                try {
                    wait(JxtaConfigSettings.WAIT_FOR_RENDEZVOUS);
                   	pipeService.createOutputPipe(pipeAdvertisement, this);
                } 
                catch (InterruptedException e) { 
                    // got our notification
				}
                removeRendezVousListener();
			}

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void outputPipeEvent(OutputPipeEvent event) {
        OutputPipe op = event.getOutputPipe();
        try {
            messageToSend.send(op);
        } catch (IOException e) {
            System.err.println("failed to send message");
            e.printStackTrace();
            //System.exit(-1);
        }
        op.close();
    }
	
	private void addRendezvousListener() {
		rendezVousService.addListener(this);
	}
	
	private void removeRendezVousListener() {
		rendezVousService.removeListener(this);
	}
	
    /**
     *  rendezvousEvent the rendezvous event
     *
     *@param  event   rendezvousEvent
     */
    public synchronized void rendezvousEvent(RendezvousEvent event) {
        if (event.getType() == RendezvousEvent.RDVCONNECT ||
            event.getType() == RendezvousEvent.RDVRECONNECT ) {
            notify();
        }
    }


}
