/*
 * $Id: MessageQueue.java,v 1.1 2007/04/30 13:14:26 thomas Exp $
 * Created on Apr 25, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.pipe;

import java.util.ArrayList;
import java.util.List;
import com.idega.cluster.net.config.JxtaConfigSettings;


/**
 *
 *  Last modified: $Date: 2007/04/30 13:14:26 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 * 
 * MessageQueue is a queue where Runnables can be added to be executed 
 * one after another by a single thread. The Runnables are
 * executed corresponding to the order they are added (first in, first out)
 * The thread stops running when the queue is empty but is notified when a Runnable is added.
 */
public class MessageQueue {
	
	protected  SimpleQueue runnables = null;
	protected List myWorkers = null;
	protected boolean go = true;
	
	public MessageQueue() {
		initialize();
	}
	
	public void initialize() {
		runnables = new SimpleQueue();
		myWorkers = new ArrayList();
		int k = 0;
		while (k++ < JxtaConfigSettings.NUMBER_WORKERS) {
			myWorkers.add(createWorker());
			//final int g = k; myWorkers.add(createWorker(g));
		}
	}
	
	public void add(Runnable runnable) {
		runnables.add(runnable);
		synchronized(myWorkers) {
			myWorkers.notifyAll();
			//System.out.println("notified");
		}
	}
	
	//private Thread createWorker(final int k) {
	private Thread createWorker() {
		Thread worker = new Thread() {
			public void run() {    
				//System.out.println("run");
				try {
					while (go) { 
						Runnable runnable = (Runnable) runnables.getFirst();
						if (runnable == null) {
							synchronized(myWorkers) {
								myWorkers.wait();
								//System.out.println("woke up " + k);
							}
						}
						else {
							runnable.run();
						}
					}
				}
				catch ( InterruptedException e){ 
					e.printStackTrace();
				}  
				// forget all others runnables
				runnables.clear();
				//System.out.println("Stopped");
			}
		};
		worker.setDaemon(true);
		worker.start();
		return worker;
	}
	
	public void destroy() {
		// do not try to stop with interrupt since there might be some waits and sleeps within the Runnables that are caught - threads might not stop!
		// better idea: let the threads  finish the current work, but do not execute any more Runnables
		go = false;
		// maybe it is waiting?
		synchronized(myWorkers) {
			myWorkers.notifyAll();
		}
	}

	// **************************************************************************************************************
	// testingcode
	// **************************************************************************************************************
	
	static private void test() {
		// adds Runnables to the queue. 
		// At the beginning adding is done faster than executing the items:
		// The working thread (myWorker) never waits for items in the queue.
		// After a while adding is getting slower than executing, the worker sometimes waits before continuing (see output "woke up").
		// At the end the worker waits every time after executing a Runnable for a new one.
		
		// see output:
		
		//		run
		//		notified
		//		woke up
		//		Run 0
		//		notified
		//		notified
		//		notified
		//		Run 1
		//		notified
		//		notified
		//		Run 2
		//		notified
		//		notified
		//		Run 3
		//		notified
		//		Run 4
		//		notified
		//		Run 5
		//		notified
		//		Run 6
		//		notified
		//		Run 7
		//		notified
		//		Run 8
		//		notified
		//		Run 9
		//		Run 10
		//		notified
		//		Run 11
		//		notified
		//		Run 12
		//		Run 13
		//		notified
		//		Run 14
		//		Run 15
		//		notified
		//		Run 16
		//		notified
		//		Run 17
		//		Run 18
		//		notified
		//		Run 19
		//		notified
		//		woke up
		//		Run 20
		//		notified
		//		woke up
		//		Run 21
		//		notified
		//		woke up
		//		Run 22
		//		notified
		//		woke up
		//		Run 23
		//		notified
		//		woke up
		//		Run 24
		//		notified
		//		woke up
		//		Run 25
		//		notified
		//		woke up
		//		Run 26
		//		notified
		//		woke up
		//		Run 27
		
		
		MessageQueue  messageQueue = new MessageQueue();
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(i);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			final int k = i;
			messageQueue.add(new Runnable() { 
				public void run() {System.out.println("Run "+k); 
					try {
						Thread.sleep(10);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		try {
			Thread.sleep(60000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static public void main(String[] args) {
		// for testing uncomment the System.out lines in initialize()
		test();
	}	
	
	
}
