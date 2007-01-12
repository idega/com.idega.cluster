/*
 * $Id: IgnoreOwnMessage.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 3, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message.impl;

import com.idega.cluster.net.message.ReceiveFilter;
import com.idega.cluster.net.message.SimpleMessage;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class IgnoreOwnMessage implements ReceiveFilter {
	
	String sender = null;
	
	public IgnoreOwnMessage(String sender) {
		this.sender = sender;
	}

	public boolean isAccepted(SimpleMessage simpleMessage) {
		String messageSender = simpleMessage.getSender();
		if (messageSender == null) {
			// do not accept messages without a sender
			return false;
		}
		// do not accept own messages
        return (! messageSender.equals(sender));
	}
}
