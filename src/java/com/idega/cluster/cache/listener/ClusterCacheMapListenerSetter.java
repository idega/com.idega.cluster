/*
 * $Id: ClusterCacheMapListenerSetter.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import net.sf.ehcache.CacheManager;
import com.idega.cluster.cache.config.ClusterCacheSettings;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.core.cache.CacheMap;
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
public class ClusterCacheMapListenerSetter implements Runnable {
	
	private String cacheName = null;
	private ApplicationMessenger applicationMessenger = null;
	private IWApplicationContext iwac = null;
	
	public ClusterCacheMapListenerSetter(String cacheName, ApplicationMessenger applicationMessenger, IWApplicationContext iwac) {
		this.cacheName = cacheName;
		this.applicationMessenger = applicationMessenger;
		this.iwac = iwac;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// get ehcache cache manager
		CacheManager cacheManager = CacheManager.getInstance();
		long start = System.currentTimeMillis();
		// wait for creation of the cache
		while (!cacheManager.cacheExists(cacheName)) {
			long currentTime = System.currentTimeMillis();
			long difference = currentTime - start;
			// wait not forever
			if (difference > ClusterCacheSettings.WAITING_TIME_PERIOD_CACHE_CREATION) {
				return;
			}
		}
		// cache was created
		// get idega cache manager
		IWMainApplication mainApplication = iwac.getIWMainApplication();
		IWCacheManager2 iwCacheManager2 = IWCacheManager2.getInstance(mainApplication);
		// get cache map
		CacheMap cacheMap = (CacheMap) iwCacheManager2.getCache(cacheName);
		// creating a listener for this cache map
		ClusterCacheMapListener clusterCacheMapListener = new ClusterCacheMapListener(cacheName,applicationMessenger);
		// adding this listener to this map
		cacheMap.addCacheListener(clusterCacheMapListener);
	}
}
