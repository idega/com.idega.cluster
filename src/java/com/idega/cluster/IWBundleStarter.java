/*
 * $Id: IWBundleStarter.java,v 1.2 2006/12/11 15:48:29 thomas Exp $
 * Created on 3.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster;

import java.io.File;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;




/**
 * 
 *  Last modified: $Date: 2006/12/11 15:48:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class IWBundleStarter implements IWBundleStartable {

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		if (true) {
			return;
		}
		System.out.println("hello that's me"); 
		File home = new File("/home/thomas/workspaces/workspace_ePlatform_rvk_20061127/applications/reykjavik/target/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
		//File home = new File("/home/thomas/workspaces/targets/targetA3/reykjavik/idegaweb/bundles/com.idega.cluster.bundle/properties");
        try {
        	System.setProperty("JXTA_HOME", home.getCanonicalPath());
        }
        catch (Exception ex) {
        	System.out.println("Hello");
        }
    	//JxtaConfig.main(null);
		//SimpleJxtaApp.main(null);
    	//DiscoveryDemo.main(null);
        //PipeListener.main(null);
        PipeExample.main(null);
		
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		// TODO Auto-generated method stub
	}
}
