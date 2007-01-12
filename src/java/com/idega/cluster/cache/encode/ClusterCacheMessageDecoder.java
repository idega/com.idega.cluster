/*
 * $Id: ClusterCacheMessageDecoder.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.encode;

import java.util.Map;
import net.sf.ehcache.CacheManager;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ClusterCacheMessageDecoder {
	
	private IWCacheManager2 iwCacheManager2 = null;
	private CacheManager cacheManager = null;
	
	public ClusterCacheMessageDecoder(IWApplicationContext iwac) {
		initialize(iwac);
	}

	private void initialize(IWApplicationContext iwac) {
		IWMainApplication mainApplication = iwac.getIWMainApplication();
		iwCacheManager2 = IWCacheManager2.getInstance(mainApplication);
		cacheManager = CacheManager.getInstance();
	}
	
	public Runnable decode(SimpleMessage simpleMessage) {
		// is the message meant to us?
		String namespace = simpleMessage.getSubject();
		if (! ClusterCacheMessageEncoder.MESSAGE_NAMESPACE.equals(namespace)) {
			return null;
		}
		// does the cache exist?
		String cacheName = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_CACHE_NAME);
		if (cacheName == null || (! cacheManager.cacheExists(cacheName))) {
			return null;
		}
		final Map myCache = iwCacheManager2.getCache(cacheName);
		String methodCall = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_METHOD_CALL);
		if (ClusterCacheMessageEncoder.MESSAGE_CLEARED.equals(methodCall)) {
			return new Runnable() {
				public void run() {
					myCache.clear();
				}
			};
		}
		else if (ClusterCacheMessageEncoder.MESSAGE_REMOVED.equals(methodCall)) {
			String value = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_PARAMETER_VALUE);
			if (value == null) {
				return null;
			}
			String type = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_PARAMETER_TYPE);
			Object key = null;
			if (ClusterCacheMessageEncoder.MESSAGE_INTEGER.equals(type)) {
				key = Integer.valueOf(value);
			}
			else if (ClusterCacheMessageEncoder.MESSAGE_STRING.equals(type)){
				key = value;
			}
			else {
				return null;
			}
			// set final variable
			final Object finalKey = key;
			key = null;
			return new Runnable() {
				public void run() {
					myCache.remove(finalKey);
				}
			};
		}
		return null;
	}

}
