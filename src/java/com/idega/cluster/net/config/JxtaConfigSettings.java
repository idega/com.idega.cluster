/*
 * $Id: JxtaConfigSettings.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
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
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class JxtaConfigSettings {

	// some variables to change set up
	
	public static final boolean USE_MULTICAST_TCP_TRANSPORT = true;
	public static final boolean USE_RENDEZVOUS_SERVICE = true;
	
	// in milliseconds (only if rendezvous is enabled) 
	public static final long WAIT_FOR_RENDEZVOUS = 2000;
	// in minutes
	public static final long MESSAGE_STORE_TIME = 30;
}
