/*
 * $Id: JxtaConfig.java,v 1.1 2006/12/11 15:48:29 thomas Exp $
 * Created on Nov 30, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster;


	

/*
 * JxtaConfig.java
 *
 * Created on June 5, 2004, 3:26 PM
 */

import java.io.*;
import java.net.URI;
import java.util.*;

import net.jxta.exception.ConfiguratorException;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.HttpTransport;
import net.jxta.ext.config.TcpTransport;


import net.jxta.ext.config.Profile;
/**
 *
 * @author  danielbrookshier
 */
public class JxtaConfig{
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JxtaConfig.class.getName());
    private static final String jxtaCoreFile = "jxta_core.properties";
    
    protected Configurator configurator;
    protected String peerName;
    protected String description;
    public static void main(String args[]){
        try{
            //File home = new File("/home/thomas/workspaces/targets/targetA3/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
            File home = new File("/home/thomas/workspaces/workspace_ePlatform_rvk_20061127/applications/reykjavik/target/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
            LOG.setLevel(org.apache.log4j.Level.INFO);
            JxtaConfig jxtaConfig = new JxtaConfig("defaul@default.com","id","password","desc",home);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void writeConfig(String email, String id, String password){
        new JxtaConfig( email,  id,  password);
    }
   private JxtaConfig(String email, String id, String password){
        this(email,  id,  password, "default description",new File("."));
        LOG.setLevel(org.apache.log4j.Level.INFO);
        
    }
    
    public JxtaConfig(String email, String id, String password, String description, File jxtaHome) {
        LOG.setLevel(org.apache.log4j.Level.INFO);
        setJxtaHome(jxtaHome);
        
        Properties defaultProps = new Properties();
        try{
            FileInputStream in = new FileInputStream(jxtaCoreFile);
            defaultProps.load(in);
            in.close();
        }catch(Exception e){
            LOG.info("There was a problem loading the file:"+jxtaCoreFile+ ". Using defaults");
        }

        String infraBase = defaultProps.getProperty("net.cluck.infraBase","DEFAULT-BASE").trim();
        String infraSeed = defaultProps.getProperty("net.cluck.infraSeed","DEFAULT-SEED").trim();
        String infraName = defaultProps.getProperty("net.cluck.infraName","DEFAULT-NAME").trim();
        String infraDesc = defaultProps.getProperty("net.cluck.infraDesc", "Default Description - You probably need to create a profile for the private infrastructure peer group").trim();
        
        LOG.info("property net.cluck.infraBase="+infraBase);
        LOG.info("property net.cluck.infraSeed="+infraSeed);
        LOG.info("property net.cluck.infraName="+infraName);
        LOG.info("property net.cluck.infraDesc="+infraDesc);
        
        createPrivateGroup(infraBase,infraSeed,infraName,infraDesc);
        
        try{
            this.peerName = email;
            this.description = description;
            
            // Need to set this environment variable before using Configurator,
            // otherwise the Configurator throws a null pointer exception.
            System.setProperty("JXTA_HOME", jxtaHome.getCanonicalPath()); 
            
            LOG.info("JXTA_HOME="+jxtaHome.getCanonicalPath());
            Profile profile = Profile.DEFAULT; // File("./jxta_profile.xml").toURL());//"./superPeer.xml").toURL());//
            
            configurator =  new Configurator(jxtaHome.toURI(),profile);
            configurator.setName("gespenst"); //by thi
            configurator.setSecurity(id, password);
            configurator.setRelaysDiscovery(false);
            configurator.setRendezVousDiscovery(false);
            LOG.info(">>>>>> save the config");
            try{
                if (configurator.save()){
                    LOG.info(">>>>>>> successful save ");
                }else{
                    LOG.info("failed to save config");
                }
            }catch(Exception e){
                LOG.error("Problem saving configuration", e);
            }
            
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
            
            List list = configurator.getTransports();
            for (int i = 0;i < list.size();i++){
                LOG.info("Transports("+i+"):"+list.get(i).getClass().getName()+" "+list.get(i));
                if (list.get(i) instanceof net.jxta.ext.config.TcpTransport){
                    net.jxta.ext.config.TcpTransport data = (net.jxta.ext.config.TcpTransport)list.get(i);
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
            
            
            configurator= null;
        }catch(Exception e ){
            LOG.error("Error initializing configuration",e);
        }
    }
    public void createPrivateGroup(String infraBase, String infraSeed, String myNameForNetGroup,String mydesc){
        getJxtaHome().mkdirs();
        File file = new File(getJxtaHome(),  "config.properties");
        if (! file.exists()) {
        	try {
        		file.createNewFile();
        	}
        	catch (Exception ex) {
        		System.out.println(ex);
        	}
        }
        
        
        
        net.jxta.peergroup.PeerGroupID pgID =  MD5ID.createInftrastructurePeerGroupID(infraBase,infraSeed);
        String id = pgID.toString();
        //id = id.substring(id.indexOf("jxta:")+5); // by thi
        LOG.info("NetPeerGroupID="+id);
        try{
            FileWriter writer = new FileWriter(file);
            writer.write("NetPeerGroupID="+id+"\n");
            writer.write("NetPeerGroupName="+myNameForNetGroup+"\n");
            writer.write("NetPeerGroupDesc="+mydesc+"\n");
            writer.close();
        }catch(IOException ioe){
            LOG.error("Unable to create config.properties",ioe);
        }
    }
    /**
     * Delete Jxta home folder
     * @return   True if the home folder was completely removed.
     */
//    public boolean deleteConfig() {
//        LOG.warn("Deleting the configuration?????");
//        return FileUtil.deleteDirTree(getJxtaHome());
//    }
//    boolean configExists(){
//        return new File(configurator.getHome(),"PlatformConfig").exists();
//    }
    private File jxtaHome;
    public File getJxtaHome(){
        return jxtaHome;
    }
    
    public void setJxtaHome(File jxtaHome) {
        this.jxtaHome = jxtaHome;
    }
}


