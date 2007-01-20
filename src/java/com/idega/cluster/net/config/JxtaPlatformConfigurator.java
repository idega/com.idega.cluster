/*
 * $Id: JxtaPlatformConfigurator.java,v 1.2 2007/01/20 21:55:17 thomas Exp $
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import net.jxta.ext.config.Address;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.MulticastAddress;
import net.jxta.ext.config.Profile;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import com.idega.cluster.JxtaConfig;
import com.idega.cluster.net.id.IDApplicationFactory;
import com.idega.idegaweb.ApplicationProductInfo;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;


/**
 * 
 *  Last modified: $Date: 2007/01/20 21:55:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @author  danielbrookshier
 * 
 * @version $Revision: 1.2 $
 */
public class JxtaPlatformConfigurator {

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JxtaConfig.class.getName());
    private static final String jxtaCoreFile = "jxta_core.properties";
    
    
    public static void createPlatformConfig(IWApplicationContext iwac) {
    	IWMainApplication mainApplication = iwac.getIWMainApplication();
    	// set JXTA home, should be private!
      	File jxtaHome = defineJxtaHome(mainApplication);
      	// clean jxtaHome
    	prepareJxtaHome(jxtaHome);
    	
      	ApplicationProductInfo productInfo = mainApplication.getProductInfo();
      	String peerName = productInfo.getName();
      	
      	JxtaPlatformConfigurator jxtaPlatformConfigurator = 
      		new JxtaPlatformConfigurator(peerName, "password", "secretpassword", jxtaHome, iwac);
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
    
    
    public JxtaPlatformConfigurator(String peerName, String id, String password, File jxtaHome, IWApplicationContext iwac) {
        LOG.setLevel(org.apache.log4j.Level.INFO);
      
//        Properties defaultProps = new Properties();
//        try{
//            FileInputStream in = new FileInputStream(jxtaCoreFile);
//            defaultProps.load(in);
//            in.close();
//        }catch(Exception e){
//            LOG.info("There was a problem loading the file:"+jxtaCoreFile+ ". Using defaults");
//        }
//
//        String infraBase = defaultProps.getProperty("net.cluck.infraBase","DEFAULT-BASE").trim();
//        String infraSeed = defaultProps.getProperty("net.cluck.infraSeed","DEFAULT-SEED").trim();
//        String infraName = defaultProps.getProperty("net.cluck.infraName","DEFAULT-NAME").trim();
//        String infraDesc = defaultProps.getProperty("net.cluck.infraDesc", "Default Description - You probably need to create a profile for the private infrastructure peer group").trim();
//        
//        LOG.info("property net.cluck.infraBase="+infraBase);
//        LOG.info("property net.cluck.infraSeed="+infraSeed);
//        LOG.info("property net.cluck.infraName="+infraName);
//        LOG.info("property net.cluck.infraDesc="+infraDesc);
//        
//        //createPrivateGroup(infraBase,infraSeed,infraName,infraDesc);
        
        try{

        	
            // change to private net
            PeerGroupID infrastructurePeerGroupID = IDApplicationFactory.getInfrastructurePeerGroupID(iwac);
            String infraStructurePeerGroupName = "privateNet"+peerName;
            String infraStructurePeerGroupDes = "desPrivateNet"+peerName;
        	
            if (true) {
	            // write file "config.properties" that is read by other classes like PeerGroupFactory
	            String infrastructurePeerGroupIDAsString = infrastructurePeerGroupID.toURI().toString();
	            writeConfigProperties(jxtaHome, infrastructurePeerGroupIDAsString, infraStructurePeerGroupName, infraStructurePeerGroupDes);
	        	
        	}
            //LOG.info("JXTA_HOME="+jxtaHome.getCanonicalPath());
            Profile profile = Profile.DEFAULT; // File("./jxta_profile.xml").toURL());//"./superPeer.xml").toURL());//
            
            Configurator configurator =  new Configurator(jxtaHome.toURI(),profile);
            
            if (true) {
	            // change configurator 
	            configurator.setInfrastructurePeerGroupName(infraStructurePeerGroupName);
	            configurator.setInfrastructurePeerGroupDescription(infraStructurePeerGroupDes);
	            configurator.setInfrastructurePeerGroupId(infrastructurePeerGroupID);
	            
	            // change also PeerGroupFactory because the PeerGroupFactory might be initialized wrong by
	            // reading a wrong config.properties file or using the defalut values if config.proeprties file was not created yet
	            PeerGroupFactory.setNetPGName(infraStructurePeerGroupName);
	            PeerGroupFactory.setNetPGDesc(infraStructurePeerGroupDes);
	            PeerGroupFactory.setNetPGID(infrastructurePeerGroupID);
            }
            
            // set this peer
            configurator.setName(peerName); 
            configurator.setSecurity(id, password);
                        
            // new stuff
//            String rdvSeedingURI="";
//            String relaySeedingURI="";
//            configurator.addRendezVous(new URI("http://www.idega.is")); //new URI(rdvSeedingURI));
//            configurator.addRelay(new URI("http://www.idega.is")); //new URI(relaySeedingURI));
            //configurator.setMode(NetworkConfigurator.RDV_SERVER + NetworkConfigurator.RELAY_SERVER);

//            configurator.setUseOnlyRelaySeeds(true);
//            configurator.setUseOnlyRendezvousSeeds(true);
//
//            configurator.setTcpEnabled(true);
//            configurator.setTcpIncoming(true);
//            configurator.setTcpOutgoing(true);

            
            
//            configurator.clearRendezVous();
//            configurator.clearRelays();
//            
//             // get own ip address
//            InetAddress localHost = InetAddress.getLocalHost();
//            String address = localHost.getHostAddress();
            
//            try {
//            	  URL url = new URL("http://157.157.121.37:9150");
//            	  HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            	  con.setFollowRedirects(true);
//            	  con.setInstanceFollowRedirects(false);
//            	  con.connect();
//            	 
//            	  while (String.valueOf(con.getResponseCode()).startsWith("3")) {
//            	     String theLocation = con.getHeaderField("Location");
//            	     con.disconnect();
//            	     url = new URL(theLocation);
//            	     con = (HttpURLConnection) url.openConnection();
//            	     con.setFollowRedirects(true);
//            	     con.setInstanceFollowRedirects(false);
//            	     con.connect();
//            	     }
//            	     /** at this point you are located at the last(target)page of
//            	         redirection chain */
//            	 
//            	} catch (Exception ex) { ex.printStackTrace(); }

//            
            //URI uri = new URI("http",null,"192.168.76.128",9700,null,null, null);
            //URI uri = new URI("http",null,"157.157.121.37",9700,null,null, null);
            //configurator.addRendezVous(uri);//new URI("http", "157.157.121.37:9150", null, null));
//            configurator.addRelay(new URI("http",address, null, null));
            
            configurator.setRelaysDiscovery(true);
            configurator.setRendezVousDiscovery(true);
            
            configurator.setRendezVous(true);
            //configurator.setRendezVousAutoStart(3000);
            
            //testttesttest
//            configurator.setRelayIncoming(false);
//            configurator.setRelayOutgoing(false);

            // testtesttest



            
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
           // LOG.info(" getRelaysDiscovery:"+configurator.getRelaRelaysDiscovery());
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
                LOG.info("  Relay("+rdv+"):"+rdvs.get(rdv));
                
            }
            LOG.info("Optimizers");
            Iterator iter = configurator.getOptimizers();
            int optimizerCount = 0;
            while(iter.hasNext()){
                Object next = iter.next();
                LOG.info("  Optomizers("+ optimizerCount +"):"+next);
                optimizerCount++;
            }
            
            try{
                if (configurator.save()){
                    LOG.info(">>>>>>> successful save ");
                }else{
                    LOG.info("failed to save config");
                }
            }catch(Exception e){
                LOG.error("Problem saving configuration", e);
            }
            
            
            List list = configurator.getTransports();
            for (int i = 0;i < list.size();i++){
                LOG.info("Transports("+i+"):"+list.get(i).getClass().getName()+" "+list.get(i));
                if (list.get(i) instanceof net.jxta.ext.config.TcpTransport){
                    net.jxta.ext.config.TcpTransport data = (net.jxta.ext.config.TcpTransport)list.get(i);
                    TcpTransportAddress tcpAddress = (TcpTransportAddress) data.getAddresses().get(0);
                    MulticastAddress multicast = (MulticastAddress) tcpAddress.getMulticastAddresses().get(0);
                    // multicast default switched off - therefore switch it on
                    if (JxtaConfigSettings.USE_MULTICAST_TCP_TRANSPORT) {
                    	multicast.setMulticast(true);
                    }
                    
                    LOG.info("  isEnabled:"+data.isEnabled());
                    LOG.info("  isIncoming:"+data.isIncoming());
                    LOG.info("  isOutgoing:"+data.isOutgoing());
                    LOG.info("  isProxy:"+data.isProxy());
                    LOG.info("  getProxyAddress:"+data.getProxyAddress());
                    LOG.info("  getPublicAddresses:"+data.getPublicAddresses());
                    LOG.info("  getScheme:"+data.getScheme());
                    
                    List listAddr = data.getAddresses();
                    for (int a = 0;i < listAddr.size();i++){
                        LOG.info("  Addresses("+i+"):"+listAddr.get(i).getClass().getName()+" "+listAddr.get(i));
                    }
                }
                if (list.get(i) instanceof net.jxta.ext.config.HttpTransport){
                    net.jxta.ext.config.HttpTransport data = (net.jxta.ext.config.HttpTransport)list.get(i);
                    if (JxtaConfigSettings.SET_HTTP_TRANSPORT_PORT_80) {
	                    Address address = (Address) data.getAddresses().get(0);
	                    URI uri = address.getAddress();
	                    String scheme = uri.getScheme();
	                    String userInfo = uri.getUserInfo();
	                    String host = uri.getHost();
	                    int port = 80;
	                    String path = uri.getPath();
	                    String query = uri.getQuery();
	                    String fragment = uri.getFragment();
	                    URI newURI = new URI(scheme,userInfo,host,port, path, query,fragment);
	                    address.setAddress(newURI);
                    }
                    LOG.info("  isEnabled:"+data.isEnabled());
                    LOG.info("  isIncoming:"+data.isIncoming());
                    LOG.info("  isOutgoing:"+data.isOutgoing());
                    LOG.info("  isProxy:"+data.isProxy());
                    LOG.info("  getProxyAddress:"+data.getProxyAddress());
                    LOG.info("  getPublicAddresses:"+data.getPublicAddresses());
                    LOG.info("  getScheme:"+data.getScheme());
                    List listAddr = data.getAddresses();
                    for (int a = 0;i < listAddr.size();i++){
                        LOG.info("  Addresses("+i+"):"+listAddr.get(i).getClass().getName()+" "+listAddr.get(i));
                    }
                }
            }
            LOG.info("  getPlatformConfig:"+configurator.getPlatformConfig());

            try{
                if (configurator.save()){
                    LOG.info(">>>>>>> successful save ");
                }else{
                    LOG.info("failed to save config");
                }
            }catch(Exception e){
                LOG.error("Problem saving configuration", e);
            }
           


        }catch(Exception e ){
            LOG.error("Error initializing configuration",e);
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


