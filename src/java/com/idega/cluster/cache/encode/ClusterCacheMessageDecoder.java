/*
 * $Id: ClusterCacheMessageDecoder.java,v 1.2 2007/01/20 21:53:42 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.encode;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import com.idega.cluster.net.message.SimpleMessage;


/**
 * 
 *  Last modified: $Date: 2007/01/20 21:53:42 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ClusterCacheMessageDecoder {
	
	private CacheManager cacheManager = null;
	
	public ClusterCacheMessageDecoder() {
		initialize();
	}

	private void initialize() {
		cacheManager = CacheManager.getInstance();
	}
	
	public Runnable decode(SimpleMessage simpleMessage) {
		// is the message meant to us?
		String namespace = simpleMessage.getSubject();
		if (! ClusterCacheMessageEncoder.MESSAGE_NAMESPACE_EHCACHE.equals(namespace)) {
			return null;
		}
		// does the cache exist?
		String cacheName = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_CACHE_NAME);
		if (cacheName == null || (! cacheManager.cacheExists(cacheName))) {
			return null;
		}
		

		String methodCall = simpleMessage.get(ClusterCacheMessageEncoder.MESSAGE_METHOD_CALL);
		if (ClusterCacheMessageEncoder.MESSAGE_REMOVED.equals(methodCall)) {
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
			final Cache myCache = cacheManager.getCache(cacheName);
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
