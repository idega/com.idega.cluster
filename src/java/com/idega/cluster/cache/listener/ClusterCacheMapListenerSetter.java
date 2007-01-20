/*
 * $Id: ClusterCacheMapListenerSetter.java,v 1.2 2007/01/20 21:53:44 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.RegisteredEventListeners;
import com.idega.cluster.cache.config.ClusterCacheSettings;
import com.idega.cluster.net.message.ApplicationMessenger;


/**
 * 
 *  Last modified: $Date: 2007/01/20 21:53:44 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ClusterCacheMapListenerSetter implements Runnable {
	
	private String cacheName = null;
	private ApplicationMessenger applicationMessenger = null;
	
	public ClusterCacheMapListenerSetter(String cacheName, ApplicationMessenger applicationMessenger) {
		this.cacheName = cacheName;
		this.applicationMessenger = applicationMessenger;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// get ehcache cache manager
		CacheManager cacheManager = CacheManager.getInstance();
		long start = System.currentTimeMillis();
		// wait for creation of the cache!!!!!!!!!!!
		while (!cacheManager.cacheExists(cacheName)) {
			long currentTime = System.currentTimeMillis();
			long difference = currentTime - start;
			// wait not forever
			if (difference > ClusterCacheSettings.WAITING_TIME_PERIOD_CACHE_CREATION) {
				return;
			}
		}
		// cache was created
		Cache cache = cacheManager.getCache(cacheName);
		// needed for adding a listener
		RegisteredEventListeners registeredEventListeners = cache.getCacheEventNotificationService();
		// creating a listener for this cache map
		ClusterCacheEventListener clusterCacheMapListener = new ClusterCacheEventListener(cacheName,applicationMessenger);
		// adding this listener to the cache
		registeredEventListeners.registerListener(clusterCacheMapListener);
	}
}
