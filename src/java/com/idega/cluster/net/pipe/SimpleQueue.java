/*
 * $Id: SimpleQueue.java,v 1.1 2007/04/30 13:14:26 thomas Exp $
 * Created on Apr 26, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.pipe;

import java.util.LinkedList;


/**
 * 
 *  Last modified: $Date: 2007/04/30 13:14:26 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 * 
 * Simple wrapper to solve problem with atomar "isEmpty getFirst" operation
 * 
 */
public class SimpleQueue {
	
	private LinkedList myList = null;
	
	public SimpleQueue() {
		myList = new LinkedList();	
	}
	
	public synchronized void add(Object object) {
		myList.add(object);
	}
	
	public synchronized Object getFirst() {
		if (myList.isEmpty()) {
			return null;
		}
		return myList.removeFirst();
	}
	
	public synchronized void clear() {
		myList.clear();
		
	}
}
