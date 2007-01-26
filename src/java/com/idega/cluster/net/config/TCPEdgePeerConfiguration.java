/*
 * $Id: TCPEdgePeerConfiguration.java,v 1.2 2007/01/26 07:15:02 thomas Exp $
 * Created on Dec 27, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.MulticastAddress;
import net.jxta.ext.config.Profile;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import com.idega.cluster.JxtaConfig;
import com.idega.cluster.net.config.id.IDApplicationFactory;
import com.idega.idegaweb.ApplicationProductInfo;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;


/**
 * 
 *  Last modified: $Date: 2007/01/26 07:15:02 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @author  danielbrookshier
 * 
 * @version $Revision: 1.2 $
 */
public class TCPEdgePeerConfiguration {
	
	/*
	see http://bench.jxta.org/config.html#rdv
	
	RENDEZVOUS AND RELAY CONFIGURATION
	
	TCP Edge Peer
	*/

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JxtaConfig.class.getName());
    
    
    public static void createPlatformConfig(IWApplicationContext iwac) {
    	IWMainApplication mainApplication = iwac.getIWMainApplication();
    	// set JXTA home, should be private!
      	File jxtaHome = defineJxtaHome(mainApplication);
      	// clean jxtaHome
    	prepareJxtaHome(jxtaHome);
    	
      	ApplicationProductInfo productInfo = mainApplication.getProductInfo();
      	String peerName = productInfo.getName();
      	
   		new TCPEdgePeerConfiguration(peerName, "password", "secretpassword", jxtaHome, iwac);
    }

    public static File defineJxtaHome(IWMainApplication mainApplication) {
    	File jxtaHome = new File(mainApplication.getPropertiesRealPath(), "jxtaHome");
      	System.setProperty("JXTA_HOME", jxtaHome.getAbsolutePath());
      	return jxtaHome;
    }
    
    public static void prepareJxtaHome(File jxtaHome) {
    	String jxtaPath = jxtaHome.getAbsolutePath();
    	FileUtil.createFolder(jxtaPath);
    	FileUtil.deleteContentOfFolder(jxtaHome);
    }
    
	public TCPEdgePeerConfiguration(String peerName, String id, String password, File jxtaHome, IWApplicationContext iwac) {
        LOG.setLevel(org.apache.log4j.Level.INFO);
       
        try{
       	
            // change to private net
            PeerGroupID infrastructurePeerGroupID = IDApplicationFactory.getInfrastructurePeerGroupID(iwac);
            String infraStructurePeerGroupName = "privateNet"+peerName;
            String infraStructurePeerGroupDes = "desPrivateNet"+peerName;
        	
            // write file "config.properties" that is read by other classes like PeerGroupFactory
            String infrastructurePeerGroupIDAsString = infrastructurePeerGroupID.toURI().toString();
	        writeConfigProperties(jxtaHome, infrastructurePeerGroupIDAsString, infraStructurePeerGroupName, infraStructurePeerGroupDes);
	        	
            //LOG.info("JXTA_HOME="+jxtaHome.getCanonicalPath());
            Profile profile = Profile.DEFAULT; // File("./jxta_profile.xml").toURL());//"./superPeer.xml").toURL());//
            
            Configurator configurator =  new Configurator(jxtaHome.toURI(),profile);
            
            // change configurator 
            configurator.setInfrastructurePeerGroupName(infraStructurePeerGroupName);
            configurator.setInfrastructurePeerGroupDescription(infraStructurePeerGroupDes);
            configurator.setInfrastructurePeerGroupId(infrastructurePeerGroupID);
	            
            // change also PeerGroupFactory because the PeerGroupFactory might be initialized wrong by
            // reading a wrong config.properties file or using the defalut values if config.proeprties file was not created yet
            PeerGroupFactory.setNetPGName(infraStructurePeerGroupName);
            PeerGroupFactory.setNetPGDesc(infraStructurePeerGroupDes);
            PeerGroupFactory.setNetPGID(infrastructurePeerGroupID);

            // set this peer
            configurator.setName(peerName); 
            configurator.setSecurity(id, password);
            
            // available tcp rendezvous 
            URI myRendezvous = new URI("tcp", null, "157.157.121.38", 9701, null,null, null );
            configurator.addRendezVous(myRendezvous);
            // switch off redezvous discovery otherwise the rendezvous will be removed
            configurator.setRendezVousDiscovery(false);
            
            
            // use a relay
            URI myRelay = new URI("tcp", null, "157.157.121.38", 9701, null,null, null );
            configurator.addRelay(myRelay);
            
            LOG.info(" isProxy:"+configurator.isProxy());
            LOG.info(" isReconfigure:"+configurator.isReconfigure());
            LOG.info(" isRelay:"+configurator.isRelay());
            LOG.info(" isRelayIncoming:"+configurator.isRelayIncoming());
            LOG.info(" isRelayOutgoing:"+configurator.isRelayOutgoing());
            LOG.info(" isRelaysDiscovery:"+configurator.isRelaysDiscovery());
            LOG.info(" isRendezVous:"+configurator.isRendezVous());
            LOG.info(" isRendezVousAutoStart:"+configurator.isRendezVousAutoStart());
            LOG.info(" isRendezVousDiscovery:"+configurator.isRendezVousDiscovery());
            LOG.info(" isSecurity:"+configurator.isSecurity());
            
            LOG.info(" getDescription:"+configurator.getDescription());
            LOG.info(" getDescriptor:"+configurator.getDescriptor());
            LOG.info(" getEndpointOutgoingQueueSize:"+configurator.getEndpointOutgoingQueueSize());
            LOG.info(" getName:"+configurator.getName());
            LOG.info(" getPeerId:"+configurator.getPeerId());
            LOG.info(" getPeerProxyAddress:"+configurator.getPeerProxyAddress());
            LOG.info(" getPrincipal:"+configurator.getPrincipal());
            LOG.info(" getRelayIncomingLease:"+configurator.getRelayIncomingLease());
            LOG.info(" getRelayIncomingMaximum:"+configurator.getRelayIncomingMaximum());
            LOG.info(" getRelayOutgoingLease:"+configurator.getRelayOutgoingLease());
            LOG.info(" getRelayOutgoingMaximum:"+configurator.getRelayOutgoingMaximum());
            LOG.info(" getRelayQueueSize:"+configurator.getRelayQueueSize());
            LOG.info(" getRelaysBootstrapAddress:"+configurator.getRelaysBootstrapAddress());
            // why isn´t there are method? LOG.info(" getRelaysDiscovery:"+configurator.getRelaysDiscovery());
            LOG.info(" getRendezVousAutoStart:"+configurator.getRendezVousAutoStart());
            LOG.info(" getRendezVousBootstrapAddress:"+configurator.getRendezVousBootstrapAddress());
            LOG.info(" getRootCertificate:"+configurator.getRootCertificate());
            LOG.info(" getRootCertificateAddress:"+configurator.getRootCertificateAddress());
            LOG.info(" getRootCertificateBase64:"+configurator.getRootCertificateBase64());
            //configurator.getTrace();//??
            LOG.info("Relay seeded");
            List relays = configurator.getRelays();
            for (int relay = 0;relay < relays.size();relay++){
                LOG.info("  Relay("+relay+"):"+relays.get(relay));
                
            }
            LOG.info("RDV seeded");
            List rdvs  = configurator.getRendezVous();
            for (int rdv = 0;rdv < rdvs.size();rdv++){
                LOG.info("  Rendezvous("+rdv+"):"+rdvs.get(rdv));
                
            }
            LOG.info("Optimizers");
            Iterator iter = configurator.getOptimizers();
            int optimizerCount = 0;
            while(iter.hasNext()){
                Object next = iter.next();
                LOG.info("  Optomizers("+ optimizerCount +"):"+next);
                optimizerCount++;
            }
            
            // configurator has to be saved to set the default transports values
            
            saveConfiguration(configurator);            

            // after that change the deault values
            
            List list = configurator.getTransports();
            for (int i = 0;i < list.size();i++){
                LOG.info("Transports("+i+"):"+list.get(i).getClass().getName()+" "+list.get(i));
                
                // TCP settings
                if (list.get(i) instanceof net.jxta.ext.config.TcpTransport){
                    net.jxta.ext.config.TcpTransport tcpData = (net.jxta.ext.config.TcpTransport)list.get(i);
                    
                    // enable tcp 
                    tcpData.setEnabled(true);
                    
                    // enable outgoing
                    tcpData.setOutgoing(true);
                    
                    // enable incoming
                    tcpData.setIncoming(false);
                    
                    TcpTransportAddress tcpAddress = (TcpTransportAddress) tcpData.getAddresses().get(0);

                    // multicast off
                    MulticastAddress multicast = (MulticastAddress) tcpAddress.getMulticastAddresses().get(0);
                    multicast.setMulticast(false);
                    
                    LOG.info("  isEnabled:"+ tcpData.isEnabled());
                    LOG.info("  isIncoming:"+ tcpData.isIncoming());
                    LOG.info("  isOutgoing:"+ tcpData.isOutgoing());
                    LOG.info("  isProxy:"+ tcpData.isProxy());
                    LOG.info("  getProxyAddress:"+ tcpData.getProxyAddress());
                    LOG.info("  getPublicAddresses:"+ tcpData.getPublicAddresses());
                    LOG.info("  getScheme:"+ tcpData.getScheme());
                    
                    List listAddr = tcpData.getAddresses();
                    for (int a = 0;i < listAddr.size();i++){
                        LOG.info("  Addresses("+i+"):"+listAddr.get(i).getClass().getName()+" "+listAddr.get(i));
                    }
                }
                
                // HTTP settings
                if (list.get(i) instanceof net.jxta.ext.config.HttpTransport){
                    net.jxta.ext.config.HttpTransport httpData = (net.jxta.ext.config.HttpTransport)list.get(i);
                    
                    // enable HTTP
                    httpData.setEnabled(false);

                    LOG.info("  isEnabled:"+ httpData.isEnabled());
                    LOG.info("  isIncoming:"+ httpData.isIncoming());
                    LOG.info("  isOutgoing:"+ httpData.isOutgoing());
                    LOG.info("  isProxy:"+ httpData.isProxy());
                    LOG.info("  getProxyAddress:"+ httpData.getProxyAddress());
                    LOG.info("  getPublicAddresses:"+ httpData.getPublicAddresses());
                    LOG.info("  getScheme:"+ httpData.getScheme());
                    List listAddr = httpData.getAddresses();
                    for (int j = 0; j < listAddr.size();  j++){
                        LOG.info("  Addresses("+ j +"):"+listAddr.get(j).getClass().getName()+" "+listAddr.get(j));
                    }
                }
            }
            LOG.info("  getPlatformConfig:"+configurator.getPlatformConfig());

            saveConfiguration(configurator);

        }catch(Exception e ){
            LOG.error("Error initializing configuration",e);
        }
    }
	
	private void saveConfiguration(Configurator configurator) {
		try{
            if (configurator.save()){
                LOG.info(">>>>>>> successful save ");
            }
            else {
                LOG.info("failed to save config");
            }
        }
		catch(Exception e){
            LOG.error("Problem saving configuration", e);
        }
		
	}
    
    private void writeConfigProperties(File jxtaHome, String netPeerGroupID, String netPeerGroupName, String netPeerGroupDesc) throws IOException {
    	File file = new File(jxtaHome,"config.properties");
    	FileWriter writer = new FileWriter(file);
        writer.write("NetPeerGroupID=" + netPeerGroupID + "\n");
        writer.write("NetPeerGroupName=" + netPeerGroupName +"\n");
        writer.write("NetPeerGroupDesc="+ netPeerGroupDesc + "\n");
        writer.close();
    }
   
 }



