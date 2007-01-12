/*
 * $Id: SimpleMessageAdapter.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.pipe.OutputPipe;
import com.idega.cluster.net.message.SimpleMessage;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public abstract class SimpleMessageAdapter implements SimpleMessage {
	
	protected static final String HEADER ="simpleMessageHeader";
	protected static final String BODY = "simpleMessageBody";
	
	protected static final String SENDER = "simpleMessageSender";
	protected static final String SUBJECT = "simpleMessageSubject";
	protected static final String MESSAGE_ID = "simpleMessageID";

	// !! this is done to mark null values - please study methods carefully before changing any of the values !!
	private static final String NULL_VALUE_PREFIX = "*";
	private static final String NULL_VALUE = NULL_VALUE_PREFIX + "null";
	


	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.SimpleMessage#send(net.jxta.pipe.OutputPipe)
	 */
	public void send(OutputPipe outputPipe) throws IOException {
		// lazy, create message only when needed
		// (some messages are never send because of filters)
		Message	message = new Message();
		put(message, HEADER, MESSAGE_ID, getIdentifier());
		put(message, HEADER, SENDER, getSender());
		put(message, HEADER, SUBJECT, getSubject());
		Iterator iterator = getKeys().iterator();
		while(iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = get(key);
			put(message, BODY, key, value);
		}
		outputPipe.send(message);
	}

	protected String get(MessageElement messageElement) {
        String messageValue = (messageElement == null) ? null : messageElement.toString();
        return decodeStringOrNull(messageValue);
    }
	
	
	protected void put(Message message, String namespace, String name, String value) {
		// enabling null values
		String messageValue = encodeStringOrNull(value);
        StringMessageElement messageElementSender = new StringMessageElement(name,messageValue,null);
        message.addMessageElement(namespace, messageElementSender);
	}
	
	private String encodeStringOrNull(String value) {
		if (value == null) {
			// null value is "null" with prefix "*"
			// return "*null"
			return NULL_VALUE;
		}
		if (value.startsWith(NULL_VALUE_PREFIX)) {
			// if a string starts with "*" add "*"
			// (e.g. "*null" -> "**null", "**42" --> "***42")
			StringBuffer buffer = new StringBuffer(NULL_VALUE_PREFIX);
			buffer.append(value);
			return buffer.toString();
		}
		return value;	
	}
	
	private String decodeStringOrNull(String value) {
		if (value != null && value.startsWith(NULL_VALUE_PREFIX)) {
			// does it represent null? ( is equal to "*null")
			if (NULL_VALUE.equals(value)) {
				return null;
			}
			// return value with removed prefix 
			return value.substring(1);
		}
		// return unchanged value
		return value;	
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#get(java.lang.String)
	 */
	abstract public String get(String name);


	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getIdentifier()
	 */
	abstract public String getIdentifier();
	

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getKeys()
	 */
	abstract public Set getKeys();

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getSender()
	 */
	abstract public String getSender();

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getSubject()
	 */
	abstract public String getSubject();


	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#put(java.lang.String, java.lang.String)
	 */
	abstract public void put(String name, String value);
}
