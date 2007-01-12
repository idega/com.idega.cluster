/*
 * $Id: ClusterCacheMapNotifier.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.cache.listener;

import com.idega.cluster.cache.encode.ClusterCacheMessageDecoder;
import com.idega.cluster.net.message.ApplicationMessenger;
import com.idega.cluster.net.message.MessageListener;
import com.idega.cluster.net.message.SimpleMessage;
import com.idega.cluster.net.message.impl.ReplyMessageStopper;
import com.idega.idegaweb.IWApplicationContext;


/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ClusterCacheMapNotifier implements MessageListener {
	
	public static void getInstanceAddedToIWCacheManager2(ApplicationMessenger applicationMessenger,IWApplicationContext iwac) {
		ReplyMessageStopper replyMessageStopper = new ReplyMessageStopper();
		ClusterCacheMapNotifier clusterCacheNotifier = new ClusterCacheMapNotifier(replyMessageStopper, iwac);
		applicationMessenger.addSendFilter(replyMessageStopper);
		applicationMessenger.addReceiveListener(clusterCacheNotifier);
	}

	private ClusterCacheMessageDecoder clusterCacheMessageDecoder = null;
	private ReplyMessageStopper replyMessageStopper = null;
	
	public ClusterCacheMapNotifier(ReplyMessageStopper replyMessageStopper,  IWApplicationContext iwac) {
		this.replyMessageStopper = replyMessageStopper;
		this.clusterCacheMessageDecoder = new ClusterCacheMessageDecoder(iwac);
	}

	/* (non-Javadoc)
	 * @see com.idega.cluster.net.message.MessageListener#gotMessage(com.idega.cluster.net.message.SimpleMessage)
	 */
	public void gotMessage(SimpleMessage simpleMessage) {
		// do action
		Runnable action = clusterCacheMessageDecoder.decode(simpleMessage);
		// something to do?
		if (action != null) {
			// prevent that this kind of mail is sent again when the action is executed
			replyMessageStopper.add(simpleMessage);
			action.run();
		}
	}
}
