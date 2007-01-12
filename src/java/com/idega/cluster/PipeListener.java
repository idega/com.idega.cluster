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
 *  $Id: PipeListener.java,v 1.2 2007/01/12 15:43:40 thomas Exp $
 */
import java.io.FileInputStream;
import java.util.Date;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.CountingOutputStream;
import net.jxta.util.DevNullOutputStream;
/**
 * this application creates an instance of an input pipe,
 * and waits for msgs on the input pipe
 *
 */

public class PipeListener implements PipeMsgListener {

    static PeerGroup netPeerGroup = null;
    private final static String SenderMessage = "PipeListenerMsg";

    private PipeService pipe;
    private PipeAdvertisement pipeAdv;
    private InputPipe pipeIn = null;

    /**
     *  main
     *
     * @param  args  command line args
     */
    public static void main(String args[]) {

        PipeListener myapp = new PipeListener();
        myapp.startJxta();
        myapp.run();
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

    /**
     * wait for msgs
     *
     */

    public void run() {

        try {
            // the following creates the inputpipe, and registers "this"
            // as the PipeMsgListener, when a message arrives pipeMsgEvent is called
            System.out.println("Creating input pipe");
            pipeIn = pipe.createInputPipe(pipeAdv, this);
        } catch (Exception e) {
            return;
        }
        if (pipeIn == null) {
            System.out.println(" cannot open InputPipe");
            System.exit(-1);
        }
        System.out.println("Waiting for msgs on input pipe");
    }


    /**
     * Starts jxta
     *
     */
    private void startJxta() {
        try {
            // create, and Start the default jxta NetPeerGroup
            netPeerGroup = PeerGroupFactory.newNetPeerGroup();

            // uncomment the following line if you want to start the app defined
            // the NetPeerGroup Advertisement (by default it's the shell)
            // at which case you must include jxtashell.jar in the classpath
            // in this case we want use jxta directly.

            // netPeerGroup.startApp(null);

        }
        catch (PeerGroupException e) {
            // could not instantiate the group, print the stack and exit
            System.out.println("fatal error : group creation failure");
            e.printStackTrace();
            System.exit(1);
        }

        pipe = netPeerGroup.getPipeService();
        System.out.println("Reading in pipexample.adv");
        try {
        	FileInputStream is = new FileInputStream("/Users/thomas/workspaces/workspace_ePlatform_rvk_20061127/applications/reykjavik/target/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties/pipexample.adv");
            //FileInputStream is = new FileInputStream("/Users/thomas/workspaces/targets/tomcat2/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties/pipexample.adv");

            pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(MimeMediaType.XMLUTF8, is);
            is.close();
        } catch (Exception e) {
            System.out.println("failed to read/parse pipe advertisement");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * By implementing PipeMsgListener, define this method to deal with
     * messages as they arrive
     */

    public void pipeMsgEvent(PipeMsgEvent event) {

        Message msg=null;
        try {
            // grab the message from the event
            msg = event.getMessage();
            if (msg == null) {
                return;
            }
            printMessageStats(msg, true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // get all the message elements
        Message.ElementIterator en = msg.getMessageElements();
        if (!en.hasNext()) {
            return;
        }

        // get the message element named SenderMessage
        MessageElement msgElement = msg.getMessageElement(null, SenderMessage);
        // Get message
        if (msgElement.toString() == null) {
            System.out.println("null msg received");
        } else {
            Date date = new Date(System.currentTimeMillis());
            System.out.println("Message received at :"+ date.toString());
            System.out.println("Message  created at :"+ msgElement.toString());
        }
    }

}

