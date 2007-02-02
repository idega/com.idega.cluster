/*
 * $Id: ClusterCacheEventListener.java,v 1.3 2007/02/02 01:45:57 thomas Exp $
 * Created on Jan 19, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import com.idega.cluster.cache.encode.ClusterCacheMessageEncoder;
import com.idega.cluster.net.message.ApplicationMessenger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;


/**
 * 
 *  Last modified: $Date: 2007/02/02 01:45:57 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class ClusterCacheEventListener implements CacheEventListener {
	
	private ApplicationMessenger applicationMessenger = null;
	private ClusterCacheMessageEncoder clusterCacheMessageEncoder = null;

	
	public ClusterCacheEventListener(String cacheName, ApplicationMessenger applicationMessenger) {
		initialize(cacheName, applicationMessenger);
	}

	private void initialize(String cacheName, ApplicationMessenger anApplicationMessenger) {
		applicationMessenger = anApplicationMessenger;
		String sender = anApplicationMessenger.getSender();
		clusterCacheMessageEncoder = new ClusterCacheMessageEncoder(sender, cacheName);
	}


	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		// ignored
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementExpired(net.sf.ehcache.Cache, net.sf.ehcache.Element)
	 */
	public void notifyElementExpired(Cache arg0, Element arg1) {
		// map to remove
		notifyElementRemoved(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementPut(net.sf.ehcache.Cache, net.sf.ehcache.Element)
	 */
	public void notifyElementPut(Cache arg0, Element arg1) throws CacheException {
		// nothing to do, ignore
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementRemoved(net.sf.ehcache.Cache, net.sf.ehcache.Element)
	 */
	public void notifyElementRemoved(Cache arg0, Element arg1) throws CacheException {
		// map to removeElement
		applicationMessenger.sendMessage(clusterCacheMessageEncoder.notifyRemovedElement(arg1));
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementUpdated(net.sf.ehcache.Cache, net.sf.ehcache.Element)
	 */
	public void notifyElementUpdated(Cache arg0, Element arg1) throws CacheException {
		// map to remove
		notifyElementRemoved(arg0, arg1);
	}
}
