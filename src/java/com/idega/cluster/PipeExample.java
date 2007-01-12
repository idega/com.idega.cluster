package com.idega.cluster;
/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL SUN MICROSYSTEMS OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  =========================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PipeExample.java,v 1.2 2007/01/12 15:43:40 thomas Exp $
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.rendezvous.RendezVousService;

/**
 *  This exapmle illustrates how to use the OutputPipeListener interface
 *
 */

public class PipeExample implements
                              Runnable,
                              OutputPipeListener,
                              RendezvousListener {

    static PeerGroup netPeerGroup = null;
    private final static String SenderMessage = "PipeListenerMsg";
    private PipeService pipe;
    private DiscoveryService discovery;
    private PipeAdvertisement pipeAdv;
    private RendezVousService rendezvous;

    /**
     *  main
     *
     *@param  args  command line arguments
     */
    public static void main(String args[]) {
        PipeExample myapp = new PipeExample();
        myapp.startJxta();
        myapp.run();
    }

    /**
     *  the thread which creates (resolves) the output pipe
     *  and sends a message once it's resolved
     */

    public synchronized void run() {
        try {
            // this step helps when running standalone (local sub-net without any redezvous setup)
            discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null, 1, null);
            // create output pipe with asynchronously
            // Send out the first pipe resolve call
            System.out.println("Attempting to create a OutputPipe");
            pipe.createOutputPipe(pipeAdv, this);
            // send out a second pipe resolution after we connect
            // to a rendezvous
            if (!rendezvous.isConnectedToRendezVous()) {
                System.out.println("Waiting for Rendezvous Connection");
                try {
                    wait();
                    System.out.println("Connected to Rendezvous, attempting to create a OutputPipe");
                    pipe.createOutputPipe(pipeAdv, this);
                } catch (InterruptedException e) {
                    // got our notification
                }
            }
        } catch (IOException e) {
            System.out.println("OutputPipe creation failure");
            e.printStackTrace();
            System.exit(-1);
        }
    }


    /**
     *  by implementing OutputPipeListener we must define this method which
     *  is called when the output pipe is created
     *
     *@param  event  event object from which to get output pipe object
     */

    public void outputPipeEvent(OutputPipeEvent event) {

        System.out.println(" Got an output pipe event");
        OutputPipe op = event.getOutputPipe();
        Message msg = null;

        try {
            System.out.println("Sending message");
            msg = new Message();
            Date date = new Date(System.currentTimeMillis());
            StringMessageElement sme = new StringMessageElement(SenderMessage, date.toString() , null);
            msg.addMessageElement(null, sme);
            op.send(msg);
        } catch (IOException e) {
            System.out.println("failed to send message");
            e.printStackTrace();
            System.exit(-1);
        }
        op.close();
        System.out.println("message sent");
    }

    /**
     *  rendezvousEvent the rendezvous event
     *
     *@param  event   rendezvousEvent
     */
    public synchronized void rendezvousEvent(RendezvousEvent event) {
        if (event.getType() == event.RDVCONNECT ||
            event.getType() == event.RDVRECONNECT ) {
            notify();
        }
    }

    /**
     *  Starts jxta, and get the pipe, and discovery service
     */
    private void startJxta() {
        try {
            // create, and Start the default jxta NetPeerGroup
            netPeerGroup = PeerGroupFactory.newNetPeerGroup();
            rendezvous = netPeerGroup.getRendezVousService();
            rendezvous.addListener(this);
            // uncomment the following line if you want to start the app defined
            // the NetPeerGroup Advertisement (by default it's the shell)
            // in this case we want use jxta directly.
            // netPeerGroup.startApp(null);

        } catch (PeerGroupException e) {
            // could not instantiate the group, print the stack and exit
            System.out.println("fatal error : group creation failure");
            e.printStackTrace();
            System.exit(-1);
        }

        // get the pipe service, and discovery
        pipe = netPeerGroup.getPipeService();
        discovery = netPeerGroup.getDiscoveryService();
        System.out.println("Reading in pipexample.adv");
        try {
        	FileInputStream is = new FileInputStream("/Users/thomas/workspaces/workspace_ePlatform_rvk_20061127/applications/reykjavik/target/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties/pipexample.adv");

            //FileInputStream is = new FileInputStream("/Users/thomas/workspaces/targets/targetA3/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties/pipexample.adv");
            pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(MimeMediaType.XMLUTF8, is);
            is.close();
        } catch (Exception e) {
            System.out.println("failed to read/parse pipe advertisement");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

