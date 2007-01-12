/*
 * $Id: ApplicationMessenger.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 28, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message;

import com.idega.idegaweb.IWApplicationContext;



public interface ApplicationMessenger {
	
	boolean start(IWApplicationContext iwac);
	
	boolean stop(IWApplicationContext iwac);
	
	String getSender();
	
	void sendMessage(SimpleMessage simpleMessage);
	
	void addReceiveListener(MessageListener messageListener);
	
	void addSendFilter(SendFilter sendfilter);
	
	void addReceiveFilter(ReceiveFilter receiveFilter);
}
