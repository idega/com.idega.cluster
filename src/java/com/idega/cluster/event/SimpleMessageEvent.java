/*
 * $Id: SimpleMessageEvent.java,v 1.2 2007/05/05 14:36:08 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.event;

import java.util.Set;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.cluster.net.message.impl.SimpleMessageAdapter;
import com.idega.core.event.MethodCallEvent;


/**
 * 
 *  Last modified: $Date: 2007/05/05 14:36:08 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class SimpleMessageEvent extends SimpleMessageAdapter implements SimpleMessage {
	
	private MethodCallEvent methodCallEvent = null;
	private String messageSender = null;
	
	public SimpleMessageEvent(String messageSender, MethodCallEvent methodCallEvent) {
		this.methodCallEvent = methodCallEvent;
		this.messageSender = messageSender;
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#get(java.lang.String)
	 */
	public String get(String name) {
		return methodCallEvent.get(name);
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#getIdentifier()
	 */
	public String getIdentifier() {
		return methodCallEvent.getIdentifier();
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#getKeys()
	 */
	public Set getKeys() {
		return methodCallEvent.getKeys();
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#getSender()
	 */
	public String getSender() {
		return messageSender;
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#getSubject()
	 */
	public String getSubject() {
		return methodCallEvent.getSubject();
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.impl.SimpleMessageAdapter#put(java.lang.String, java.lang.String)
	 */
	public void put(String name, String value) {
		methodCallEvent.put(name, value);
	}
}
