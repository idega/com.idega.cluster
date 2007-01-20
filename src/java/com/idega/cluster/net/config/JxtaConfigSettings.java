/*
 * $Id: JxtaConfigSettings.java,v 1.2 2007/01/20 21:55:17 thomas Exp $
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
 *  Last modified: $Date: 2007/01/20 21:55:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class JxtaConfigSettings {

	// some variables to change set up
	
	public static final boolean USE_MULTICAST_TCP_TRANSPORT = false;
	public static final boolean USE_RENDEZVOUS_SERVICE = true;
	
	// set this when NAT or firewalls are in the way, default value is port 9700
	public static final boolean SET_HTTP_TRANSPORT_PORT_80 = true;
	
	// in milliseconds (only used if rendezvous is enabled) 
	public static final long WAIT_FOR_RENDEZVOUS = 2000;
	// not really important, in minutes
	public static final long MESSAGE_STORE_TIME = 30;
}
