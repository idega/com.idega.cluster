/*
 * $Id: ReplyMessageStopper.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 2, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message.impl;

import java.util.Iterator;
import java.util.Map;
import com.idega.cluster.net.config.JxtaConfigSettings;
import com.idega.cluster.net.message.SendFilter;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.util.datastructures.map.TimeLimitedMap;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ReplyMessageStopper implements SendFilter {
	
	private Map forbiddenMessages = null;
	
	public ReplyMessageStopper() {
		forbiddenMessages = TimeLimitedMap.getInstanceWithTimeLimitInMinutes(JxtaConfigSettings.MESSAGE_STORE_TIME);
	}
	
	public void add(SimpleMessage simpleMessage) {
		forbiddenMessages.put(simpleMessage.getIdentifier(), simpleMessage);
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.SendFilter#isAccepted(com.idega.cluster.net.message.SimpleMessage)
	 */
	public boolean isAccepted(SimpleMessage simpleMessage) {
		String sameMessage = findSameMessage(simpleMessage);
		if (sameMessage == null) {
			return true;
		}
		forbiddenMessages.remove(sameMessage);
		return false;
	}
		
	private String findSameMessage(SimpleMessage simpleMessage) {
		Iterator iterator = forbiddenMessages.values().iterator();
		while (iterator.hasNext()) {
			SimpleMessage forbiddenMessage = (SimpleMessage) iterator.next();
			if (hasSameContent(forbiddenMessage, simpleMessage)) {
				return forbiddenMessage.getIdentifier();
			}
		}
		return null;
	}
	
	private boolean hasSameContent(SimpleMessage forbiddenMessage, SimpleMessage message) {
		String forbiddenSubject = forbiddenMessage.getSubject();
		String subject = message.getSubject();
		if (! hasSameValue(forbiddenSubject, subject)) {
			return false;
		}
		Iterator forbiddenKeys = forbiddenMessage.getKeys().iterator();
		while (forbiddenKeys.hasNext()) {
			String key = (String) forbiddenKeys.next();
			String forbiddenValue = forbiddenMessage.get(key);
			String value = message.get(key);
			if (! hasSameValue(forbiddenValue,value)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasSameValue(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return true;
		}
		if (value1 != null) {
			return value1.equals(value2);
		}
		// value1 is null but not value2 therefore false
		return false; 
	}
}
	
	
	
	
