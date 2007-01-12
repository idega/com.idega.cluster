/*
 * $Id: MessageEventHandler.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.event;

import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventHandler;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class MessageEventHandler implements MethodCallEventHandler {
	
	private ApplicationMessenger applicationMessenger = null;
	
	public MessageEventHandler(ApplicationMessenger applicationMessenger) {
		this.applicationMessenger = applicationMessenger; 
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventHandler#handleEvent(com.idega.core.event.MethodCallEvent)
	 */
	public void handleEvent(MethodCallEvent methodCallEvent) {
		// put a wrapper around the event
		SimpleMessage simpleMessage = new SimpleMessageEvent(methodCallEvent);
		applicationMessenger.sendMessage(simpleMessage);
	}
}
