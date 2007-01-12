/*
 * Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *       Sun Microsystems, Inc. for Project JXTA."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact Project JXTA at http://www.jxta.org.
 *
 * 5. Products derived from this software may not be called "JXTA",
 *    nor may "JXTA" appear in their name, without prior written
 *    permission of Sun.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Project JXTA.  For more
 * information on Project JXTA, please see
 * <http://www.jxta.org/>.
 *
 * This license is based on the BSD license adopted by the Apache Foundation.
 *
 */

package com.idega.cluster;

import java.io.StringWriter;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.MimeMediaType;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;

public class JoinDemo   {
    
    static PeerGroup myGroup = null;    // my initial group
    private DiscoveryService discoSvc;
    
    public static void main(String args[]) {
        
        System.out.println("Starting JoinDemo ....");
        JoinDemo myapp = new JoinDemo();
        
        myapp.startJxta();
        PeerGroup newGroup = myapp.createGroup();
        if (newGroup != null) {
            myapp.joinGroup(newGroup);
        }
        System.out.println("Good Bye ....");
        //System.exit(0);
    }

    private void startJxta() {
        try {
            // create, and Start the default jxta NetPeerGroup
            myGroup = PeerGroupFactory.newNetPeerGroup();
        } catch (PeerGroupException e) {
            // could not instantiate the group, print the stack and exit
            System.out.println("fatal error : group creation failure");
            e.printStackTrace();
            //System.exit(1);
        }
        
        // Extract the discovery service from our peer group
        discoSvc = myGroup.getDiscoveryService();
    }
    
    private PeerGroup createGroup() {
        PeerGroup pg;               // new peer group
        PeerGroupAdvertisement adv; // advertisement for the new peer group
        
        System.out.println("Creating a new group advertisement");
        
        try {
            // create a new all purpose peergroup.
            ModuleImplAdvertisement implAdv =
		myGroup.getAllPurposePeerGroupImplAdvertisement();
            
            pg = myGroup.newGroup(null,                // Assign new group ID
				  implAdv,              // The implem. adv
				  "JoinTest",            // The name
				  "testing group adv"); // Helpful descr.
            
            // print the name of the group and the peer group ID
            adv = pg.getPeerGroupAdvertisement();
            PeerGroupID GID = adv.getPeerGroupID(); 
            PipeService pipeService = pg.getPipeService();  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            pipeService.toString(); //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            System.out.println("  Group = " +adv.getName() +
			       "\n  Group ID = " + GID.toString());
            
        }
        catch (Exception eee) {
            System.out.println("Group creation failed with " + eee.toString());
            return (null);
        }
        
        try {
            // publish this advertisement
            // (send out to other peers and rendezvous peer)
            discoSvc.remotePublish(adv);
            System.out.println("Group published successfully.\n");
        }
        catch (Exception e) {
            System.out.println("Error publishing group advertisement");
            e.printStackTrace();
            return (null);
        }
        
        return(pg);
        
    }
    
    private void joinGroup(PeerGroup grp) {
        System.out.println("Joining peer group...");
        
        StructuredDocument creds = null;
        
        try {
            // Generate the credentials for the Peer Group
            AuthenticationCredential authCred =
		new AuthenticationCredential( grp, null, creds );
            
            // Get the MembershipService from the peer group
            MembershipService membership = grp.getMembershipService();
            
            // Get the Authenticator from the Authentication creds
            Authenticator auth = membership.apply( authCred );
            
            // Check if everything is okay to join the group
            if (auth.isReadyForJoin()){
                Credential myCred = membership.join(auth);

                System.out.println("Successfully joined group " +
				   grp.getPeerGroupName());
                
                // display the credential as a plain text document.
                System.out.println("\nCredential: ");
                StructuredTextDocument doc = (StructuredTextDocument)
		    myCred.getDocument(new MimeMediaType("text/plain"));
                
                StringWriter out = new StringWriter();
                doc.sendToWriter(out);
                System.out.println(out.toString());
                out.close();
            }
            else
                System.out.println("Failure: unable to join group");
        }
        catch (Exception e){
            System.out.println("Failure in authentication.");
            e.printStackTrace();
        }
    }
}
