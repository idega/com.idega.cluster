/*
 * $Id: ClusterCacheMessageEncoder.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.encode;

import com.idega.cluster.net.message.SimpleMessage;
import com.idega.cluster.net.message.impl.SimpleMessageImpl;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ClusterCacheMessageEncoder {
	
	// values
	public final static String MESSAGE_NAMESPACE = "iwCacheManager2";
	public final static String MESSAGE_CLEARED = "cleared";
	public final static String MESSAGE_REMOVED = "removed";
	public final static String MESSAGE_STRING = "string";
	public final static String MESSAGE_INTEGER = "integer";
	
	// keys
	public final static String MESSAGE_CACHE_NAME = "cacheName";
	public final static String MESSAGE_METHOD_CALL = "methodCall";	
	public final static String MESSAGE_PARAMETER_TYPE = "parameter";
	public final static String MESSAGE_PARAMETER_VALUE = "value";

	
	private String sender = null;
	private String cacheName = null;
	
	public ClusterCacheMessageEncoder(String sender, String cacheName) {
		this.sender = sender;
		this.cacheName = cacheName;
	}
	
	
	public SimpleMessage cleared() {
		SimpleMessage simpleMessage = SimpleMessageImpl.getInstanceForSending(sender, MESSAGE_NAMESPACE);
		simpleMessage.put(MESSAGE_CACHE_NAME, cacheName);
		simpleMessage.put(MESSAGE_METHOD_CALL, MESSAGE_CLEARED);
		return simpleMessage;
	}
	
	public SimpleMessage removedObject(Object key) {
		// keep it simple: key is either string or integer
		// much better performance not to use streams in JXTA
		SimpleMessage simpleMessage = SimpleMessageImpl.getInstanceForSending(sender, MESSAGE_NAMESPACE);
		simpleMessage.put(MESSAGE_CACHE_NAME, cacheName);
		simpleMessage.put(MESSAGE_METHOD_CALL, MESSAGE_REMOVED);
		if (key instanceof Integer) {
			simpleMessage.put(MESSAGE_PARAMETER_TYPE, MESSAGE_INTEGER);
		}
		else {
			simpleMessage.put(MESSAGE_PARAMETER_TYPE, MESSAGE_STRING);
		}
		simpleMessage.put(MESSAGE_PARAMETER_VALUE, key.toString());
		return simpleMessage;
	}
	
	
}
