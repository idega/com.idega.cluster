/*
 * $Id: SimpleMessageImpl.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Jan 2, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.core.idgenerator.business.UUIDGenerator;


public class SimpleMessageImpl extends SimpleMessageAdapter implements SimpleMessage {

	public static SimpleMessageImpl getInstanceByReceivedMessage(Message message) {
		return new SimpleMessageImpl(message);
	}
	
	public static SimpleMessage getInstanceForSending(String sender, String subject) {
		return new SimpleMessageImpl(sender, subject);
	}
	
	
	private String identifier = null;
	private String sender = null;
	private String subject = null;
	
	private Map body = null;
	
	private SimpleMessageImpl(String sender, String subject) {
		this.identifier = UUIDGenerator.getInstance().generateUUID();
		this.sender = sender;
		this.subject = subject;
	}
	
	private SimpleMessageImpl(Message message) {
		initialize(message);
	}
	
	private void initialize(Message message) {
		// faster to fetch all elements of a namespace
		Iterator iteratorHeader = message.getMessageElementsOfNamespace(SimpleMessageAdapter.HEADER);
		while (iteratorHeader.hasNext()) {
			MessageElement element = (MessageElement) iteratorHeader.next();
			String name = element.getElementName();
			String value = get(element);
			if (SimpleMessageAdapter.MESSAGE_ID.equals(name)) {
				identifier = value;
			}
			else if (SimpleMessageAdapter.SENDER.equals(name)) {
				sender = value;
			}
			else if (SimpleMessageAdapter.SUBJECT.equals(name)) {
				subject = value;
			}
		}
		Iterator iteratorBody = message.getMessageElementsOfNamespace(SimpleMessageAdapter.BODY);
		while (iteratorBody.hasNext()) {
			MessageElement element = (MessageElement) iteratorBody.next();
			String name = element.getElementName();
			String value = get(element);
			getBody().put(name, value);				
		}
	}
	
	public void put(String name, String value) {
		getBody().put(name, value);
	}
	
	public String get(String name) {
		return (String) getBody().get(name);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public Set getKeys() {
		return getBody().keySet();
	}
	
	public String toString() {
		return "Sender: "+ sender + " Subject: " + " ID: " + identifier;
	}
	


	
	protected Map getBody() {
		if (body == null) {
			body = new HashMap();
		}
		return body;
	}
}
