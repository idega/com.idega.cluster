/*
 * $Id: JxtaConfigSettings.java,v 1.4 2007/05/03 16:56:49 thomas Exp $
 * Created on Jan 5, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.config;


/**
 * 
 *  Last modified: $Date: 2007/05/03 16:56:49 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public class JxtaConfigSettings {
	
	//how to configure the JXTA client
	
	// step 1
	// if true the system looks up an existing one in jxta home else go to step 2
	public static final boolean USE_EXISTING_PLATFORM_CONFIG = false;
	
	// step 2
	// if true the system cleans jxta home and calls the config wizard (GUI appliction) else go to step 3
	public static final boolean CALL_CONFIG_APPLICATION = false;
	
	// step 3 
	// configure automatically

	
	// some variables to change set up
	public static final boolean USE_OWN_GROUP = false;	
	public static final boolean USE_MULTICAST_TCP_TRANSPORT = false;
	public static final boolean USE_RENDEZVOUS_SERVICE = true;
	
	// in milliseconds (only used if rendezvous is enabled) 
	public static final long WAIT_FOR_RENDEZVOUS = 2000;
	
	public static final int NUMBER_WORKERS = 40;
	// not really important, in minutes
	public static final long MESSAGE_STORE_TIME = 30;
}
