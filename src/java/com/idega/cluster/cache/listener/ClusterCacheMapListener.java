/*
 * $Id: ClusterCacheMapListener.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import com.idega.cluster.cache.encode.ClusterCacheMessageEncoder;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.core.cache.CacheMapListener;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ClusterCacheMapListener implements CacheMapListener {
	
	private ApplicationMessenger applicationMessenger = null;
	private ClusterCacheMessageEncoder clusterCacheMessageEncoder = null;

	
	public ClusterCacheMapListener(String cacheName, ApplicationMessenger applicationMessenger) {
		initialize(cacheName, applicationMessenger);
	}

	private void initialize(String cacheName, ApplicationMessenger anApplicationMessenger) {
		applicationMessenger = anApplicationMessenger;
		String sender = anApplicationMessenger.getSender();
		clusterCacheMessageEncoder = new ClusterCacheMessageEncoder(sender, cacheName);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.cache.CacheMapListener#cleared()
	 */
	public void cleared() {
		applicationMessenger.sendMessage(clusterCacheMessageEncoder.cleared());
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.CacheMapListener#gotObject(java.lang.String, java.lang.Object)
	 */
	public void gotObject(Object key, Object object) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.CacheMapListener#putObject(java.lang.String, java.lang.Object)
	 */
	public void putObject(Object key, Object object) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.CacheMapListener#removedObject(java.lang.String)
	 */
	public void removedObject(Object key) {
		applicationMessenger.sendMessage(clusterCacheMessageEncoder.removedObject(key));
	}
}
