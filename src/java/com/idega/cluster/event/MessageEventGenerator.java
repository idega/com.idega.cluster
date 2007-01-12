/*
 * $Id: MessageEventGenerator.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.event;

import com.idega.cluster.net.message.MessageListener;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventGenerator;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class MessageEventGenerator implements MessageListener, MethodCallEventGenerator {

	
	private MethodCallEventDispatcher myDispatcher = null;
	
	public MessageEventGenerator(MethodCallEventDispatcher dispatcher) {
		myDispatcher = dispatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.MessageListener#gotMessage(com.idega.cluster.net.message.SimpleMessage)
	 */
	public void gotMessage(SimpleMessage simpleMessage) {
		myDispatcher.fireEvent(simpleMessage);
	}
}
