/*
 * $Id: IgnoreAlreadyReceivedMessage.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 8, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message.impl;

import java.util.Map;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.message.ReceiveFilter;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.util.datastructures.map.TimeLimitedMap;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class IgnoreAlreadyReceivedMessage implements ReceiveFilter {
	
	
	private Map receivedMessages = null;

	public IgnoreAlreadyReceivedMessage() {
		receivedMessages = TimeLimitedMap.getInstanceWithTimeLimitInMinutes(JxtaConfigSettings.MESSAGE_STORE_TIME);
	}
	
	public void add(SimpleMessage simpleMessage) {
		receivedMessages.put(simpleMessage.getIdentifier(), null);
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.SendFilter#isAccepted(com.idega.cluster.net.message.SimpleMessage)
	 */
	public boolean isAccepted(SimpleMessage simpleMessage) {
		String identifier = simpleMessage.getIdentifier();
		if (receivedMessages.containsKey(identifier)) {
			return false;
		}
		receivedMessages.put(identifier, null);
		return true;
	}

}
