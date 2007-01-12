/*
 * $Id: MethodCallEventConnector.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.event;

import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.cluster.net.message.MessageListener;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventHandler;
import com.idega.core.event.impl.InOutEventDispatcher;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class MethodCallEventConnector {
	
	public synchronized  void connectToMethodCallEventSystem(ApplicationMessenger applicationMessenger) {
		// get the event dispatcher
		InOutEventDispatcher inOutEventDispatcher = InOutEventDispatcher.getInstance();
		// start receiving messages
		startReceivingMessageAndFiringEvents(applicationMessenger, inOutEventDispatcher);
		// start listening to events
		startListeningToEventsAndSendingMessages(applicationMessenger, inOutEventDispatcher);
	}
		
		
	private void startReceivingMessageAndFiringEvents(ApplicationMessenger applicationMessenger, InOutEventDispatcher inOutEventDispatcher) {
		// target for events
		MethodCallEventDispatcher inDispatcher = inOutEventDispatcher.getInEventDispatcher();
		// create event generator
		MessageListener messageListener = new MessageEventGenerator(inDispatcher);
		// register this listener to the application messenger
		applicationMessenger.addReceiveListener(messageListener);
	}

	private void startListeningToEventsAndSendingMessages(ApplicationMessenger applicationMessenger, InOutEventDispatcher inOutEventDispatcher) {
		// source for events
		MethodCallEventDispatcher outDispatcher = inOutEventDispatcher.getOutEventDispatcher();
		// create listener for events
		MethodCallEventHandler methodCallEventHandler = new MessageEventHandler(applicationMessenger);
		// register this listener to the event dispatcher
		outDispatcher.addListener(methodCallEventHandler);
	}
}
