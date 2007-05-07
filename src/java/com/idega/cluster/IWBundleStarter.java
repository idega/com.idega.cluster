/*
 * $Id: IWBundleStarter.java,v 1.6 2007/05/07 14:06:42 thomas Exp $
 * Created on 3.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster;

import java.io.File;
import net.jxta.access.AccessService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.idega.cluster.cache.listener.ClusterCacheManagerListener;
import com.idega.cluster.cache.listener.ClusterCacheMapNotifier;
import com.idega.cluster.event.MethodCallEventConnector;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.config.JxtaPlatformConfigurator;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.cluster.net.message.ReceiveFilter;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.cluster.net.message.impl.IgnoreAlreadyReceivedMessage;
import com.idega.cluster.net.message.impl.IgnoreOwnMessage;
import com.idega.cluster.net.message.impl.SimpleMessageImpl;
import com.idega.cluster.net.pipe.ApplicationPeerGroupPipe;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;




/**
 * 
 *  Last modified: $Date: 2007/05/07 14:06:42 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.6 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	private ApplicationMessenger applicationMessenger = null;

	public void start(IWBundle starterBundle) {
		String levelAsString = starterBundle.getApplication().getSettings().getProperty( JxtaConfigSettings.COM_IDEGA_CLUSTER_LOG4J_LEVEL, Level.ERROR.toString());
		Level level = Level.toLevel(levelAsString);
		// set com.idega.cluster to error level
	    Logger log = Logger.getLogger(IWBundleStarter.class.getPackage().getName());
	    log.setLevel(level);
	    // set net.jxta to error level
	    Logger logJxta = Logger.getLogger("net.jxta");
	    logJxta.setLevel(level);
		if (true) {
			// for production
			startPrivateNet(starterBundle);
		}
		
		else if (false) {
			// for testing 
			startPipeListener(starterBundle);
		}
		
		else if (true) {
			// for testing
			startPipeExample(starterBundle);
		}
	}
	
	public void startPipeExample(IWBundle starterBundle) {
		IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
		File jxtaHome = JxtaPlatformConfigurator.defineJxtaHome(iwac.getIWMainApplication());
		//JxtaPlatformConfigurator.prepareJxtaHome(jxtaHome);
		PipeExample.main(null);
	}
	
	public void startPipeListener(IWBundle starterBundle) {
		IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
		File jxtaHome = JxtaPlatformConfigurator.defineJxtaHome(iwac.getIWMainApplication());
		//JxtaPlatformConfigurator.prepareJxtaHome(jxtaHome);
		PipeListener.main(null);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void startPrivateNet(IWBundle starterBundle) {
		IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
		try {
			applicationMessenger = new ApplicationPeerGroupPipe();
			if (! applicationMessenger.start(iwac)) {
				applicationMessenger = null;
				System.err.println("[IWBundleStarter] Could not start an application messenger");
				return;
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// testing
		String sender = applicationMessenger.getSender();
		SimpleMessage messageToSend = SimpleMessageImpl.getInstanceForSending(sender,"test");
		applicationMessenger.sendMessage(messageToSend);
		
		// registering filters for receiving
		ReceiveFilter receiveFilter = new IgnoreOwnMessage(sender);
		applicationMessenger.addReceiveFilter(receiveFilter);
		applicationMessenger.addReceiveFilter(new IgnoreAlreadyReceivedMessage());
		
		// registering listeners that are sending messages
		ClusterCacheManagerListener.getInstanceAddedToEhCache(applicationMessenger);
		
		// registering listeners that are getting messages
		ClusterCacheMapNotifier.getInstanceAddedToEhCache(applicationMessenger);
		
		// registering to event system
		MethodCallEventConnector connector = new MethodCallEventConnector();
		connector.connectToMethodCallEventSystem(applicationMessenger);

		
		// 
		//File home = new File("/Users/thomas/workspaces/workspace_ePlatform_rvk_20061127/applications/reykjavik/target/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
		
		//File home = new File("/Users/thomas/workspaces/targets/tomcat2/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties/");
//		try {
//			System.out.println("Home of JXTA: " + home.getCanonicalPath());
//		}
//		catch (IOException ex) {
//		}
		
		
		//File home = new File("/home/thomas/workspaces/targets/targetA3/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
//        try {
//
//        	JxtaConfig.main(null);
//        	/*
//        	 * Register the default Edge platform configurator
//        	 */
//        	//AbstractConfigurator.register(ApplicationJxtaNetConfigurator.class);
//        	//PeerGroupFactory.setConfiguratorClass(ApplicationJxtaNetConfigurator.class);
//        	IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
//            PeerGroup defaultPeerGroup = PeerGroupFactory.newNetPeerGroup();
//            DiscoveryService discovery = defaultPeerGroup.getDiscoveryService();
////          this step helps when running standalone (local sub-net without any redezvous setup)
//            discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null, 1, null);
//            ApplicationPeerGroup applicationPeerGroup = new ApplicationPeerGroup(defaultPeerGroup, iwac);
//            applicationPeerGroup.toString();
//            ApplicationPeerGroupPipe applicationPeerGroupPipe = new ApplicationPeerGroupPipe(applicationPeerGroup, iwac);
//            SimpleMessage messageToSend = new SimpleMessage("cache","hello world");
//            applicationPeerGroupPipe.sendMessage(messageToSend);
//        }
//        catch (Exception ex) {
//        	System.out.println("Hello");
//        }
//    	//JxtaConfig.main(null);
		//SimpleJxtaApp.main(null);
    	//DiscoveryDemo.main(null);
        //PipeListener.main(null);
        //PipeExample.main(null);

        
        //JoinDemo.main(null);
		
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		if (applicationMessenger != null) {
			IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
			applicationMessenger.stop(iwac);
		}
		applicationMessenger = null;
		// TODO Auto-generated method stub
	}
}
