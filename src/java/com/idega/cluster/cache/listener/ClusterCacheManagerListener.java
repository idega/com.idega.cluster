/*
 * $Id: ClusterCacheManagerListener.java,v 1.1 2007/01/12 15:42:35 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.CacheManagerEventListener;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.idegaweb.IWApplicationContext;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:35 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ClusterCacheManagerListener implements CacheManagerEventListener {
	
	public static ClusterCacheManagerListener getInstanceAddedToEhCache(ApplicationMessenger applicationMessenger, IWApplicationContext iwac) {
		// cache manager is a singleton
		CacheManager cacheManager = CacheManager.getInstance();
		// first add a listener
		ClusterCacheManagerListener clusterCacheManagerListener = null;
		CacheManagerEventListener cacheManagerEventListener = cacheManager.getCacheManagerEventListener();
		if (cacheManagerEventListener == null) {
			clusterCacheManagerListener = new ClusterCacheManagerListener(applicationMessenger, iwac);
			cacheManager.setCacheManagerEventListener(clusterCacheManagerListener);
		}
		else {
			// something wrong, why is there already a listener? Do not replace it!
			System.out.println("[ClusterCacheManagerListener] Could not add listener CacheManager, Listener already exist: " + 
					cacheManagerEventListener.toString());
			return null;
		}
		// check if there are already caches
		String[] cacheNames = cacheManager.getCacheNames();
		for (int i = 0; i < cacheNames.length; i++) {
			// not necessary to do it with threads but we do not want to write code twice
			clusterCacheManagerListener.notifyCacheAdded(cacheNames[i]);
		}
		return clusterCacheManagerListener;
	}

	private ApplicationMessenger applicationMessenger = null;
	private IWApplicationContext iwac;
	
	public ClusterCacheManagerListener(ApplicationMessenger applicationMessenger, IWApplicationContext iwac) {
		this.applicationMessenger = applicationMessenger;
		this.iwac = iwac;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheManagerEventListener#notifyCacheAdded(java.lang.String)
	 */
	public void notifyCacheAdded(String cacheName) {
		// start a thread, do not call the cache manager, prevent deadlock, see javadoc: 
        // "...the addCaches methods which cause this notification are synchronized on the CacheManager. 
		// An attempt to call CacheManager.getEhcache(String)  will cause a deadlock." 
		setListener(cacheName);
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheManagerEventListener#notifyCacheRemoved(java.lang.String)
	 */
	public void notifyCacheRemoved(String arg0) {
		// TODO Auto-generated method stub
	}
	
	private void setListener(String cacheName) {
		ClusterCacheMapListenerSetter clusterCacheMapListenerSetter = new ClusterCacheMapListenerSetter(cacheName,applicationMessenger,iwac);
		Thread setterThread = new Thread(clusterCacheMapListenerSetter);
		// set as daemon, if server chrashes.....
		setterThread.setDaemon(true);
		// go!
		setterThread.start();
	}
}
