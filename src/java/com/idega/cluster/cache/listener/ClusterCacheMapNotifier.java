/*
 * $Id: ClusterCacheMapNotifier.java,v 1.2 2007/01/20 21:53:43 thomas Exp $
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


/**
 * 
 *  Last modified: $Date: 2007/01/20 21:53:43 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ClusterCacheMapNotifier implements MessageListener {
	
	public static void getInstanceAddedToEhCache(ApplicationMessenger applicationMessenger) {
		ReplyMessageStopper replyMessageStopper = new ReplyMessageStopper();
		ClusterCacheMapNotifier clusterCacheNotifier = new ClusterCacheMapNotifier(replyMessageStopper);
		applicationMessenger.addSendFilter(replyMessageStopper);
		applicationMessenger.addReceiveListener(clusterCacheNotifier);
	}

	private ClusterCacheMessageDecoder clusterCacheMessageDecoder = null;
	private ReplyMessageStopper replyMessageStopper = null;
	
	public ClusterCacheMapNotifier(ReplyMessageStopper replyMessageStopper) {
		this.replyMessageStopper = replyMessageStopper;
		this.clusterCacheMessageDecoder = new ClusterCacheMessageDecoder();
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
			// does not need to be a thread, just run
			action.run();
		}
	}
}
